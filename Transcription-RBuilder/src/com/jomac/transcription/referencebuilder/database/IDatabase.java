package com.jomac.transcription.referencebuilder.database;

import java.sql.Connection;
import javax.persistence.EntityManagerFactory;

public interface IDatabase {

    public EntityManagerFactory getEMFactory();

    public void createDB() throws Exception;

    public boolean deleteDB();

    public void cleanUp();

    public void zipDB();

    public Connection getConnection() throws Exception;

    public void closeConnection();
}
