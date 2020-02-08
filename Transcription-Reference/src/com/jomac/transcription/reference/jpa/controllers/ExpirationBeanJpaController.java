package com.jomac.transcription.reference.jpa.controllers;

import com.jomac.transcription.reference.jpa.models.ExpirationBean;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

public class ExpirationBeanJpaController implements Serializable {

    public ExpirationBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean create(ExpirationBean expirationBean) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(expirationBean);
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

    public boolean edit(ExpirationBean expirationBean) {
        EntityManager em = null;
        boolean valid;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(expirationBean);
            em.getTransaction().commit();
            valid = true;
        } catch (Exception ex) {
            valid = false;
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Date id = expirationBean.getExpirationdate();
                if (findExpirationBean(id) == null) {
                    System.out.println("The expirationBean with id " + id + " no longer exists.");
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return valid;
    }

    public boolean destroy(Date id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExpirationBean expirationBean;
            try {
                expirationBean = em.getReference(ExpirationBean.class, id);
                expirationBean.getExpirationdate();
            } catch (EntityNotFoundException enfe) {
                return false;
            }
            em.remove(expirationBean);
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

    public ExpirationBean getExpirationBean() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExpirationBean.class));
            Query q = em.createQuery(cq);
            return q.getResultList().isEmpty() ? null : (ExpirationBean) q.getSingleResult();
        } catch (Exception ex) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public ExpirationBean findExpirationBean(Date id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExpirationBean.class, id);
        } finally {
            em.close();
        }
    }
}
