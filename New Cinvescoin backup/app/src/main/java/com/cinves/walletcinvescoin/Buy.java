package com.cinves.walletcinvescoin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.spongycastle.pqc.math.ntru.util.Util;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;

import amp_new.Security.Hash;
import amp_new.Tools.Utilities;

public class Buy extends Activity {

    Spinner boletos;

    EditText vendedor, precio;
    ImageButton backBuy, comprar;
    TextView balance;
    ImageButton update;
    Context CX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy);

        vendedor = (EditText) findViewById(R.id.vendedor_address_et);
        precio = (EditText) findViewById(R.id.precio_et);
        backBuy = (ImageButton) findViewById(R.id.backBuy);
        comprar = (ImageButton) findViewById(R.id.comprar);
        CX = this;

        boletos = (Spinner) findViewById(R.id.boleto_spinner);
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
        final KeyPair kp = db.deserializeKeyPair(rawKp);

        final Cartera cartera = new Cartera();

        cartera.setKeyPair(kp.getPublic(), kp.getPrivate());

        //Instancia de API con dificultad
        final com.cinves.walletcinvescoin.API api = new com.cinves.walletcinvescoin.API(0, Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin/blockchain.x", this);

        //Cargar boletos ///////////////////////////////////////////

        final ArrayList<Boleto> a = db.getAllBoletos();

        //Crear lista de String para mostrarlos en el Spinner
        ArrayList<String> s = new ArrayList<>();

        for(int x = 0; x < a.size(); x++)
            s.add(a.get(x).ruta + " | "+ a.get(x).periodo+" | "+ a.get(x).fecha);


        // Por defecto utiliza una configuración predeterminada
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1 ,s);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boletos.setAdapter(adapter);

        boletos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vendedor.setText(Utilities.encode(a.get(position).vendedor.getEncoded()));
                precio.setText(a.get(position).precio+ " CC");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        backBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!vendedor.getText().toString().equals("")){

                    final Dialog d3 = new Dialog(CX);
                    //Se asigna al layout el archivo xml que contiene el formulario para agregar una nueva encuesta
                    d3.setContentView(R.layout.pineldialog);
                    //Se asigna un titulo al dialogo
                    d3.setTitle("Ingresa tu pin para confimar la compra");
                    final EditText pinControl = (EditText) d3.findViewById(R.id.pin_confirmacion);
                    ImageButton back = (ImageButton) d3.findViewById(R.id.pin_back_btn);
                    ImageButton confirm = (ImageButton) d3.findViewById(R.id.pin_confirm_btn);


                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Hash h = new Hash();
                            if(h.getHexValue(pinControl.getText().toString()).equals(pin)){
                                Toast.makeText(getApplicationContext(), "Espere...", Toast.LENGTH_SHORT).show();
                                Transaccion t = cartera.comprarBoleto(a.get( boletos.getSelectedItemPosition() ));
                                if(t!=null){
                                    api.makeTransaction(t, cartera);
                                    d3.dismiss();
                                }
                                else{
                                    Toast.makeText(CX, "No puedes realizarte una transacción a ti mismo", Toast.LENGTH_SHORT).show();
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
