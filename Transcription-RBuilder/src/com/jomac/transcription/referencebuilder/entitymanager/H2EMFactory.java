package com.jomac.transcription.referencebuilder.entitymanager;

import com.jomac.transcription.referencebuilder.Main;
import java.io.File;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class H2EMFactory {

    private final EntityManagerFactory EMF;
    private final ResourceBundle bundle = Main.getResourceBundle();

    public H2EMFactory() {
        String dbPath_ = System.getProperty("user.dir").concat(File.separator).concat("db")
                .concat(File.separator).concat(bundle.getString("db_name"));
        Map<String, String> properties = new WeakHashMap<>();
        properties.put("javax.persistence.jdbc.url",
                "jdbc:h2:" + dbPath_ + ";IFEXISTS=TRUE");
        properties.put("javax.persistence.jdbc.user", bundle.getString("db_user"));
        properties.put("javax.persistence.jdbc.password", bundle.getString("db_pass"));
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");

        EMF = Persistence.createEntityManagerFactory("Transcription-ReferenceBuilderPU", properties);
    }

    public static H2EMFactory getInstance() {
        return EMFactoryHolder.INSTANCE;
    }

    private static class EMFactoryHolder {

        private static final H2EMFactory INSTANCE = new H2EMFactory();
    }

    public EntityManagerFactory getEMFactory() {
        return EMF;
    }
}
