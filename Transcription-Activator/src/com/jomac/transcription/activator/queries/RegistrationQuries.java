package com.jomac.transcription.activator.queries;

import com.jomac.transcription.activator.Activator;
import com.jomac.transcription.activator.jpa.controllers.RegistrationBeanJpaController;
import com.jomac.transcription.activator.jpa.models.RegistrationBean;
import java.util.List;

public class RegistrationQuries {

    private final RegistrationBeanJpaController REGISTRATION_CONTROLLER = new RegistrationBeanJpaController(
            new Activator().getEMFactory());

    public boolean save(RegistrationBean bean) {
        if (bean.getRegistrationid() == null) {
            return REGISTRATION_CONTROLLER.create(bean);
        } else {
            return REGISTRATION_CONTROLLER.edit(bean);
        }
    }

    public List<RegistrationBean> getAllRequestBean(boolean all) throws Exception {
        return all ? REGISTRATION_CONTROLLER.findAllRequest()
                : REGISTRATION_CONTROLLER.findAllNewRequest();
    }

    public boolean removeActivatorBean(RegistrationBean bean) {
        return REGISTRATION_CONTROLLER.destroy(bean);
    }

    public RegistrationBean getRegistrationById(Integer registrationid) {
        return REGISTRATION_CONTROLLER.findRegistrationBean(registrationid);
    }

    public List<RegistrationBean> findAllApprovedAccount() throws Exception {
        return REGISTRATION_CONTROLLER.findRequestBy(true);
    }

    public List<RegistrationBean> findAllActiveByQuery(String query) throws Exception {
        return REGISTRATION_CONTROLLER.findAllActiveByQuery(query);
    }

    public List<RegistrationBean> findNeed2UpdateAccount() throws Exception {
        return REGISTRATION_CONTROLLER.findUpdateAccount();
    }

    public List<RegistrationBean> findExpiringAccounts() throws Exception {
        return REGISTRATION_CONTROLLER.findNearExpirtaion();
    }

    public List<RegistrationBean> findAllAbandonAccounts() throws Exception {
        return REGISTRATION_CONTROLLER.findAbandonAccounts();
    }

    public List<RegistrationBean> findDuplicateAccounts() throws Exception {
        return REGISTRATION_CONTROLLER.findDuplicateAccounts();
    }
}
