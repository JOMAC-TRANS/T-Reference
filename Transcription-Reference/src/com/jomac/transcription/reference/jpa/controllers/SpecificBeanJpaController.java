package com.jomac.transcription.reference.jpa.controllers;

import com.jomac.transcription.reference.jpa.models.SpecificBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 *
 * @author denrosssalenga
 */
public class SpecificBeanJpaController implements Serializable {

    public SpecificBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<SpecificBean> getQueryResults(Map filterMap) {
        int dictatorId = -1;
        String workType = "";
        EntityManager em = getEntityManager();
        Query q;

        if (filterMap.containsKey("dictatorid")) {
            dictatorId = Integer.parseInt(filterMap.get("dictatorid").toString());
        }
        if (filterMap.containsKey("worktype")) {
            workType = filterMap.get("worktype").toString();
        }

        if (dictatorId != -1 && !workType.isEmpty()) {
            q = em.createNamedQuery("SpecificBean.findByDictator&WorkType");
            q.setParameter("dictatorid", dictatorId);
            q.setParameter("worktype", workType);
        } else {
            q = em.createNamedQuery("SpecificBean.findByAccountSpecific");
        }

        try {
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public SpecificBean getSpecificResult(Map filterMap) {
        int dictatorId = -1;
        String workType = "";
        EntityManager em = getEntityManager();
        Query q;

        if (filterMap != null) {
            if (filterMap.containsKey("dictatorid")) {
                dictatorId = Integer.parseInt(filterMap.get("dictatorid").toString());
            }
            if (filterMap.containsKey("worktype")) {
                workType = filterMap.get("worktype").toString();
            }
        }

        if (dictatorId != -1 && !workType.isEmpty()) {
            q = em.createNamedQuery("SpecificBean.findByDictator&WorkType");
            q.setParameter("dictatorid", dictatorId);
            q.setParameter("worktype", workType);
        } else {
            q = em.createNamedQuery("SpecificBean.findByAccountSpecific");
        }

        try {
            return (SpecificBean) q.getSingleResult();
//            return (SpecificBean) q.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
