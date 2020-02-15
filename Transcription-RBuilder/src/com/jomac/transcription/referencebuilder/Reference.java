package com.jomac.transcription.referencebuilder;

import com.jomac.transcription.referencebuilder.database.connection.EngineConnector;
import com.jomac.transcription.referencebuilder.database.engine.H2;
import com.jomac.transcription.referencebuilder.database.engine.MySQL;
import java.util.ResourceBundle;

public class Reference extends EngineConnector {

    private final ResourceBundle bundle = Main.getResourceBundle();

    public Reference() {
        String engine = bundle.getString("db_engine");

        if (engine.equals("H2")) {
            setEngine(new H2());
        } else if (engine.equals("MySQL")) {
            setEngine(new MySQL());
        }
    }
}
