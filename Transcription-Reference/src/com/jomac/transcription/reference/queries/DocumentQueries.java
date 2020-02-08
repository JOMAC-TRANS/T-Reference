package com.jomac.transcription.reference.queries;

import com.jomac.transcription.reference.Reference;
import com.jomac.transcription.reference.jpa.controllers.DocumentBeanJpaController;
import com.jomac.transcription.reference.jpa.models.DocumentBean;
import java.util.List;
import java.util.Map;

public class DocumentQueries {

    private DocumentBeanJpaController DOCUMENT_CONTROLLER = new DocumentBeanJpaController(
            new Reference().getEMFactory());

    public List<DocumentBean> getQueryResults(Map filterMap) {
        return DOCUMENT_CONTROLLER.getQueryResults(filterMap);
    }
}
