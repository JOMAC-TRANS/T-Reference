package com.jomac.transcription.activator;

import com.jomac.transcription.activator.connections.EngineConnector;
import com.jomac.transcription.activator.engines.Postgre;

public class Activator extends EngineConnector {

    public Activator() {
        setEngine(Postgre.getInstance());
    }
}
