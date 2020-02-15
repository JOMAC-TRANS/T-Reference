package com.jomac.transcription.referencebuilder.jpa.controllers;

import com.jomac.transcription.referencebuilder.jpa.models.DocumentBean;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class DocumentBeanJpaController implements Serializable {

    public DocumentBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean create(DocumentBean documentBean) {
        boolean valid;
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(documentBean);
            em.getTransaction().commit();
            valid = true;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return valid;
    }
}
