package com.jomac.transcription.reference.queries;

import com.jomac.transcription.reference.Reference;
import com.jomac.transcription.reference.jpa.controllers.SchemaVersionBeanJpaController;
import com.jomac.transcription.reference.jpa.models.SchemaVersionBean;

public class DBQueries {

    public String getSchemaVersion() {
        SchemaVersionBeanJpaController SCHEMA_CONTROLLER
                = new SchemaVersionBeanJpaController(new Reference().getEMFactory());
        try {
            return SCHEMA_CONTROLLER.getSchemaVersionBean().getVersion();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String validateSchemaVersion(String dbName, String version) {
        SchemaVersionBean schema;
        Reference reference = new Reference();
        StringBuilder errorLog = new StringBuilder();
        SchemaVersionBeanJpaController SCHEMA_CONTROLLER
                = new SchemaVersionBeanJpaController(reference.getEMFactory());
        try {
            schema = SCHEMA_CONTROLLER.getSchemaVersionBean();
            if (Integer.parseInt(schema.getVersion()) < Integer.parseInt(version)) {
                errorLog.append("Invalid Plugin Version\n");
            } else if (!dbName.equalsIgnoreCase(schema.getDbname())) {
                errorLog.append("Invalid Plugin Name\n");
            }
        } catch (Exception e) {
            if (e.toString().contains("JdbcSQLException: File corrupted ")) {
                errorLog.append("Plugin file is corrupted\n");
            } else {
                errorLog.append("Invalid Plugin\n");
                e.printStackTrace();
            }
        }

        return errorLog.toString();
    }
//    public static void main(String args[]) {
//
//        DBQueries h2 = new DBQueries();
//        h2.validateSchemaVersion("July2012");
//        System.out.println("expired?" + h2.isExpired());
//    }
}
