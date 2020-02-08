package com.jomac.transcription.reference.connections;

import javax.persistence.EntityManagerFactory;

public interface IConnection {

    public EntityManagerFactory getInstanceEMFactory();
}
