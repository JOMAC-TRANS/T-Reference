package com.jomac.transcription.reference.queries;

import com.jomac.transcription.reference.Reference;
import com.jomac.transcription.reference.engines.Postgre;
import com.jomac.transcription.reference.jpa.controllers.RegistrationBeanJpaController;
import com.jomac.transcription.reference.jpa.models.RegistrationBean;

public class RegistrationQueries {

    private final RegistrationBeanJpaController REGISTRATION_CONTROLLER = new RegistrationBeanJpaController(
            new Reference(Reference.EngineType.POSTGRE).getEMFactory());

    public boolean save(RegistrationBean bean) {
        if (bean.getRegistrationid() == null) {
            return REGISTRATION_CONTROLLER.create(bean);
        } else {
            return REGISTRATION_CONTROLLER.edit(bean);
        }
    }

    public boolean removeActivatorBean(RegistrationBean bean) {
        return REGISTRATION_CONTROLLER.destroy(bean);
    }

    public RegistrationBean getRegistrationById(Integer registrationid) {
        return REGISTRATION_CONTROLLER.findRegistrationBean(registrationid);
    }

    public void closePostgreConnection() {
        Postgre.closeInstance();
    }
}
