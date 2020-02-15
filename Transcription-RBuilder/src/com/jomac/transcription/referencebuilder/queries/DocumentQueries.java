package com.jomac.transcription.referencebuilder.queries;

import com.jomac.transcription.referencebuilder.Reference;
import com.jomac.transcription.referencebuilder.jpa.controllers.DocumentBeanJpaController;
import com.jomac.transcription.referencebuilder.jpa.models.DocumentBean;

public class DocumentQueries {

    DocumentBeanJpaController DOCUMENT_QUERY
            = new DocumentBeanJpaController(new Reference().getEMFactory());

    public boolean save(DocumentBean bean) {
        return DOCUMENT_QUERY.create(bean);
    }
}
