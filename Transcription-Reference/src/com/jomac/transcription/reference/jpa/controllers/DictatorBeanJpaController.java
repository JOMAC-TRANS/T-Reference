package com.jomac.transcription.reference.jpa.controllers;

import com.jomac.transcription.reference.jpa.models.DictatorBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class DictatorBeanJpaController implements Serializable {

    public DictatorBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<DictatorBean> getLNameDictator(String lastName) {
        EntityManager em = getEntityManager();
        return em.createNamedQuery("DictatorBean.findByLastname")
                .setParameter("lastname", lastName.toLowerCase() + "%")
                .getResultList();
    }

    public List<DictatorBean> getFNameDictator(String firstName) {
        EntityManager em = getEntityManager();
        return em.createNamedQuery("DictatorBean.findByFirstname")
                .setParameter("firstname", firstName.toLowerCase() + "%")
                .getResultList();
    }

    public List<DictatorBean> getLFNameDictator(String lastName, String firstName) {
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("DictatorBean.findByFLname");
        q.setParameter("lastname", lastName.toLowerCase() + "%");
        q.setParameter("firstname", firstName.toLowerCase() + "%");
        List<DictatorBean> results = q.getResultList();
        return results.isEmpty() ? new ArrayList<DictatorBean>() : results;
    }

    public List<DictatorBean> getDictator(String name) {
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("DictatorBean.findDictator");
        q.setParameter("lastname", name.toLowerCase() + "%");
        q.setParameter("firstname", name.toLowerCase() + "%");
        List<DictatorBean> results = q.getResultList();
        return results.isEmpty() ? new ArrayList<DictatorBean>() : results;
    }

    public List<DictatorBean> findDictatorBeanEntities() {
        return findDictatorBeanEntities(true, -1, -1);
    }

    public List<DictatorBean> findDictatorBeanEntities(int maxResults, int firstResult) {
        return findDictatorBeanEntities(false, maxResults, firstResult);
    }

    private List<DictatorBean> findDictatorBeanEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DictatorBean.class));
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

    public int getDictatorBeanCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DictatorBean> rt = cq.from(DictatorBean.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public DictatorBean findDictatorBean(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DictatorBean.class, id);
        } finally {
            em.close();
        }
    }
}
