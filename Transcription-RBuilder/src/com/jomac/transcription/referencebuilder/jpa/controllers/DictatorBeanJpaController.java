package com.jomac.transcription.referencebuilder.jpa.controllers;

import com.jomac.transcription.referencebuilder.jpa.models.DictatorBean;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DictatorBeanJpaController implements Serializable {

    public DictatorBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean create(DictatorBean dictatorBean) {
        EntityManager em = null;
        boolean valid;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dictatorBean);
            em.getTransaction().commit();
            valid = true;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return valid;
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

    public DictatorBean findDictatorBean(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DictatorBean.class, id);
        } finally {
            em.close();
        }
    }

    public DictatorBean findDictator(String[] dictator) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();
            Root<DictatorBean> from = cq.from(DictatorBean.class);
            CriteriaQuery<DictatorBean> select = cq.select(from);

            if (dictator.length == 1) {
                select.where(cb.equal(from.get("lastname"), dictator[0]));
            } else {
                Predicate predicate1 = cb.equal(from.get("lastname"), dictator[0]);
                Predicate predicate2 = cb.equal(from.get("firstname"), dictator[1]);
                if (dictator.length > 2) {
                    Predicate predicate3 = cb.equal(from.get("middlename"), dictator[2]);
                    select.where(cb.and(predicate1, predicate2, predicate3));
                } else {
                    select.where(cb.and(predicate1, predicate2));
                }
            }
            Query q = em.createQuery(cq);
            List<DictatorBean> result = q.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }
}
