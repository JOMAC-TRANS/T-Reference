package com.jomac.transcription.activator.connections;

import javax.persistence.EntityManagerFactory;

public interface IConnection {

    public EntityManagerFactory getInstanceEMFactory();
}
