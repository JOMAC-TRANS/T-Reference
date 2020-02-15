package com.jomac.transcription.referencebuilder.queries;

import com.jomac.transcription.referencebuilder.Reference;
import com.jomac.transcription.referencebuilder.jpa.controllers.SpecificBeanJpaController;
import com.jomac.transcription.referencebuilder.jpa.models.SpecificBean;

public class SpecificQueries {

    SpecificBeanJpaController SPECIFIC_QUERY = new SpecificBeanJpaController(
            new Reference().getEMFactory());

    public boolean save(SpecificBean bean) {
        return SPECIFIC_QUERY.create(bean);
    }
}
