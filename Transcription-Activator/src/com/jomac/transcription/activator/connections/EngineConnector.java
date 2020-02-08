package com.jomac.transcription.activator.connections;

import javax.persistence.EntityManagerFactory;

public abstract class EngineConnector {

    private IConnection iConnection;

    public void setEngine(IConnection iConnection) {
        this.iConnection = iConnection;
    }

    public EntityManagerFactory getEMFactory() {
        return iConnection.getInstanceEMFactory();
    }
}
