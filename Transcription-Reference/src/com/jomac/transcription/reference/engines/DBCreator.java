package com.jomac.transcription.reference.engines;

import com.jomac.transcription.reference.Main;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class DBCreator {

    private final String dbPath, username, password;
    private final String SCHEMA = "com/jomac/transcription/reference/sql/referencedb.sql";

    public DBCreator(String dbPath, String username, String password) {
        this.dbPath = dbPath;
        this.username = username;
        this.password = password;
    }

    public void create() throws Exception {
        Connection con;
        Statement stmt;

        InputStream is = H2.class.getClassLoader().getResourceAsStream(SCHEMA);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line;

        Class.forName("org.h2.Driver");

        Properties p = new Properties();
        p.put("user", username);
        p.put("password", password);

        con = DriverManager.getConnection(
                "jdbc:h2:".concat(dbPath), p);
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
        stmt.execute("INSERT INTO schema_version (version)"
                + "VALUES ('" + Main.getResourceBundle().getString("local_schema") + "');");
        stmt.close();

        //Clean-Up
        stmt = con.createStatement();
        stmt.execute("SHUTDOWN COMPACT");
        stmt.close();

        reader.close();
        is.close();
        con.close();
    }
}
