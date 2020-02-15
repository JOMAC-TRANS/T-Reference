package com.jomac.transcription.referencebuilder.jpa.controllers;

import com.jomac.transcription.referencebuilder.jpa.models.WorkTypeBean;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class WorkTypeBeanJpaController implements Serializable {

    public WorkTypeBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean create(WorkTypeBean worktypeBean) {
        EntityManager em = null;
        boolean valid;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(worktypeBean);
            em.getTransaction().commit();
            valid = true;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return valid;
    }

    public WorkTypeBean findWorkTypeByType(String workType) {
        EntityManager em = getEntityManager();
        try {
            List<WorkTypeBean> query = em.createNamedQuery("WorkTypeBean.findByWorktype")
                    .setParameter("worktype", workType).getResultList();
            if (query.isEmpty()) {
                return null;
            } else {
                return query.get(0);
            }
        } finally {
            em.close();
        }
    }
}
