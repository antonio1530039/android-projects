/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cinves.walletcinvescoin;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author cti
 */
public class dbConnect {

    String dbPath;
    String dbName;

    final String NombreDirectorio ="Cinvesoin";

    public dbConnect() {
        //this.dbPath = "jdbc:sqlite:/home/cti/NetBeansProjects/DbTest/testFromJava.db";
        this.dbPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/"+NombreDirectorio+"/";
        this.dbName = "cinvescoin";
        if (createNewDatabase(this.dbPath, this.dbName)) {
            
            String sql = "CREATE TABLE IF NOT EXISTS node(\n"
                    + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "	keypair blob,\n" +
                    " user VARCHAR(255), \n" +
                    " password VARCHAR(255) \n"
                    + ");";

            String sql2
                    = "CREATE TABLE IF NOT EXISTS boleto(\n"
                    + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "	folio VARCHAR(255),\n"
                    + "	periodo VARCHAR(255),\n"
                    + "	ruta VARCHAR(255),\n"
                    + "	precio DECIMAL(10,5),\n"
                    + "	vendedor blob,\n"
                    + "	fechaCompra VARCHAR(255)\n"
                    + ");";

            execute(this.dbPath, this.dbName, sql);

            execute(this.dbPath, this.dbName, sql2);
        }
    }

    

    public static boolean createNewDatabase(String dbPath, String fileName) {
        String url = "jdbc:sqlite:"+ dbPath +fileName+".db";
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error creating new database");
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }

    public Connection connect() {
        // SQLite connection string
        //String url = "jdbc:sqlite:/home/cti/NetBeansProjects/DbTest/testFromJava.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:"+this.dbPath+ this.dbName+".db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public boolean insert(){
        return true;
    }

    public static void execute(String dbPath, String dbName, String sql) {
        // SQL statement for creating a new table
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbPath+dbName+".db");
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("OK executed: " + sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public byte[] serializeObjectForStorage(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.flush();
            return bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                System.out.println("Exception in: trying to serialize obejct");
                return null;
            }
        }
    }

    public Object deserializeObject(byte[] path){
        return new String();
    }

}
