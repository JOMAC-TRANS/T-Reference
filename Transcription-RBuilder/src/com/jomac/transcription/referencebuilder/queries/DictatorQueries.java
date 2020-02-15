package com.jomac.transcription.referencebuilder.queries;

import com.jomac.transcription.referencebuilder.Reference;
import com.jomac.transcription.referencebuilder.jpa.controllers.DictatorBeanJpaController;
import com.jomac.transcription.referencebuilder.jpa.models.DictatorBean;

public class DictatorQueries {

    DictatorBeanJpaController DICTATOR_QUERY
            = new DictatorBeanJpaController(new Reference().getEMFactory());

    public boolean save(DictatorBean bean) {
        return DICTATOR_QUERY.create(bean);
    }

    public DictatorBean findDictator(String[] dictator) {
        return DICTATOR_QUERY.findDictator(dictator);
    }
}
