package com.jomac.transcription.reference.jpa.controllers;

import com.jomac.transcription.reference.jpa.models.RegistrationBean;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class RegistrationBeanJpaController implements Serializable {

    public RegistrationBeanJpaController(EntityManagerFactory emf) {
        if (emf != null) {
            emf.getCache().evictAll();
        }
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean create(RegistrationBean registrationBean) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(registrationBean);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean edit(RegistrationBean registrationBean) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            registrationBean = em.merge(registrationBean);
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

    public boolean destroy(RegistrationBean bean) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegistrationBean registrationBean = null;
            try {
                registrationBean = em.getReference(RegistrationBean.class, bean.getRegistrationid());
                registrationBean.getRegistrationid();
            } catch (EntityNotFoundException enfe) {
            }
            em.remove(registrationBean == null ? bean : registrationBean);
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

    public List<RegistrationBean> findRegistrationBeanEntities() {
        return findRegistrationBeanEntities(true, -1, -1);
    }

    public List<RegistrationBean> findRegistrationBeanEntities(int maxResults, int firstResult) {
        return findRegistrationBeanEntities(false, maxResults, firstResult);
    }

    private List<RegistrationBean> findRegistrationBeanEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RegistrationBean.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public RegistrationBean findRegistrationBean(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RegistrationBean.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegistrationBeanCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RegistrationBean> rt = cq.from(RegistrationBean.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
