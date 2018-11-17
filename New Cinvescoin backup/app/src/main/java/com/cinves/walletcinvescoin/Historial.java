package com.cinves.walletcinvescoin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.spongycastle.pqc.math.ntru.util.Util;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;

import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Blockchain.Transaction;
import amp_new.Tools.Utilities;

public class Historial extends Activity {

    ImageButton back;

    ListView LV1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial);

        back = (ImageButton) findViewById(R.id.back);

        //Obtener el par de llaves pasado por parametro
        Intent i = getIntent();

        //Instancia de la base de datos
        dbConnect db = new dbConnect(this);

        try {
            verifyPath();
        } catch (IOException e) {
            e.printStackTrace();
        }



        byte[] rawKp = i.getByteArrayExtra("keypair");
        //Deserializacion de par de llaves
        KeyPair kp = db.deserializeKeyPair(rawKp);


        // Array of Months acting as a data pump
       // String[] objects = { "January", "Feburary", "March", "April", "May",
         //       "June", "July", "August", "September", "October", "November","December" };

        ArrayList<String> objects = new ArrayList<>();
        final ArrayList<Transaccion> arrTransactions = new ArrayList<>();


        //Instancia de API con dificultad
        final com.cinves.walletcinvescoin.API api = new com.cinves.walletcinvescoin.API(0, Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin/blockchain.x", this);

        //Leer la cadena de bloques
        Blockchain bc = api.blockchain;
        if(bc!=null){
            for(Block b : bc.chain){
                for(Transaction t: b.transactions){
                    if(t.publicKeySender != null){
                        if(Utilities.encode(t.publicKeySender.getEncoded()).equals(Utilities.encode(kp.getPublic().getEncoded()))   ||
                                Utilities.encode(t.publicKeyReceiver.getEncoded()).equals(Utilities.encode(kp.getPublic().getEncoded()))   ){

                            objects.add("- De: " + ( (Utilities.encode(t.publicKeySender.getEncoded()).equals(Utilities.encode(kp.getPublic().getEncoded()))) ? "Mi dirección" :  Utilities.encode(t.publicKeySender.getEncoded()) )
                                    +"\n\n- A: "+ ( (Utilities.encode(t.publicKeyReceiver.getEncoded()).equals(Utilities.encode(kp.getPublic().getEncoded()))) ? "Mi dirección" :  Utilities.encode(t.publicKeyReceiver.getEncoded()) )
                                    + "\n\n- Concepto: " +
                                    ((t.concept.getClass().isInstance(new Boleto())) ?  "\n\n----\nBoleto\n" +((Boleto) t.concept).ruta+ "\n"+ ((Boleto) t.concept).periodo+"\nPrecio: " +((Boleto) t.concept).precio+" CC\n---" : t.concept.toString()+ " CC" ));
                            arrTransactions.add((Transaccion) t);
                        }
                    }else{
                        if(Utilities.encode(t.publicKeyReceiver.getEncoded()).equals(Utilities.encode(kp.getPublic().getEncoded())) ){
                            arrTransactions.add((Transaccion) t);
                            objects.add("Recompensa por minar: " + t.concept.toString() + " CC");
                        }

                    }

                   }
            }

        }else{
            Toast.makeText(getApplicationContext(), "La cadena no existe", Toast.LENGTH_LONG).show();
        }

        //Invertir orden de la lista
        ArrayList<String> newList = new ArrayList<>();
        for(int l = objects.size()-1; l > -1; l--)
            newList.add(objects.get(l));


        // Por defecto utiliza una configuración predeterminada
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1 ,newList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Es posibile substituir por colores chirriantes que el usuario desee
        //ArrayAdapter adapter = new ArrayAdapter(this,R.layout.fila_spinner ,objects);
        //adapter.setDropDownViewResource(R.layout.fila_spinner_despega);


        LV1=(ListView) findViewById(R.id.listView2);


        LV1.setAdapter(adapter);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(api.transactionExists(arrTransactions.get(position))){
                    Toast.makeText(getApplicationContext(), "Transacción válida en la cadena", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Transacción inválida", Toast.LENGTH_SHORT).show();
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
