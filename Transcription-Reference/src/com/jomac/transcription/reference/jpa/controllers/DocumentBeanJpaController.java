package com.jomac.transcription.reference.jpa.controllers;

import com.jomac.transcription.reference.Main;
import com.jomac.transcription.reference.jpa.models.DocumentBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class DocumentBeanJpaController implements Serializable {

    private EntityManagerFactory emf = null;
    private String oQuery = "";

    public DocumentBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public DocumentBean findDocumentBean(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DocumentBean.class, id);
        } finally {
            em.close();
        }
    }

    public List<DocumentBean> getQueryResults(Map filterMap) {
        int dictatorId = -1, searchLimit = -1;
        String document = filterMap.get("document").toString().toLowerCase();
        String[] phraseList = document.split("[+]");
        boolean multiplePhrase = phraseList.length > 1;
        EntityManager em = getEntityManager();
        String workType = "";
        Query q = null;

        if (filterMap.containsKey("dictatorid")) {
            dictatorId = Integer.parseInt(filterMap.get("dictatorid").toString());
        }
        if (filterMap.containsKey("worktype")) {
            workType = filterMap.get("worktype").toString();
        }
        if (filterMap.containsKey("searchLimit")) {
            searchLimit = Integer.parseInt(filterMap.get("searchLimit").toString());
            System.out.println("searchLimit: " + searchLimit);
        }
        if (filterMap.containsKey("oQuery")) {
            oQuery = filterMap.get("oQuery").toString();
        }

        if (dictatorId != -1 || !workType.isEmpty()) {
            if (dictatorId != -1 && !workType.isEmpty()) {
                q = em.createNamedQuery(multiplePhrase
                        ? "DocumentBean.findByDictator&WorkType_"
                        : "DocumentBean.findByDictator&WorkType");
                q.setParameter("dictatorid", dictatorId);
                q.setParameter("worktype", workType);
            } else if (dictatorId != -1) {
                q = em.createNamedQuery(multiplePhrase
                        ? "DocumentBean.findByDictator_"
                        : "DocumentBean.findByDictator");
                q.setParameter("dictatorid", dictatorId);
            } else if (!workType.isEmpty()) {
                q = em.createNamedQuery(multiplePhrase
                        ? "DocumentBean.findByWorkType_"
                        : "DocumentBean.findByWorkType");
                q.setParameter("worktype", workType);
            }
        } else {
            q = em.createNamedQuery(multiplePhrase
                    ? "DocumentBean.findByDocument_"
                    : "DocumentBean.findByDocument");
        }

        if (multiplePhrase) {
            String temp = "";
            for (String x : phraseList) {
                if (x.trim().isEmpty()) {
                    continue;
                }
                temp = temp.isEmpty() ? x.trim() : temp + "%" + x.trim();
            }
            document = temp;
            temp = "";
            for (int i = (phraseList.length - 1); i >= 0; i--) {
                temp = temp.isEmpty() ? phraseList[i].trim() : temp + "%" + phraseList[i].trim();
            }
            q.setParameter("document2", "%" + temp.replaceAll("\"", "") + "%");
        }

        q.setParameter("document", "%" + document.replaceAll("\"", "") + "%");

        if (searchLimit != -1) {
            q.setMaxResults(searchLimit);
            searchLimit = searchLimit / 3;
        }

        List<DocumentBean> beanx;

        try {
            beanx = q.getResultList();
            int totalsize = beanx.size();
            System.out.println("total Results: " + totalsize);

            boolean trimSize = false;
            if (totalsize >= 47500) {
                trimSize = true;
                totalsize = totalsize / 3;
            }
            if (totalsize >= 37500) {
                trimSize = true;
                totalsize = totalsize / 2;
            }

            if (trimSize) {
                beanx.clear();
                beanx = q.setMaxResults(totalsize).getResultList();
            }

            System.gc();

            if (!oQuery.isEmpty()) {
                List<DocumentBean> newBeanList = new ArrayList<>();
                if (oQuery.contains("*")) {
                    List<String> wordList = new ArrayList<>();
                    Map<String, Integer> addressMap = new WeakHashMap<>();

                    for (DocumentBean bean : beanx) {
                        if (searchLimit != -1 && newBeanList.size() >= searchLimit) {
                            break;
                        }

                        if (multiplePhrase) {
                            String[] groupWords = oQuery.toLowerCase().split("[+]");
                            for (int i = 0; i < groupWords.length; i++) {
                                for (String str : getWCWordList(bean, groupWords[i].trim().toLowerCase())) {
                                    if (!wordList.contains(str)) {
                                        wordList.add(str);
                                        addressMap.put(str, i);
                                    }
                                }
                            }
                        } else {
                            for (String str : getWCWordList(bean, oQuery.toLowerCase())) {
                                if (!wordList.contains(str)) {
                                    wordList.add(str);
                                }
                            }
                        }

                        if (!wordList.isEmpty() && bean.getSearchCount() > 0) {
                            newBeanList.add(bean);
                        }
                    }

                    if (multiplePhrase) {
                        List<DocumentBean> bListToRemove = new ArrayList<DocumentBean>();
                        for (DocumentBean dBean : newBeanList) {
                            boolean removeBean;
                            if (dBean.getSearchCount() < phraseList.length) {
                                removeBean = true;
                            } else {
                                List<Integer> indexList = new ArrayList<Integer>();
                                for (String str : wordList) {
                                    int index = addressMap.get(str);

                                    if (dBean.getDocument().toLowerCase().contains(str.toLowerCase())
                                            && !indexList.contains(index)) {
                                        indexList.add(index);
                                    }
                                }
                                removeBean = (indexList.size() < phraseList.length);

                            }
                            if (removeBean && !bListToRemove.contains(dBean)) {
                                bListToRemove.add(dBean);
                            }
                        }
                        newBeanList.removeAll(bListToRemove);
                        bListToRemove.clear();
                    }
                    addressMap.clear();
                    wordList.clear();
                    beanx.clear();

                    return newBeanList;
                } else {
                    for (DocumentBean bean : beanx) {
                        if (multiplePhrase) {
                            for (String x : phraseList) {
                                highLightWords(bean, x.trim());
                            }
                        } else {
                            highLightWords(bean, oQuery.toLowerCase());
                        }
                        if (bean.getSearchCount() > 0) {
                            newBeanList.add(bean);
                            if (searchLimit != -1 && newBeanList.size() >= searchLimit) {
                                break;
                            }
                        }
                    }

                    beanx.clear();
                    return newBeanList;
                }
            }
        } catch (OutOfMemoryError e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    System.gc();
                    showMemoryDialog();
                }
            });
        }
        return new ArrayList<>();
    }

    private List<String> getWCWordList(DocumentBean bean, String searchPhrase) throws OutOfMemoryError {
        String documentContent = bean.getDocument().toLowerCase();
        String[] query2 = searchPhrase.split("[*]");
        List<String> wordList = null;

        try {
            if (searchPhrase.startsWith("\"") && searchPhrase.endsWith("\"")) {
                wordList = new ArrayList<String>();
                String[] tmpQuery2 = Pattern.compile("[*][1-5]").matcher(searchPhrase).find()
                        ? searchPhrase.split("[*][1-5]")
                        : searchPhrase.split("[*]");
                Pattern p = Pattern.compile(
                        ".*" + tmpQuery2[0].replaceAll("\"", "").trim()
                        + "(.*?)" + tmpQuery2[1].replaceAll("\"", "").trim() + ".*");

                if (!tmpQuery2[0].replaceAll("\"", "").trim().isEmpty()
                        && !tmpQuery2[1].replaceAll("\"", "").trim().isEmpty()) {
                    for (String sentence : documentContent.split("\\r")) {
                        if (sentence.trim().isEmpty()) {
                            continue;
                        }

                        Matcher m = p.matcher(sentence);
                        if (m.find()) {
                            String sContent = m.group(1);
                            if (sContent.trim().isEmpty()) {
                                continue;
                            }

                            boolean includeWords;
                            int wordCount = 0;
                            for (String x : sContent.trim().split(" ")) {
                                if (!x.trim().isEmpty()) {
                                    wordCount = wordCount + 1;
                                }
                            }

                            if (!sContent.startsWith(" ")) {
                                wordCount = wordCount - 1;
                            }
                            if (!sContent.endsWith(" ")) {
                                wordCount = wordCount - 1;
                            }
                            if (searchPhrase.contains("*5")) {
                                includeWords = (wordCount == 5);
                            } else if (searchPhrase.contains("*4")) {
                                includeWords = (wordCount == 4);
                            } else if (searchPhrase.contains("*3")) {
                                includeWords = (wordCount == 3);
                            } else if (searchPhrase.contains("*2")) {
                                includeWords = (wordCount == 2);
                            } else if (searchPhrase.contains("*1")) {
                                includeWords = (wordCount == 1);
                            } else {
                                includeWords = (wordCount <= 5);
                            }

                            String gWords = tmpQuery2[0].replaceAll("\"", "").trim() + sContent
                                    + tmpQuery2[1].replaceAll("\"", "").trim();

                            if (includeWords && !wordList.contains(gWords)) {
                                wordList.add(gWords);
                                highLightWords(bean, gWords);
                            }
                        }
                    }
                }

                //abc*def
            } else if (!searchPhrase.startsWith("*") && !searchPhrase.endsWith("*") && searchPhrase.contains("*")) {
                wordList = new ArrayList<String>();
                for (String word : documentContent.split("[\\n\\r\\s]")) {
                    word = word.replaceAll("[:.;,]", "");
                    if ((query2.length == 3 && word.length() >= searchPhrase.length() && word.contains(query2[1])
                            && word.matches("^" + query2[0] + ".+?" + query2[2] + "$"))
                            || (query2.length < 3 && word.matches("^" + query2[0] + ".+?" + query2[1] + "$"))) {
                        if (!wordList.contains(word)) {
                            wordList.add(word);
                            highLightWords(bean, word.replaceAll("[^\\w\\s'-]", ""));
                        }
                    }
                }
                //*abc*
            } else if (searchPhrase.startsWith("*") && searchPhrase.endsWith("*")) {
                wordList = new ArrayList<String>();
                for (String word : documentContent.split("[\\n\\r\\s]")) {
                    word = word.replaceAll("[:.;,]", "");
                    if (word.length() >= searchPhrase.length()
                            && (!word.startsWith(query2[1])
                            && !word.endsWith(query2[1]))
                            && word.contains(query2[1])) {
                        if (!wordList.contains(word)) {
                            wordList.add(word);
                            highLightWords(bean, word.replaceAll("[^\\w\\s'-]", ""));
                        }
                    }
                }
                //*abc || *abc*def
            } else if (searchPhrase.startsWith("*")) {
                wordList = new ArrayList<String>();
                for (String word : documentContent.split("[\\n\\r\\s]")) {
                    word = word.replaceAll("[:.;,]", "");
                    if (word.length() >= searchPhrase.length()
                            && ((query2.length == 2 && word.matches("\\w*" + query2[1] + "\\b"))
                            || (query2.length == 3 && word.contains(query2[1]) && word.endsWith(query2[2])))) {
                        if (!wordList.contains(word)) {
                            wordList.add(word);
                            highLightWords(bean, word.replaceAll("[^\\w\\s'-]", ""));
                        }
                    }
                }
                //abc*
            } else if (searchPhrase.endsWith("*")) {
                wordList = new ArrayList<String>();
                for (String word : documentContent.split("[\\n\\r\\s]")) {
                    word = word.replaceAll("[:.;,]", "");
                    if ((query2.length == 1 && word.matches(query2[0] + "(\\S+)\\s?"))
                            || (query2.length == 2 && word.matches(query2[0] + "(\\S+)\\s?")
                            && word.contains(query2[1])
                            && !word.endsWith(query2[1]))) {
                        if (!wordList.contains(word)) {
                            wordList.add(word);
                            highLightWords(bean, word.replaceAll("[^\\w\\s'-]", ""));
                        }
                    }
                }
            } else if (documentContent.contains(searchPhrase)) {
                wordList = new ArrayList<String>();
                wordList.add(searchPhrase);
                highLightWords(bean, searchPhrase);
            }

            if (wordList != null) {
                return wordList;
            } else {
                return new ArrayList<String>();
            }
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    private void highLightWords(DocumentBean bean, String query) throws OutOfMemoryError {
        try {
            String content = bean.getDocumentHTML();
            int counter = bean.getSearchCount();
            String word = "";

            if (content == null || content.isEmpty()) {
                content = bean.getDocument();
            }
            Pattern pattern = Pattern.compile("(?i)" + query.replace("*", ".*"));
            Matcher matcher = pattern.matcher(content);
            StringBuffer replacement = new StringBuffer();
            while (matcher.find()) {
                word = matcher.group();
                int endId1 = matcher.start() + 1,
                        endId2 = content.length();

                String wholeWord = content.substring(
                        content.substring(0, matcher.start()).lastIndexOf(" ") + 1,
                        (endId1 < endId2 && content.substring(endId1, endId2).indexOf(" ") != -1)
                        ? content.substring(matcher.start(), content.length()).indexOf(" ") + matcher.start()
                        : content.length());

                if (word.equalsIgnoreCase(query.toLowerCase()) && !wholeWord.startsWith("yellow\">")) {
                    matcher.appendReplacement(replacement,
                            "<font style=\"BACKGROUND-COLOR: yellow\">" + word
                            + "</font>");
                    counter++;
                }
            }
            matcher.appendTail(replacement);
            bean.setSearchCount(counter);
            bean.setCaretPosition(content.indexOf(word));
            bean.setDocumentHTML(replacement.toString());
        } catch (Exception ex) {
        } catch (OutOfMemoryError e) {
            throw e;
        }
    }

    private void showMemoryDialog() {
        JOptionPane.showMessageDialog(Main.getController().getMainFrame(),
                "Transcription Reference encounted a problem\n"
                + "Make sure to filter your search", "OutOfMemoryError",
                JOptionPane.ERROR_MESSAGE);
    }
}
