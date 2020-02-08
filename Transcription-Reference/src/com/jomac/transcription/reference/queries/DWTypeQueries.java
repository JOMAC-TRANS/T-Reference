package com.jomac.transcription.reference.queries;

import com.jomac.transcription.reference.Reference;
import com.jomac.transcription.reference.jpa.controllers.DictatorWorktypeBeanJpaController;
import com.jomac.transcription.reference.jpa.models.DictatorBean;
import java.util.List;

public class DWTypeQueries {

    private DictatorWorktypeBeanJpaController DWTYPE_CONTROLLER = new DictatorWorktypeBeanJpaController(
            new Reference().getEMFactory());

    public List<Integer> findWorkTypeByDictator(DictatorBean bean) {
        return DWTYPE_CONTROLLER.findWorkTypeByDictator(bean);
    }
}
