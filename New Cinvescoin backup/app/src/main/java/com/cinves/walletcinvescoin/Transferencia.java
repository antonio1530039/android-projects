package com.cinves.walletcinvescoin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;

import amp_new.Security.Hash;

public class Transferencia extends Activity {

    Spinner boletos;

    EditText address, cantidad;
    ImageButton back, commit;
    TextView balance;
    ImageButton update;
    Context CX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transferencia);

        address = (EditText) findViewById(R.id.transferencia_address_et);
        cantidad = (EditText) findViewById(R.id.transferencia_cantidad_et);
        back = (ImageButton) findViewById(R.id.transferencia_back_btn);
        commit = (ImageButton) findViewById(R.id.transferencia_commit_btn);
        CX = this;


        //Obtener el par de llaves pasado por parametro
        Intent i = getIntent();

        //Instancia de la base de datos
        final dbConnect db = new dbConnect(this);

        try {
            verifyPath();
        } catch (IOException e) {
            e.printStackTrace();
        }



        final byte[] rawKp = i.getByteArrayExtra("keypair");
        final String pin = i.getStringExtra("pin");
        //Deserializacion de par de llaves
        final KeyPair kp = db.deserializeKeyPair(rawKp);


        final int secLevel = 112;
        final String algorithm = "ECC";



        final Cartera cartera = new Cartera(secLevel, algorithm);

        cartera.setKeyPair(kp.getPublic(), kp.getPrivate());




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!address.getText().toString().equals("") && !cantidad.getText().toString().equals("")){

                    final Dialog d3 = new Dialog(CX);
                    //Se asigna al layout el archivo xml que contiene el formulario para agregar una nueva encuesta
                    d3.setContentView(R.layout.pineldialog);
                    //Se asigna un titulo al dialogo
                    d3.setTitle("Ingresa tu pin para confimar la transferencia");
                    final EditText pinControl = (EditText) d3.findViewById(R.id.pin_confirmacion);
                    ImageButton back = (ImageButton) d3.findViewById(R.id.pin_back_btn);
                    ImageButton confirm = (ImageButton) d3.findViewById(R.id.pin_confirm_btn);


                    final String algorithm = "ECC";
                    final int secLevel = 112;

                    //Instancia de API con dificultad
                    final API api = new API(0, Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin/blockchain.x", CX, secLevel, algorithm);

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Hash h = new Hash(secLevel);

                            if(h.getHexValue(pinControl.getText().toString()).equals(pin)){

                                HashMap<String, Cartera_sa> nodes = db.getAllNodes();
                                Cartera_sa c = nodes.get(address.getText().toString());
                                if(c!= null){
                                    Transaccion t = cartera.makeTransfer(c.kp.getPublic(), Double.valueOf(cantidad.getText().toString()));
                                    if(t != null){
                                        d3.dismiss();
                                        apiTransaction ap = new apiTransaction();
                                        ap.transaction = t;
                                        ap.node = cartera;
                                        //api.makeTransaction(t, cartera);
                                        api.execute(ap);

                                    }
                                    else{
                                        Toast.makeText(CX, "No puedes realizarte una transacción a ti mismo", Toast.LENGTH_SHORT).show();
                                        d3.dismiss();
                                    }

                                }

                                else{
                                    Toast.makeText(CX, "No existe ningun usuario con esa dirección", Toast.LENGTH_LONG).show();
                                    d3.dismiss();
                                }


                            }else{
                                Toast.makeText(CX, "PIN incorrecto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d3.dismiss();
                        }
                    });


                    d3.show();










                }
            }
        });










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
