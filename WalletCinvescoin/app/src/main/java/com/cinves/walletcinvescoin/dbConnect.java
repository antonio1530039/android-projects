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
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 *
 * @author cti
 */
public class dbConnect extends SQLiteOpenHelper {

    String dbPath;
    String dbName;

    final String NombreDirectorio ="Cinvesoin";
    //this.dbPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/"+NombreDirectorio+"/";




    public static final String DATABASE_NAME = "cinvescoin.db";
    //private HashMap hp;



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
        db.execSQL("CREATE TABLE IF NOT EXISTS boleto(\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	folio VARCHAR(255),\n"
                + "	periodo VARCHAR(255),\n"
                + "	ruta VARCHAR(255),\n"
                + "	precio DECIMAL(10,5),\n"
                + "	vendedor blob,\n"
                + "	fechaCompra VARCHAR(255)\n"
                + ")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS node");
        db.execSQL("DROP TABLE IF EXISTS boleto");
        onCreate(db);
    }

    public boolean insertBoleto(String folio, String periodo, String ruta, double precio, byte[] vendedor, String fechaCompra){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("folio", folio);
        contentValues.put("periodo", periodo);
        contentValues.put("ruta", ruta);
        contentValues.put("precio", precio);
        contentValues.put("vendedor", vendedor);
        contentValues.put("fechaCompra", fechaCompra);
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
            b.folio = res.getString(1);
            b.ruta = res.getString(2);
            b.precio = res.getDouble(3);
            b.vendedorRaw = res.getBlob(4);
            b.fechaCompra = res.getString(5);
            /*
            *
            * 	folio VARCHAR(255),\n"
                + "	periodo VARCHAR(255),\n"
                + "	ruta VARCHAR(255),\n"
                + "	precio DECIMAL(10,5),\n"
                + "	vendedor blob,\n"
                + "	fechaCompra VARCHAR(255)\n"
            *
            * */

            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Carterita> getAllNodes() {
        ArrayList<Carterita> array_list = new ArrayList<Carterita>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from node", null );
        String[] columns = {"id", "keypair", "user", "password"};
        Cursor res = db.query("node",columns,null,null,null,null,null);
        //res.moveToFirst();

        while (res.moveToNext()){
            Carterita c = new Carterita();
            c.user = res.getString(2);
            c.password = res.getString(3);
           // c.kp = deserializeKeyPair(res.getBlob(1));


            /*
            *	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	keypair blob,\n" +
                " user VARCHAR(255), \n" +
                " password VARCHAR(255) \n"
            *
            *
            * */

            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            //res.moveToNext();
        }
        return array_list;
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
