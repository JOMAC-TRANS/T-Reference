package com.jomac.transcription.reference.jpa.controllers;

import com.jomac.transcription.reference.jpa.models.ActivatorBean;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ActivatorBeanJpaController implements Serializable {

    public ActivatorBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean create(ActivatorBean activatorBean) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(activatorBean);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean edit(ActivatorBean activatorBean) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(activatorBean);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean destroy(ActivatorBean bean) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ActivatorBean activatorBean = null;
            try {
                activatorBean = em.getReference(ActivatorBean.class, bean.getActivatorid());
                activatorBean.getActivatorid();
            } catch (Exception e) {
            }
            em.remove(activatorBean == null ? bean : activatorBean);
            em.getTransaction().commit();
            return true;

        } catch (Exception ex) {
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public ActivatorBean getActivatorBean() {
        EntityManager em = getEntityManager();
        List<ActivatorBean> list = em.createNamedQuery("ActivatorBean.findAll").getResultList();
        return list.isEmpty() ? null : list.get(0);
    }
}
