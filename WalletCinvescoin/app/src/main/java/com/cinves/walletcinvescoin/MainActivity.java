package com.cinves.walletcinvescoin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.Security;
import java.util.ArrayList;

import amp_new.Security.DigitalSignature;

public class MainActivity extends Activity {

    ImageButton iniciar;


    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        iniciar = (ImageButton) findViewById(R.id.btn_iniciar);
        askPermissionOnly();

        dbConnect db = new dbConnect(this);



        DigitalSignature ds = new DigitalSignature();

        KeyPair kp;

        kp = ds.generateKeyPair();

        byte[] objectSerialized = new byte[0];


        try {
            objectSerialized = this.serializeObjectForStorage(kp);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Exception in serialized object", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        if(db.insertNode("user","password", objectSerialized))
            Toast.makeText(getApplicationContext(), "Node inserted!", Toast.LENGTH_SHORT).show();


        Toast.makeText(getApplicationContext(), "Number of rows in Node: " + db.numberOfRows("node"), Toast.LENGTH_SHORT).show();

        ArrayList<Carterita> arr = db.getAllNodes();
        Toast.makeText(getApplicationContext(), "Number GET ALL NODES: " + arr.size(), Toast.LENGTH_SHORT).show();

        for(int i=0; i<arr.size(); i++ )
            Toast.makeText(getApplicationContext(), "USER: " + arr.get(i).user, Toast.LENGTH_SHORT).show();

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(MainActivity.this, Segunda.class);
                //Enviar parametros por el intent
                // parametro: tipo ... corresponde al tipo de seguridad (BAJA, MEDIA, ALTA)
                // parametro: tiempo ... corresponde al cálculo de tiempo realizado por algun procedimiento (a manera de ejemplo se usara: 20)
                //newActivity.putExtra("tipo", extra_tipo_seguridad);
                //newActivity.putExtra("tiempo", "20");
                //Iniciar la actividad
                startActivity(newActivity);





            }
        });

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
