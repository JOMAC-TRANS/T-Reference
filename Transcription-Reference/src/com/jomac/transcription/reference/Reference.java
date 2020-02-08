package com.jomac.transcription.reference;

import com.jomac.transcription.reference.connections.EngineConnector;
import com.jomac.transcription.reference.engines.H2;
import com.jomac.transcription.reference.engines.MySQL;
import com.jomac.transcription.reference.engines.Postgre;

public class Reference extends EngineConnector {

    public enum EngineType {

        H2 {
                    @Override
                    public String toString() {
                        return "H2";
                    }
                },
        POSTGRE {
                    @Override
                    public String toString() {
                        return "Postgre";
                    }
                }
    }

    public Reference(EngineType val) {
        setSQLEngine(val.toString());
    }

    public Reference() {
        setSQLEngine(Main.getResourceBundle().getString("db_engine"));
    }

    private void setSQLEngine(String engine) {
        switch (engine) {
            case "H2":
                setEngine(H2.getInstance());
                break;
            case "Postgre":
                setEngine(Postgre.getInstance());
                break;
            case "MySQL":
                setEngine(new MySQL());
                break;
        }
    }
}
