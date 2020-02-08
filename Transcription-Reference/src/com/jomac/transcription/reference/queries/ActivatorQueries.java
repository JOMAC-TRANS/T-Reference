package com.jomac.transcription.reference.queries;

import com.jomac.transcription.reference.Reference;
import com.jomac.transcription.reference.jpa.controllers.ActivatorBeanJpaController;
import com.jomac.transcription.reference.jpa.models.ActivatorBean;

public class ActivatorQueries {

    private final ActivatorBeanJpaController ACTIVATOR_CONTROLLER = new ActivatorBeanJpaController(
            new Reference().getEMFactory());

    public boolean save(ActivatorBean bean) {
        if (bean.getActivatorid() == null) {
            return ACTIVATOR_CONTROLLER.create(bean);
        } else {
            return ACTIVATOR_CONTROLLER.edit(bean);
        }
    }

    public ActivatorBean getActivatorBean() {
        return ACTIVATOR_CONTROLLER.getActivatorBean();
    }

    public boolean removeActivatorBean(ActivatorBean bean) {
        return ACTIVATOR_CONTROLLER.destroy(bean);
    }
}
