package com.jomac.transcription.referencebuilder.jpa.controllers;

import com.jomac.transcription.referencebuilder.jpa.models.DictatorBean;
import com.jomac.transcription.referencebuilder.jpa.models.DictatorWorkTypeBean;
import com.jomac.transcription.referencebuilder.jpa.models.WorkTypeBean;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

public class DictatorWorkTypeBeanJpaController implements Serializable {

    public DictatorWorkTypeBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean create(DictatorWorkTypeBean dictatorWorkTypeBean) {
        EntityManager em = null;
        boolean valid;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dictatorWorkTypeBean);
            em.getTransaction().commit();
            valid = true;
        } catch (Exception ex) {
            valid = false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return valid;
    }

    public DictatorWorkTypeBean findDWByDictatorAndWorkType(DictatorBean dictator, WorkTypeBean workType) {
        EntityManager em = getEntityManager();
//        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//            Root<DictatorWorkTypeBean> from = cq.from(DictatorWorkTypeBean.class);
//            cq.select(from);
//            Predicate predicate1 = em.getCriteriaBuilder().equal(from.get("dictatorid"), dictator.getDictatorid());
//            Predicate predicate2 = em.getCriteriaBuilder().equal(from.get("worktypeid"), workType.getWorktypeid());
//            cq.where(em.getCriteriaBuilder().and(predicate1, predicate2));
//            Query q = em.createQuery(cq);
//            for (Object obj : q.getResultList()){
//                System.out.println("obj: " + obj);
//            }
//            List<DictatorWorkTypeBean> result = q.getResultList();
//            return result.size() > 0 ? (DictatorWorkTypeBean) q.getResultList().get(0) : null;
//        } finally {
//            em.close();
//        }
        try {
            List<DictatorWorkTypeBean> results = em.createNamedQuery("DictatorWorkTypeBean.findByDictator&WorkTYpe")
                    .setParameter("dictatorid", dictator.getDictatorid()).setParameter("worktypeid", workType.getWorkTypeid()).getResultList();
            return results.size() > 0 ? results.get(0) : null;
        } finally {
            em.close();
        }
    }

    public List<DictatorWorkTypeBean> findDictatorWorkTypeBeanEntities() {
        return findDictatorWorkTypeBeanEntities(true, -1, -1);
    }

    public List<DictatorWorkTypeBean> findDictatorWorkTypeBeanEntities(int maxResults, int firstResult) {
        return findDictatorWorkTypeBeanEntities(false, maxResults, firstResult);
    }

    private List<DictatorWorkTypeBean> findDictatorWorkTypeBeanEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DictatorWorkTypeBean.class));
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

    public DictatorWorkTypeBean findDictatorWorkTypeBean(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DictatorWorkTypeBean.class, id);
        } finally {
            em.close();
        }
    }
}
