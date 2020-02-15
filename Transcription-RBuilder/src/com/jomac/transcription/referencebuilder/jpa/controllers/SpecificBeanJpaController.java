package com.jomac.transcription.referencebuilder.jpa.controllers;

import com.jomac.transcription.referencebuilder.jpa.models.SpecificBean;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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

    public boolean create(SpecificBean specificBean) {
        EntityManager em = null;
        boolean valid;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(specificBean);
            em.getTransaction().commit();
            valid = true;
        } catch (Exception e) {
            valid = false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return valid;
    }
}
