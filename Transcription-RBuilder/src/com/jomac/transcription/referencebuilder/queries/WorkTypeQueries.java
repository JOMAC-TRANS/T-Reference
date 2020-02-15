package com.jomac.transcription.referencebuilder.queries;

import com.jomac.transcription.referencebuilder.Reference;
import com.jomac.transcription.referencebuilder.jpa.controllers.WorkTypeBeanJpaController;
import com.jomac.transcription.referencebuilder.jpa.models.WorkTypeBean;

public class WorkTypeQueries {

    WorkTypeBeanJpaController WORKTYPE_QUERY
            = new WorkTypeBeanJpaController(new Reference().getEMFactory());

    public boolean save(WorkTypeBean bean) {
        return WORKTYPE_QUERY.create(bean);
    }

    public WorkTypeBean findWorkType(String workType) {
        return WORKTYPE_QUERY.findWorkTypeByType(workType);
    }

    public static void main(String... args) throws Exception {
        WorkTypeQueries wq = new WorkTypeQueries();
        WorkTypeBean bean = wq.findWorkType("13");
        if (bean == null) {
            System.out.println("null");
        } else {
            System.out.println("not null");
        }
    }
}
