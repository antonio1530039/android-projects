package com.cinves.walletcinvescoin;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.spongycastle.pqc.math.ntru.util.Util;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;

import amp_new.AppInterface.API;
import amp_new.Security.Hash;
import amp_new.Tools.Utilities;

public class Segunda extends Activity {

    ImageButton historial, copy, buy, transfer;

    EditText address;
    Button copiar;
    TextView balance;
    ImageButton update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.segunda);

        address = (EditText) findViewById(R.id.address_et);
        balance = (TextView) findViewById(R.id.balance_tv);
        update = (ImageButton) findViewById(R.id.update);
        historial = (ImageButton) findViewById(R.id.historia_btn);
        copy = (ImageButton) findViewById(R.id.copy_btn);
        transfer = (ImageButton) findViewById(R.id.transfer_btn);

        buy = (ImageButton) findViewById(R.id.buyBoleto_btn);

        //Obtener el par de llaves pasado por parametro
        Intent i = getIntent();

        //Instancia de la base de datos
        dbConnect db = new dbConnect(this);

        try {
            verifyPath();
        } catch (IOException e) {
            e.printStackTrace();
        }



        final byte[] rawKp = i.getByteArrayExtra("keypair");

        final String pin = i.getStringExtra("pin");
        //Deserializacion de par de llaves
        KeyPair kp = db.deserializeKeyPair(rawKp);
        //Asignar dirección pública al cuadro de texto
        address.setText(Utilities.encode(kp.getPublic().getEncoded()));


        final Cartera cartera = new Cartera();

        cartera.setKeyPair(kp.getPublic(), kp.getPrivate());

        //Instancia de API con dificultad
        final com.cinves.walletcinvescoin.API api = new com.cinves.walletcinvescoin.API(0, Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin/blockchain.x", this);

        //Enviar incentivo

        if(!cartera.verifyFirstIncentive())
            api.makeTransaction(cartera.getFirstIncentive(), cartera);

        updateSaldo(cartera);



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSaldo(cartera);
            }
        });


        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(Segunda.this, Historial.class);
                newActivity.putExtra("keypair",rawKp);
                newActivity.putExtra("pin", pin);
                startActivity(newActivity);
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(Segunda.this, Buy.class);
                newActivity.putExtra("keypair",rawKp);
                newActivity.putExtra("pin", pin);
                startActivity(newActivity);
            }
        });

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(Segunda.this, Transferencia.class);
                newActivity.putExtra("keypair",rawKp);
                newActivity.putExtra("pin", pin);
                startActivity(newActivity);
            }
        });



        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(
                        "text label", // What should I set for this "label"?
                        address.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Segunda.this, "Dirección copiada a portapapeles", Toast.LENGTH_SHORT).show();
            }
        });







    }


    private void updateSaldo(Cartera cartera){
        balance.setText(""+cartera.miSaldo()+" CC");
    }


    private void verifyPath () throws IOException {
        String Ruta= Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin";
        File F = new File (Ruta);
        if (!F.exists()) { // Ya existe el directorio
            Toast.makeText(getApplicationContext(),"Se ha creado la ruta",Toast.LENGTH_SHORT).show();
            F.mkdir();
        }
    }
}
