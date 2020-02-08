package com.jomac.transcription.reference.queries;

import com.jomac.transcription.reference.Reference;
import com.jomac.transcription.reference.jpa.controllers.DictatorBeanJpaController;
import com.jomac.transcription.reference.jpa.models.DictatorBean;
import java.util.List;

public class DictatorQueries {

    private DictatorBeanJpaController DICTATOR_CONTROLLER = new DictatorBeanJpaController(
            new Reference().getEMFactory());

    public List<DictatorBean> getAllDictator() {
        return DICTATOR_CONTROLLER.findDictatorBeanEntities();
    }

    public List<DictatorBean> getDictatorByLastName(String lastName) {
        return DICTATOR_CONTROLLER.getLNameDictator(lastName);
    }

    public List<DictatorBean> getDictatorByFirstName(String firstName) {
        return DICTATOR_CONTROLLER.getFNameDictator(firstName);
    }

    public List<DictatorBean> getDictatorByLastnFirstName(String lastName, String firstName) {
        return DICTATOR_CONTROLLER.getLFNameDictator(lastName, firstName);
    }

    public List<DictatorBean> getDictator(String name) {
        return DICTATOR_CONTROLLER.getDictator(name);
    }
//    public static void main(String[] args) {
//        DictatorQueries xx = new DictatorQueries();
//        for (DictatorBean xy : xx.getDictatorByLastnFirstName("Abalo", "tomas")) {
////        for (DictatorBean xy : xx.getDictatorByFirstName("to")){
//            System.out.println("DIC: " + xy);
//        }
//    }
}
