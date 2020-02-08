package com.jomac.transcription.activator.jpa.controllers;

import com.jomac.transcription.activator.jpa.models.RegistrationBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class RegistrationBeanJpaController implements Serializable {

    public RegistrationBeanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        if (emf != null) {
            emf.getCache().evictAll();
        }
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
            em.merge(registrationBean);
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
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("RegistrationBean.findAll");
        return q.getResultList();
    }

    public List<RegistrationBean> findAllRequest() throws Exception {
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("RegistrationBean.findAllRequest");

        return q.getResultList();
    }

    public List<RegistrationBean> findAllNewRequest() throws Exception {
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("RegistrationBean.findAllNewRequest");

        return q.getResultList();
    }

    public List<RegistrationBean> findAllActiveByQuery(String query) {
        EntityManager em = getEntityManager();
        Query q;
        if (query.length() == 1) {
            q = em.createNamedQuery("RegistrationBean.findAllActiveByPerson")
                    .setParameter("query", query + "%");
        } else {
            q = em.createNamedQuery("RegistrationBean.findAllActiveByQuery")
                    .setParameter("query", "%" + query + "%");
        }
        return q.getResultList();
    }

    public List<RegistrationBean> findRequestBy(boolean allRequest) {
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("RegistrationBean.findRequestBy")
                .setParameter("active", allRequest);

        return q.getResultList();
    }

    public List<RegistrationBean> findNearExpirtaion() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 10);
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("RegistrationBean.findByExpiration")
                .setParameter("expirationdate", c.getTime());

        return q.getResultList();
    }

    public List<RegistrationBean> findUpdateAccount() throws Exception {
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("RegistrationBean.findUpdateRequest");

        return q.getResultList();
    }

    public List<RegistrationBean> findAbandonAccounts() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -90);
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("RegistrationBean.findAbandonAccount")
                .setParameter("date1", c.getTime());

        return q.getResultList();

    }

    public List<RegistrationBean> findDuplicateAccounts() {
        EntityManager em = getEntityManager();
        RegistrationBean bean = new RegistrationBean();
        bean.getMachineid();
        Query q;
        q = em.createNativeQuery("SELECT registrationid FROM registrations as r "
                + "LEFT JOIN machines as m on r.machineid = m.machineid "
                + "LEFT JOIN persons as p on r.personid = p.personid "
                + "WHERE (p.email) IN "
                + "(SELECT email FROM persons GROUP BY email HAVING COUNT(email) > 1) OR (m.harddisk,m.motherboard) IN  "
                + "(SELECT harddisk,motherboard FROM machines GROUP BY harddisk,motherboard HAVING COUNT(harddisk) > 1)");

        List regIds = new ArrayList<>();
        regIds.addAll(q.getResultList());

        q = em.createNamedQuery("RegistrationBean.findAll");
        List<RegistrationBean> rBean = new ArrayList<>();
        for (Object obj : q.getResultList()) {
            RegistrationBean reg = (RegistrationBean) obj;
            for (Object id : regIds) {
                if (reg.getRegistrationid() == (int) id) {
                    rBean.add(reg);
                }
            }
        }
        return rBean;
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
