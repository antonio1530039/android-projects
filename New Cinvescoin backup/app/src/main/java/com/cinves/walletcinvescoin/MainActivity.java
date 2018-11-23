package com.cinves.walletcinvescoin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Security.DigitalSignature;
import amp_new.Security.Hash;
import amp_new.Security.Utilities;

public class MainActivity extends Activity {

    ImageButton iniciar, experimento;


    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;
    EditText user, pin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        iniciar = (ImageButton) findViewById(R.id.btn_iniciar);
        experimento = (ImageButton) findViewById(R.id.experimento_btn);
        user = (EditText) findViewById(R.id.user_et);
        pin = (EditText) findViewById(R.id.pin_et);



        askPermissionOnly();

        final dbConnect db = new dbConnect(this);

        final int secLevel = 112;


//        Toast.makeText(getApplicationContext(), "Number of rows in Node: " + db.numberOfRows("node"), Toast.LENGTH_SHORT).show();

        //for(int i=0; i<arr.size(); i++ )
            //Toast.makeText(getApplicationContext(), "USER: " + Utilities.encode(arr.get(i).kp.getPublic().getEncoded()), Toast.LENGTH_SHORT).show();

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getText().toString().equals("") && !pin.getText().toString().equals("")){
                    Cursor c = db.getNode(user.getText().toString(), pin.getText().toString());
                    if(c.moveToFirst()){
                        Intent newActivity = new Intent(MainActivity.this, Segunda.class);
                        //KeyPair kp = db.deserializeKeyPair(c.getBlob(1));
                        newActivity.putExtra("keypair", c.getBlob(1));
                        Hash h = new Hash(secLevel);
                        newActivity.putExtra("pin", h.getHexValue(pin.getText().toString()));
                        startActivity(newActivity);
                        //Toast.makeText(getApplicationContext(), "Bienvenido!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "El usuario no existe", Toast.LENGTH_SHORT).show();
                }
            //    db.getNode()

                //Intent newActivity = new Intent(MainActivity.this, Segunda.class);
                //Enviar parametros por el intent
                // parametro: tipo ... corresponde al tipo de seguridad (BAJA, MEDIA, ALTA)
                // parametro: tiempo ... corresponde al cálculo de tiempo realizado por algun procedimiento (a manera de ejemplo se usara: 20)
                //newActivity.putExtra("tipo", extra_tipo_seguridad);
                //newActivity.putExtra("tiempo", "20");
                //Iniciar la actividad
                //startActivity(newActivity);





            }
        });

        final Context c = this;
        experimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Experiment e = new Experiment(c, Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin");
                e.execute();

            }
        });

    }








    private void askPermissionOnly() {
        this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        this.askPermission(REQUEST_ID_READ_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    // With Android Level >= 23, you have to ask the user
    // for permission with device (For example read/write data on the device).
    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }
        }
        return true;
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_READ_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //cargarControles();
                        Toast.makeText(getApplicationContext(), "Permiso de lectura concedido!", Toast.LENGTH_SHORT).show();
                        //cargarControles();
                    }
                }
                case REQUEST_ID_WRITE_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //writeFile();
                        //
                        Toast.makeText(getApplicationContext(), "Permiso de escritura concedido!", Toast.LENGTH_SHORT).show();
                        //cargarControles();
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permiso cancelado", Toast.LENGTH_SHORT).show();
        }
    }


    public class Experiment extends AsyncTask<Void, Void, Void>{
        private ProgressDialog dialog;
        String ruta;

        public Experiment(Context c, String ruta){
            dialog = new ProgressDialog(c);
            this.ruta = ruta;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            performExperiment();
            return null;
        }
        private void verifyPath () throws IOException {
            String Ruta= this.ruta;
            File F = new File (Ruta);
            if (!F.exists()) { // Ya existe el directorio
               // Toast.makeText(getApplicationContext(),"Se ha creado la ruta",Toast.LENGTH_SHORT).show();
                F.mkdir();
            }
        }


        public void performExperiment() {

            try {
                verifyPath();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //Abrir archivo de texto
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(
                        Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/cinvescoin/experimento.txt"
                        , "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }



            //Experimento 1. Crear 100 transacciones y procesarlas
            /*

             */
            //Medir tiempo


            //Generate KeyPair for transaction

            int d_securityLevel = 112;

            ArrayList<String> algorithms = new ArrayList<>();
            algorithms.add("ECC");
            algorithms.add("RSA");
            algorithms.add("DSA");



            for(int i = 0; i < algorithms.size(); i++){
                System.out.println("Realizando experimento: " + algorithms.get(i));
                KeyPair kp = null;
                DigitalSignature ds = new DigitalSignature(d_securityLevel,algorithms.get(i));
                try {
                    kp = ds.generateKeyPair();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                }

                long start = System.nanoTime();
                Transaccion t = new Transaccion(kp.getPublic(), 100.0, 0, d_securityLevel, algorithms.get(i));
                t.processTransaction();
                long end = System.nanoTime();

                double timeTaked = ((end - start) / 1000000.0) / 1000.0;

                writer.println("=============");
                writer.println("Experimento 1."+ (i+1)+" Algoritmo: " + algorithms.get(i) + " / Nivel de seguridad: 112-bits");
                writer.println("Determinar el número de transacciones creadas y procesadas por segundo, para el nivel de seguridad.");

                writer.println("-> El experimentó se realizó en: " + timeTaked + " seg");
                writer.println("-> Número de transacciones por segundo: " + (1.0 / timeTaked) + " seg");
                writer.println("=============");
                System.out.println("Termino experimento: " + algorithms.get(i));
            }







            //Experimento 2
            //Determinar el número de transacciones creadas y procesadas por segundo variando el nivel de seguridad y usando solamente el algoritmo ECC.




            ArrayList<Integer> secLevels = new ArrayList<>();

            secLevels.add(128);
            secLevels.add(192);
            secLevels.add(256);


            for (int i = 0; i < secLevels.size(); i++) {
                DigitalSignature ds = new DigitalSignature(secLevels.get(i), "ECC");
                KeyPair kpNew = null;
                try {
                    kpNew = ds.generateKeyPair();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                }
                long start2 = System.nanoTime();
                Transaccion tt = new Transaccion(kpNew.getPublic(), 100.0, 0, secLevels.get(i), "ECC");
                tt.processTransaction();
                long end2 = System.nanoTime();
                double timeTaked2 = ((end2 - start2) / 1000000.0) / 1000.0;

                writer.println("=============");
                writer.println("Experimento 2." + (i + 1) + ". Algoritmo ECC / Nivel de seguridad: " + secLevels.get(i));
                writer.println("Una transacción toma: " + timeTaked2 + " seg");
                writer.println("Número de transacciones por segundo: " + (1.0 / timeTaked2)+ " seg");
                writer.println("=============");


            }




            //Experimento 3.
            //Determinar el número de bloques creados y agregados a la cadena de bloques por segundo,
            // variando el nivel de seguridad y los tres algoritmos de firma.


            for(int i =0; i < secLevels.size(); i++){
                System.out.println("Ejecutando ns: " + secLevels.get(i));

                for(int j = 0; j < algorithms.size(); j++){

                    if( !(algorithms.get(j).equals("DSA") && secLevels.get(i) > 128)){
                        Blockchain bc = new Blockchain(0, secLevels.get(i));
                        DigitalSignature ds = new DigitalSignature(secLevels.get(i), algorithms.get(j));
                        KeyPair kpNew = null;
                        try {
                            kpNew = ds.generateKeyPair();
                        } catch (NoSuchProviderException e) {
                            e.printStackTrace();
                        }
                        long start3 = System.nanoTime();
                        //Instancia del bloque con una transaccion agregada
                        Block b = new Block(new Transaccion(kpNew.getPublic(), 100.0, 0, secLevels.get(i), algorithms.get(j)), bc.getLashHashInChain(), secLevels.get(i) );
                        b.mineBlock(0);
                        bc.addBlock(b);
                        long end3 = System.nanoTime();
                        double timeTaked3 = ((end3 - start3) / 1000000.0) / 1000.0;
                        writer.println("=============");
                        writer.println("Experimento 3." + (i+1) + "."+(j+1)+". Algoritmo: " + algorithms.get(j)+ " / Nivel de seguridad" + secLevels.get(i));
                        writer.println("Cada bloque toma: " + timeTaked3 + " seg");
                        writer.println("En un segundo, se agregan: " + (1.0 / timeTaked3) + " bloques");
                        writer.println("La cadena es válida: " + bc.validateChain());
                        writer.println("=============");
                        System.out.println("Termino exp para algoritmo: " + algorithms.get(j));
                    }else{
                        System.out.println("Se ignoro algoritmo DSA");
                    }

                }
            }


            writer.close();


        }



        /** application context. */
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Ejecutando experimento...");
            this.dialog.show();
        }


        @Override
        protected void onPostExecute(Void nu) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
