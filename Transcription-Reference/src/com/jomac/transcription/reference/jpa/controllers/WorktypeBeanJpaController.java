package com.jomac.transcription.reference.jpa.controllers;

import com.jomac.transcription.reference.jpa.models.WorktypeBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class WorktypeBeanJpaController implements Serializable {

    public WorktypeBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Integer> getWorkTypeList() {
        EntityManager em = getEntityManager();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();
            Root<WorktypeBean> from = cq.from(WorktypeBean.class);
            cq.select(cb.array(from.get("worktype")));
            Query q = em.createQuery(cq);
            List<Integer> workTypes = new ArrayList<Integer>();
            for (Object xx : q.getResultList()) {
                workTypes.add(Integer.parseInt(xx.toString()));
            }
            Collections.sort(workTypes);
            return workTypes;
        } finally {
            em.close();
        }
    }
}
