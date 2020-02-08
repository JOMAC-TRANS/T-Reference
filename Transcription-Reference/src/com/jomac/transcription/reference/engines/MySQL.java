package com.jomac.transcription.reference.engines;

import com.jomac.transcription.reference.connections.IConnection;
import javax.persistence.EntityManagerFactory;

public class MySQL implements IConnection {

    @Override
    public EntityManagerFactory getInstanceEMFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
