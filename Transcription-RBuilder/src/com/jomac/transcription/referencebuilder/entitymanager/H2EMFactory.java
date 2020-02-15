package com.jomac.transcription.referencebuilder.entitymanager;

import com.jomac.transcription.referencebuilder.Main;
import java.io.File;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class H2EMFactory {

    private EntityManagerFactory emf;
    private ResourceBundle bundle = Main.getResourceBundle();

    public H2EMFactory() {
        String dbPath_ = System.getProperty("user.dir").concat(File.separator).concat("db")
                .concat(File.separator).concat(bundle.getString("db_name"));
        Map<String, String> properties = new WeakHashMap<String, String>();
        properties.put("javax.persistence.jdbc.url",
                "jdbc:h2:" + dbPath_ + ";IFEXISTS=TRUE");
        properties.put("javax.persistence.jdbc.user", bundle.getString("db_user"));
        properties.put("javax.persistence.jdbc.password", bundle.getString("db_pass"));
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");

        emf = Persistence.createEntityManagerFactory("Transcription-ReferenceBuilderPU", properties);
    }

    public static H2EMFactory getInstance() {
        return EMFactoryHolder.INSTANCE;
    }

    private static class EMFactoryHolder {

        private static final H2EMFactory INSTANCE = new H2EMFactory();
    }

    public EntityManagerFactory getEMFactory() {
        return emf;
    }
}
