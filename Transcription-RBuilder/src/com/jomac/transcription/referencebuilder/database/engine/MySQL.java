package com.jomac.transcription.referencebuilder.database.engine;

import com.jomac.transcription.referencebuilder.database.IDatabase;
import java.sql.Connection;
import javax.persistence.EntityManagerFactory;

public class MySQL implements IDatabase {

    public EntityManagerFactory getEMFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void createDB() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean deleteDB() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void cleanUp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Connection getConnection() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void closeConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void zipDB() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
