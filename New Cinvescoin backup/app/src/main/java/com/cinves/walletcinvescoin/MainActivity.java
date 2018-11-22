package com.cinves.walletcinvescoin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
                        Hash h = new Hash();
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


        experimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performExperiment();
            }
        });

    }


    public void performExperiment() {

        try {
            verifyPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Starting experiment...", Toast.LENGTH_SHORT).show();

        //Experimento 1. Crear 100 transacciones y procesarlas

        //Medir tiempo


        //Generate KeyPair for transaction

        KeyPair kp;
        DigitalSignature ds = new DigitalSignature("P-224");
        kp = ds.generateKeyPair();

        long start = System.nanoTime();
        Transaccion t = new Transaccion(kp.getPublic(), 100.0, 0, "P-224");
        t.processTransaction();
        //for (int i = 0; i < 100; i++) {
          //  Transaccion t = new Transaccion(kp.getPublic(), 100.0, i);
            //t.processTransaction();
        //}
        long end = System.nanoTime();

        double timeTaked = ((end - start) / 1000000.0) / 1000.0;

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
        writer.println("=============");
        writer.println("Experimento 1. Transacciones procesadas por segundo");
        writer.println("El experimentó se realizó en: " + timeTaked + " seg");
        writer.println("Número de transacciones por segundo: " + (1.0 / timeTaked) + " seg");
        writer.println("=============");


        //Experimento 2. Número de transacciones por segundo variando el nivel de seguridad


        ArrayList<String> curves = new ArrayList<>();

        curves.add("P-256");
        curves.add("P-384");
        curves.add("P-521");


        for (int i = 0; i < curves.size(); i++) {
            KeyPair kpNew;
            kpNew = generateKeyPairForExperiment(curves.get(i));
            long start2 = System.nanoTime();
            Transaccion tt = new Transaccion(kpNew.getPublic(), 100.0, 0, curves.get(i));
            tt.processTransaction();
            //for (int j = 0; j < 100; j++) {
                //Transaccion t = new Transaccion(kpNew.getPublic(), 100.0, i);
                //t.processTransaction();
            //}
            long end2 = System.nanoTime();
            double timeTaked2 = ((end2 - start2) / 1000000.0) / 1000.0;

            writer.println("=============");
            writer.println("Experimento 2." + (i + 1) + ". Curva: " + curves.get(i));
            writer.println("Una transacción toma: " + timeTaked2 + " seg");
            writer.println("Número de transacciones por segundo: " + (1.0 / timeTaked2)+ " seg");
            writer.println("=============");


        }




        //Experimento 3. Número de bloques agregados a la cadena por segundo

        Blockchain bc = new Blockchain(0);


        Integer[] exp3 = {10, 100, 1000};

        for(int i = 0; i < exp3.length; i++){



                Block b = new Block(new Transaccion(kp.getPublic(), 100.0, 0, "P-224"), bc.getLashHashInChain() );

                for(int j = 0; j < exp3[i]-1; j++){
                    b.addTransaction(new Transaccion(kp.getPublic(), 100.0, j+1, "P-224"));
                }
                long start3 = System.nanoTime();
                b.mineBlock(0);
                bc.addBlock(b);
                long end3 = System.nanoTime();

            double timeTaked3 = ((end3 - start3) / 1000000.0) / 1000.0;


            writer.println("=============");
            writer.println("Experimento 3." + (i+1) + ". Transacciones agregadas en cada bloque: " + exp3[i]);
            writer.println("Cada bloque toma: " + timeTaked3 + " seg");
            writer.println("En un segundo, se agregan: " + (1.0 / timeTaked3) + " bloques");
            writer.println("La cadena es válida: " + bc.validateChain());
            writer.println("=============");
        }


        writer.close();




        Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();





    }


    public KeyPair generateKeyPairForExperiment(String curve) {
        try {
            //Defautl: P-224
            ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(curve);
            KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA");
            g.initialize(ecSpec, new SecureRandom());
            return g.generateKeyPair();
        } catch (NoSuchAlgorithmException var3) {
            Logger.getLogger(DigitalSignature.class.getName()).log(Level.SEVERE, (String)null, var3);
        } catch (InvalidAlgorithmParameterException var4) {
            Logger.getLogger(DigitalSignature.class.getName()).log(Level.SEVERE, (String)null, var4);
        }

        return null;
    }



    private void verifyPath () throws IOException {
        String Ruta= Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin";
        File F = new File (Ruta);
        if (!F.exists()) { // Ya existe el directorio
            Toast.makeText(getApplicationContext(),"Se ha creado la ruta",Toast.LENGTH_SHORT).show();
            F.mkdir();
        }
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
}
