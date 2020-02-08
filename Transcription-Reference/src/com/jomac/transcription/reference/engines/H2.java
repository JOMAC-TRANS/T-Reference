package com.jomac.transcription.reference.engines;

import com.jomac.transcription.reference.Main;
import com.jomac.transcription.reference.connections.IConnection;
import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class H2 implements IConnection {

    private EntityManagerFactory emf;
    private final String DB_NAME;
    private final String DB_PATH;

    public H2() {
        DB_NAME = Main.getDBAccount();
        DB_PATH = Main.getDBPath().concat(File.separator).concat(DB_NAME);

        System.out.println("connecting to " + DB_NAME);

        Map<String, String> properties = new WeakHashMap<>();
        properties.put("javax.persistence.jdbc.url",
                "jdbc:h2:" + DB_PATH + ";IFEXISTS=TRUE");
        properties.put("javax.persistence.jdbc.user", Main.getResourceBundle().getString("db_user"));
        properties.put("javax.persistence.jdbc.password", Main.getResourceBundle().getString("db_pass"));
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("eclipselink.logging.level", "OFF");

        emf = Persistence.createEntityManagerFactory("Transcription-ReferencePU", properties);
    }

    public static H2 getInstance() {
        return H2.EMFactoryHolder.getH2();
    }

    public static void closeInstance() {
        H2.EMFactoryHolder.closeConnection();
    }

    //<editor-fold defaultstate="collapsed" desc="FactoryHolder">
    private static class EMFactoryHolder {

        private static H2 INSTANCE = new H2();

        public static H2 getH2() {
            if (INSTANCE == null) {
                INSTANCE = new H2();
            }
            return INSTANCE;
        }

        public static void closeConnection() {
            if (INSTANCE != null) {
                INSTANCE.closeConn();
                INSTANCE = null;
            }
        }

        public static EntityManagerFactory getEMFactory() {
            return INSTANCE.getEMF();
        }
    }
    //</editor-fold>

    private EntityManagerFactory getEMF() {
        return emf;
    }

    private void closeConn() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }

    @Override
    public EntityManagerFactory getInstanceEMFactory() {
        return H2.EMFactoryHolder.getEMFactory();

    }
}
