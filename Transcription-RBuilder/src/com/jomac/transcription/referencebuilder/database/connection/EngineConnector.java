package com.jomac.transcription.referencebuilder.database.connection;

import com.jomac.transcription.referencebuilder.database.IDatabase;
import javax.persistence.EntityManagerFactory;

public abstract class EngineConnector {

    private IDatabase iDatabase;

    public void setEngine(IDatabase database) {
        iDatabase = database;
    }

    public void createDB() throws Exception {
        iDatabase.createDB();
    }

    public void zipDB() {
        iDatabase.zipDB();
    }

    public boolean deleteDB() {
        return iDatabase.deleteDB();
    }

    public void cleanUp() {
        iDatabase.cleanUp();
    }

    public EntityManagerFactory getEMFactory() {
        return iDatabase.getEMFactory();
    }
}
