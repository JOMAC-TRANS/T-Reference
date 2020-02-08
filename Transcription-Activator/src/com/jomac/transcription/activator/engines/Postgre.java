package com.jomac.transcription.activator.engines;

import com.jomac.transcription.activator.connections.IConnection;
import java.util.Map;
import java.util.WeakHashMap;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Postgre implements IConnection {

//    private final String host = "127.0.0.1";
//    private final String port = "5433";
//    private final String dbname = "transdb";;
//    private final String user = "postgres";
//    private final String pass = "postgres";
//    private final String sslString = "";

//    private final String host = "208.122.118.27";
//    private final String port = "5433";
//    private final String user = "transref";
//    private final String pass = "j0M@cTr@ns";
    
    private final String host = "ec2-54-235-196-250.compute-1.amazonaws.com";
    private final String port = "5432";
    private final String dbname = "d9el3co3nr8o39";
    private final String user = "gewxdiedqwglcg";
    private final String pass = "d80b91924869404f9698d6d4c64540043535704867cfc01738b5e261cab28210";
    private final String sslString = "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

    private EntityManagerFactory emf;

    public Postgre() {
        Map<String, String> properties = new WeakHashMap<>();
        properties.put("javax.persistence.jdbc.url",
                "jdbc:postgresql://" + host
                + ":" + port + "/" + dbname
                + sslString);
        properties.put("javax.persistence.jdbc.user", user);
        properties.put("javax.persistence.jdbc.password", pass);
        properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("eclipselink.logging.level", "OFF");

        emf = Persistence.createEntityManagerFactory("Transcription-ActivatorPU", properties);
        System.out.println("DataBase at " + host);
    }

    public static Postgre getInstance() {
        return Postgre.EMFactoryHolder.getPostgre();
    }

    public static void closeInstance() {
        Postgre.EMFactoryHolder.closeConnection();
    }

    //<editor-fold defaultstate="collapsed" desc="FactoryHolder">
    private static class EMFactoryHolder {

        private static Postgre INSTANCE = new Postgre();

        public static Postgre getPostgre() {
            if (INSTANCE == null) {
                INSTANCE = new Postgre();
            }
            return INSTANCE;
        }

        public static void closeConnection() {
            INSTANCE.closeConn();
            INSTANCE = null;
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
        return Postgre.EMFactoryHolder.getEMFactory();

    }
}
