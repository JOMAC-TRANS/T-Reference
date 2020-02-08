package com.jomac.transcription.reference.jpa.controllers;

import com.jomac.transcription.reference.jpa.models.SchemaVersionBean;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

public class SchemaVersionBeanJpaController implements Serializable {

    public SchemaVersionBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public SchemaVersionBean getSchemaVersionBean() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SchemaVersionBean.class));
            Query q = em.createQuery(cq);
            return q.getResultList().isEmpty() ? null : (SchemaVersionBean) q.getSingleResult();
        } finally {
            em.close();
        }
    }
}
