package com.jomac.transcription.referencebuilder.database.engine;

import com.hccs.util.FileUtilities;
import com.jomac.transcription.referencebuilder.Main;
import com.jomac.transcription.referencebuilder.database.IDatabase;
import com.jomac.transcription.referencebuilder.entitymanager.H2EMFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.Deflater;
import javax.persistence.EntityManagerFactory;
import org.h2.tools.DeleteDbFiles;

public class H2 implements IDatabase {

    private Connection conn;
    private String dbPath;
    private final ResourceBundle BUNDLE = Main.getResourceBundle();
    private final String USERNAME = BUNDLE.getString("db_user");
    private final String PASSWORD = BUNDLE.getString("db_pass");
    private final String DB_NAME = BUNDLE.getString("db_name");
    private final String DB_PATH = System.getProperty("user.dir").concat(File.separator).concat("db");
    private final String SCHEMA = "com/jomac/transcription/referencebuilder/sql/referencedb.sql";

    public H2() {
        dbPath = DB_PATH.concat(File.separator).concat(DB_NAME);
    }

    public EntityManagerFactory getEMFactory() {
        return H2EMFactory.getInstance().getEMFactory();
    }

    @Override
    public void createDB() throws Exception {
        Connection con;
        Statement stmt;

        InputStream is = H2.class.getClassLoader().getResourceAsStream(SCHEMA);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line;

        con = getConnection(true);
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("--") || line.startsWith("SET")) {
                continue;
            }

            buffer.append(line);

            if (line.endsWith(";")) {
                stmt = con.createStatement();
                stmt.execute(buffer.toString());
                stmt.close();

                buffer = new StringBuffer();
            }
        }
        stmt = con.createStatement();
        stmt.execute("INSERT INTO schema_version (version,dbname)"
                + "VALUES ('" + BUNDLE.getString("schema_version") + "',"
                + "'" + DB_NAME + "');");
        stmt.close();

        reader.close();
        is.close();
        con.close();
    }

    @Override
    public void zipDB() {
        try {
            FileUtilities.zipDirectory(
                    new File(dbPath + ".h2.db"),
                    new File(dbPath + ".zip"), Deflater.DEFAULT_COMPRESSION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean deleteDB() {
        DeleteDbFiles.execute(DB_PATH, DB_NAME, true);

        if (new File(dbPath).exists()) {
            return false;
        }

        return true;
    }

    @Override
    public void cleanUp() {
        try {
            Connection con = getConnection(true);
            Statement stmt;

            stmt = con.createStatement();
            stmt.execute("SHUTDOWN COMPACT");
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws Exception {
        return getConnection(false);
    }

    private Connection getConnection(boolean initConn) throws Exception {
        Class.forName("org.h2.Driver");
        Properties p = new Properties();
        p.put("user", USERNAME);
        p.put("password", PASSWORD);

        if (initConn) {
            return DriverManager.getConnection(
                    "jdbc:h2:".concat(dbPath), p);
        } else if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(
                    "jdbc:h2:".concat(dbPath).concat(";IFEXISTS=TRUE"), p);
        }
        return conn;
    }

    @Override
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
