package com.jomac.transcription.reference.jpa.controllers;

import com.jomac.transcription.reference.jpa.models.DictatorBean;
import com.jomac.transcription.reference.jpa.models.DictatorWorktypeBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class DictatorWorktypeBeanJpaController implements Serializable {

    public DictatorWorktypeBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Integer> findWorkTypeByDictator(DictatorBean bean) {
        EntityManager em = getEntityManager();
        try {
            List<DictatorWorktypeBean> dwBean = em.createNamedQuery("DictatorWorktypeBean.findWorkTypeByDictator")
                    .setParameter("dictatorid", bean).getResultList();
            List<Integer> workTypes = new ArrayList<Integer>();
            for (DictatorWorktypeBean obj : dwBean) {
                workTypes.add(Integer.parseInt(obj.getWorktypeid().getWorktype()));
            }
            Collections.sort(workTypes);
            return workTypes;
        } finally {
            em.close();
        }
    }

    public DictatorWorktypeBean findDictatorWorktypeBean(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DictatorWorktypeBean.class, id);
        } finally {
            em.close();
        }
    }

    public int getDictatorWorktypeBeanCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DictatorWorktypeBean> rt = cq.from(DictatorWorktypeBean.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
