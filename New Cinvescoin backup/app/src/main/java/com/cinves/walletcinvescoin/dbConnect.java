/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cinves.walletcinvescoin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import amp_new.Security.DigitalSignature;
import amp_new.Security.Utilities;

/**
 *
 * @author cti
 */
public class dbConnect extends SQLiteOpenHelper implements Serializable {



    public static final String DATABASE_NAME = "cinvescoin.db";

    public dbConnect(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS node(\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	keypair blob,\n" +
                " user VARCHAR(255), \n" +
                " password VARCHAR(255) \n"
                + ")");
        /*db.execSQL("CREATE TABLE IF NOT EXISTS boleto(\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	folio VARCHAR(255),\n"
                + "	periodo VARCHAR(255),\n"
                + "	ruta VARCHAR(255),\n"
                + "	precio DECIMAL(10,5),\n"
                + "	vendedor blob,\n"
                + "	fechaCompra VARCHAR(255)\n"
                + ")");*/

        db.execSQL("CREATE TABLE IF NOT EXISTS boleto(\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	periodo VARCHAR(255),\n"
                + "	ruta VARCHAR(255),\n"
                + "	precio DECIMAL(10,5),\n"
                + "	vendedor blob, \n"
                + " fecha VARCHAR(255) \n"
                + ")");


        /**
         * User 1
         */
        KeyPair kp = null;
        DigitalSignature ds = new DigitalSignature(112, "ECC");
        try {
            kp = ds.generateKeyPair();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        byte[] serialized = new byte[0];
        try {
            serialized = this.serializeObjectForStorage(kp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(serialized != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("keypair", serialized);
            contentValues.put("user", "user");
            contentValues.put("password", "1234");
            db.insert("node", null, contentValues);
        }

        /**
         * User 2
         */

        KeyPair kp2 = null;
        try {
            kp2 = ds.generateKeyPair();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        byte[] serialized2 = new byte[0];
        try {
            serialized2 = this.serializeObjectForStorage(kp2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(serialized2 != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("keypair", serialized2);
            contentValues.put("user", "user2");
            contentValues.put("password", "1234");
            db.insert("node", null, contentValues);
        }

        /**
         * Vendedor
         */


        KeyPair kp3 = null;
        try {
            kp3 = ds.generateKeyPair();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        byte[] serialized3 = new byte[0];
        try {
            serialized3 = this.serializeObjectForStorage(kp3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(serialized3 != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("keypair", serialized3);
            contentValues.put("user", "vendedor");
            contentValues.put("password", "1234");
            db.insert("node", null, contentValues);

            /**
             * Boletos
             */
            byte[] serialized4 = new byte[0];
            try {
                serialized4 = this.serializeObjectForStorage(kp3.getPublic());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("periodo", "Lunes a Sabado");
            contentValues2.put("ruta", "Completo - Tamatan");
            contentValues2.put("precio", 100.00);
            contentValues2.put("vendedor", serialized4);
            contentValues2.put("fecha", "Abril de 2018");
            db.insert("boleto", null, contentValues2);

            ContentValues contentValues3 = new ContentValues();
            contentValues3.put("periodo", "Lunes a Sabado");
            contentValues3.put("ruta", "Entradas - Tamatan");
            contentValues3.put("precio", 50.00);
            contentValues3.put("vendedor", serialized4);
            contentValues3.put("fecha", "Abril de 2018");
            db.insert("boleto", null, contentValues3);

            ContentValues contentValues4 = new ContentValues();
            contentValues4.put("periodo", "Lunes a Viernes");
            contentValues4.put("ruta", "Entradas - Tamatan");
            contentValues4.put("precio", 50.00);
            contentValues4.put("vendedor", serialized4);
            contentValues4.put("fecha", "Abril de 2018");
            db.insert("boleto", null, contentValues4);


            ContentValues contentValues5 = new ContentValues();
            contentValues5.put("periodo", "Lunes a Viernes");
            contentValues5.put("ruta", "Entradas - Libertad");
            contentValues5.put("precio", 80.00);
            contentValues5.put("vendedor", serialized4);
            contentValues5.put("fecha", "Abril de 2018");
            db.insert("boleto", null, contentValues5);

        }




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //db.execSQL("DROP TABLE IF EXISTS node");
        //db.execSQL("DROP TABLE IF EXISTS boleto");
        onCreate(db);
    }

    public boolean insertBoleto(String periodo, String ruta, double precio, byte[] vendedor, String fecha){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("folio", folio);
        contentValues.put("periodo", periodo);
        contentValues.put("ruta", ruta);
        contentValues.put("precio", precio);
        contentValues.put("vendedor", vendedor);
        contentValues.put("fecha", fecha);
        db.insert("boleto", null, contentValues);

        return true;
    }


    public boolean insertNode(String user, String password, byte[] keypair){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("keypair", keypair);
        contentValues.put("user", user);
        contentValues.put("password", password);
        db.insert("node", null, contentValues);
        return true;
    }

    public Cursor getNode(String user, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from node where user='"+user+"' and password='"+password+"'", null );
        return res;
    }

    public int numberOfRows(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, table);
        return numRows;
    }


    public ArrayList<Boleto> getAllBoletos() {
        ArrayList<Boleto> array_list = new ArrayList<Boleto>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from boleto", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Boleto b = new Boleto();
            b.periodo = res.getString(1);
            b.ruta = res.getString(2);
            b.precio = res.getDouble(3);
            b.vendedor = this.deserializePublicKey(res.getBlob(4));
            b.fecha = res.getString(5);
            array_list.add(b);
            res.moveToNext();

        }
        db.close();
        return array_list;
    }

    public HashMap<String, Cartera_sa> getAllNodes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from node", null );
        res.moveToFirst();
        HashMap<String, Cartera_sa> nodes = new HashMap();
        while(res.isAfterLast() == false){
            Cartera_sa c = new Cartera_sa();
            c.user = res.getString(2);
            c.password = res.getString(3);
            c.kp = deserializeKeyPair(res.getBlob(1));
            c.stringAddress = Utilities.encode(c.kp.getPublic().getEncoded());

            nodes.put(c.stringAddress, c);

            //array_list.add(c);




            res.moveToNext();
        }
        db.close();
        return nodes;
    }

    public KeyPair deserializeKeyPair(byte[] path) {
        KeyPair e = null;
        try {
            InputStream myInputStream = new ByteArrayInputStream(path);
            ObjectInputStream in = new ObjectInputStream(myInputStream);

            e = (KeyPair) in.readObject();
            in.close();
            myInputStream.close();
            return e;
        } catch (IOException i) {
            //i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Error intentando deserializar el objeto");
            //c.printStackTrace();
            return null;
        }
    }

    public PublicKey deserializePublicKey(byte[] path) {
        PublicKey e = null;
        try {
            InputStream myInputStream = new ByteArrayInputStream(path);
            ObjectInputStream in = new ObjectInputStream(myInputStream);

            e = (PublicKey) in.readObject();
            in.close();
            myInputStream.close();
            return e;
        } catch (IOException i) {
            //i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Error intentando deserializar el objeto");
            //c.printStackTrace();
            return null;
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






/*

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
    }*/

}
