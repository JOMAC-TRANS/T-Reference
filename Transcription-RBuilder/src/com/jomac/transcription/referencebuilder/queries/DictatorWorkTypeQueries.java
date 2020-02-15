package com.jomac.transcription.referencebuilder.queries;

import com.jomac.transcription.referencebuilder.Reference;
import com.jomac.transcription.referencebuilder.jpa.controllers.DictatorWorkTypeBeanJpaController;
import com.jomac.transcription.referencebuilder.jpa.models.DictatorBean;
import com.jomac.transcription.referencebuilder.jpa.models.DictatorWorkTypeBean;
import com.jomac.transcription.referencebuilder.jpa.models.WorkTypeBean;

public class DictatorWorkTypeQueries {

    private final DictatorWorkTypeBeanJpaController DICTATOR_WORKTYP_CONTROLLER
            = new DictatorWorkTypeBeanJpaController(new Reference().getEMFactory());

    public boolean save(DictatorWorkTypeBean bean) {
        return DICTATOR_WORKTYP_CONTROLLER.create(bean);
    }

    public DictatorWorkTypeBean findDWByDictatorAndWorkType(DictatorBean dictator, WorkTypeBean workType) {
        return DICTATOR_WORKTYP_CONTROLLER.findDWByDictatorAndWorkType(dictator, workType);
    }

    public static void main(String[] args) {
        DictatorWorkTypeQueries dwq = new DictatorWorkTypeQueries();
        DictatorBean dbean = new DictatorBean();
        WorkTypeBean wbean = new WorkTypeBean();
        dbean.setDictatorid(1);
        wbean.setWorkTypeid(1);
        DictatorWorkTypeBean dwBean = dwq.findDWByDictatorAndWorkType(dbean, wbean);
        if (dwBean == null) {
            System.out.println("null!");
        } else {
            System.out.println("not null");
        }
    }
}
