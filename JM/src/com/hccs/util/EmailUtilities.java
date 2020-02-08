package com.hccs.util;

import com.sun.mail.imap.protocol.FLAGS;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

public class EmailUtilities {

    private String smtpServer, emailAddress, emailPassword, emailPort;

    private enum EFlag {

        READ,
        UNREAD,
        ALL,
        DELETED
    }

    private enum EFolder {

        INBOX {
            @Override
            public String toString() {
                return "Inbox";
            }
        },
        SENT {
            @Override
            public String toString() {
                return "[Gmail]/Sent Mail";
            }
        },
        TRASH {
            @Override
            public String toString() {
                return "[Gmail]/Trash";
            }
        }
    }

    public EmailUtilities(String smtpServer, String emailAddress, String emailPassword, String emailPort) {
        this.smtpServer = smtpServer;
        this.emailAddress = emailAddress;
        this.emailPassword = emailPassword;
        this.emailPort = emailPort;
    }

    public boolean sendMail(
            String to,
            String cc,
            String bcc,
            String subject,
            String body) {

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "false"); // added this line
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", smtpServer);
        props.put("mail.smtp.starttls.required", "false"); // added this line

        try {
            return sendEmail(to, cc, bcc, subject, body, false, props);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendGMail(
            String to,
            String cc,
            String bcc,
            String subject,
            String body,
            boolean htmlFormat) throws Exception {

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", smtpServer);
        props.put("mail.smtp.starttls.required", "true");

        return sendEmail(to, cc, bcc, subject, body, htmlFormat, props);
    }

    private boolean sendEmail(
            String to,
            String cc,
            String bcc,
            String subject,
            String body,
            boolean htmlFormat,
            Properties props) throws Exception {
        Session session = Session.getInstance(props, null);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(emailAddress));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

        if (cc != null && !cc.isEmpty()) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
        }

        if (bcc != null && !bcc.isEmpty()) {
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
        }

        msg.setSubject(subject);
        if (htmlFormat) {
            msg.setContent(body, "text/html");
        } else {
            msg.setText(body);
        }
        msg.setHeader("X-Mailer", "LOTONtechEmail");
        msg.setSentDate(new java.util.Date());
        Transport transport = session.getTransport("smtp");
        transport.connect(smtpServer, Integer.valueOf(emailPort), emailAddress, emailPassword);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();

        return true;
    }

    public boolean sendMail(
            String to,
            String subject,
            String body) {
        return sendMail(to, null, null, subject, body);
    }

    //Used in TransRef Registration
    public boolean sendGMail(
            String to,
            String subject,
            String body) throws Exception {
        return sendGMail(to, null, null, subject, body, false);
    }

    //Used in TransRef Activator
    public boolean sendGMail(
            String to,
            String subject,
            String body,
            boolean htmlFormat) throws Exception {
        return sendGMail(to, null, null, subject, body, htmlFormat);
    }

    public List<Message> getROUnreadEmails() throws Exception {
        return getEmails(EFolder.INBOX, EFlag.UNREAD, true);
    }

    public List<Message> getUnreadEmails() throws Exception {
        return getEmails(EFolder.INBOX, EFlag.UNREAD, false);
    }

    public List<Message> getReadEmails() throws Exception {
        return getEmails(EFolder.INBOX, EFlag.READ, false);
    }

    public List<Message> getAllEmails() throws Exception {
        return getEmails(EFolder.INBOX, EFlag.ALL, true);
    }

    public List<Message> getAllSentItems() throws Exception {
        return getEmails(EFolder.SENT, EFlag.READ, true);
    }

    public List<Message> getTrashItems() throws Exception {
        return getEmails(EFolder.TRASH, EFlag.ALL, true);
    }

    private List<Message> getEmails(EFolder eF, EFlag flag, boolean readOnly) throws Exception {
        Message messages[] = null;
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        props.put("mail.imaps.ssl.trust", "*");

        Session session = Session.getDefaultInstance(props);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", emailAddress, emailPassword);

        Folder folder = store.getFolder(eF.toString());

        if (readOnly) {
            folder.open(Folder.READ_ONLY);
        } else {
            folder.open(Folder.READ_WRITE);
        }

        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.CONTENT_INFO);

        if (flag == EFlag.ALL) {
            messages = folder.getMessages();
            folder.fetch(messages, fp);
        } else {
            if (flag == EFlag.UNREAD) {
                messages = folder.search(new FlagTerm(new Flags(FLAGS.Flag.SEEN), false));
            } else if (flag == EFlag.READ) {
                messages = folder.search(new FlagTerm(new Flags(FLAGS.Flag.SEEN), true));
            }
            folder.fetch(messages, fp);
        }
        return Arrays.asList(messages);
    }

    public void openEmail(Message msg) {
        setEmailFlag(msg, false, false);
    }

    public void deleteEmail(Message msg) {
        setEmailFlag(msg, true, false);
    }

    public void deleteEmailPermanent(Message msg) {
        setEmailFlag(msg, true, true);
    }

    private void setEmailFlag(Message msg, boolean deleteMsg, boolean permanent) {
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", emailAddress, emailPassword);
            Folder folder = store.getFolder(permanent ? EFolder.TRASH.toString() : EFolder.INBOX.toString());
            folder.open(Folder.READ_WRITE);
            Message msg2 = folder.getMessage(msg.getMessageNumber());
            msg2.getContent();

            if (permanent) {
                msg2.setFlag(Flags.Flag.DELETED, true);
                folder.close(true);
            } else {
                if (deleteMsg) {
                    Folder trash = store.getFolder(EFolder.TRASH.toString());
                    folder.copyMessages(new Message[]{msg2}, trash);
                }
                folder.close(false);
            }

        } catch (Exception ex) {
        }
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(String emailPort) {
        this.emailPort = emailPort;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public static void main(String[] args) {
        EmailUtilities eu = new EmailUtilities("smtp.gmail.com", "jomactranscription", "xxPasswordHerexx", "587");
//        eu.sendGMail("jomactranscription", new java.util.Date().toString(), "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG");
        List<Message> mails;
        try {
            mails = eu.getUnreadEmails();
        } catch (Exception e) {
            if (e.toString().toLowerCase().contains("javax.mail.MessagingException")) {
                System.out.println("no connection!");
            }
            return;
        }
        System.out.println("count: " + mails.size());
        try {
            for (Message ms : mails) {
                System.out.println("\n" + ms.getSubject());
                Object content = ms.getContent();
                if (content instanceof String) {
                    System.out.println("content: " + content);
                } else if (content instanceof Multipart) {
//                    Multipart mp = (Multipart) content;
//                    for (int x = 0; x < mp.getCount(); x++) {
//                        System.out.println("mpContent: " + mp.getBodyPart(x).getContent());
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
