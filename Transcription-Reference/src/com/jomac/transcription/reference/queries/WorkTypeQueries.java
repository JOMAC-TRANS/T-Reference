package com.jomac.transcription.reference.queries;

import com.jomac.transcription.reference.Reference;
import com.jomac.transcription.reference.jpa.controllers.WorktypeBeanJpaController;
import java.util.List;

public class WorkTypeQueries {

    private WorktypeBeanJpaController REFERENCE_CONTROLLER = new WorktypeBeanJpaController(
            new Reference().getEMFactory());

    public List<Integer> getWorkTypeList() {
        return REFERENCE_CONTROLLER.getWorkTypeList();
    }
//    public static void main(String[] args) {
//        WorkTypeQueries xx = new WorkTypeQueries();
//        for (Object yy : xx.getWorkTypeList()) {
//            System.out.println("TYPE: " + yy);
//        }
//        System.exit(0);
//    }
}
