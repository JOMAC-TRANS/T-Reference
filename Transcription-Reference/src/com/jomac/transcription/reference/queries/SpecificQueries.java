package com.jomac.transcription.reference.queries;

import com.jomac.transcription.reference.Reference;
import com.jomac.transcription.reference.jpa.controllers.SpecificBeanJpaController;
import com.jomac.transcription.reference.jpa.models.SpecificBean;
import java.util.List;
import java.util.Map;

public class SpecificQueries {

    SpecificBeanJpaController SPECIFIC_QUERY = new SpecificBeanJpaController(
            new Reference().getEMFactory());

    public List<SpecificBean> getQueryResults(Map filterMap) {
        return SPECIFIC_QUERY.getQueryResults(filterMap);
    }

    public SpecificBean getSpecific(Map filterMap) {
        return SPECIFIC_QUERY.getSpecificResult(filterMap);
    }
}
