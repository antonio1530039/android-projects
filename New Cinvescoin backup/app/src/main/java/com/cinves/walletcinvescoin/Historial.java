package com.cinves.walletcinvescoin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Adapter;
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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

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





        //Instancia de API con dificultad
        final com.cinves.walletcinvescoin.API api = new com.cinves.walletcinvescoin.API(0, Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin/blockchain.x", this);

        //Leer la cadena de bloques
        Blockchain bc = api.blockchain;







        // Es posibile substituir por colores chirriantes que el usuario desee
        //ArrayAdapter adapter = new ArrayAdapter(this,R.layout.fila_spinner ,objects);
        //adapter.setDropDownViewResource(R.layout.fila_spinner_despega);


        LV1=(ListView) findViewById(R.id.listView2);


        //LV1.setAdapter(adapter);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        HistorialAsync ha = new HistorialAsync(api, this, kp.getPublic() );

        ha.execute(bc);






    }


    private void verifyPath () throws IOException {
        String Ruta= Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin";
        File F = new File (Ruta);
        if (!F.exists()) { // Ya existe el directorio
            Toast.makeText(getApplicationContext(),"Se ha creado la ruta",Toast.LENGTH_SHORT).show();
            F.mkdir();
        }
    }

    public class HistorialAsync extends AsyncTask<Blockchain, Void, Void>{

        private ProgressDialog dialog;
        Context cx;
        PublicKey pk;
        ArrayList<Transaccion> arrTransactions;
        API api;
        ArrayList<String> newList;

        public  HistorialAsync(final API api, Context c, PublicKey pk){
            this.cx = c;
            this.api = api;
            this.dialog = new ProgressDialog(c);
            this.pk = pk;
            this.arrTransactions = new ArrayList<>();
            this.newList = new ArrayList<>();

        }


        public void readHistorial(Blockchain bc, PublicKey pk){
            ArrayList<String> objects = new ArrayList<>();
            this.newList.clear();
            this.arrTransactions.clear();
            if(bc!=null){
                for(Block b : bc.chain){
                    for(Transaction t: b.transactions){
                        if(t.publicKeySender != null){
                            if(Utilities.encode(t.publicKeySender.getEncoded()).equals(Utilities.encode(pk.getEncoded()))   ||
                                    Utilities.encode(t.publicKeyReceiver.getEncoded()).equals(Utilities.encode(pk.getEncoded()))   ){

                                objects.add("- De: " + ( (Utilities.encode(t.publicKeySender.getEncoded()).equals(Utilities.encode(pk.getEncoded()))) ? "Mi dirección" :  Utilities.encode(t.publicKeySender.getEncoded()) )
                                        +"\n\n- A: "+ ( (Utilities.encode(t.publicKeyReceiver.getEncoded()).equals(Utilities.encode(pk.getEncoded()))) ? "Mi dirección" :  Utilities.encode(t.publicKeyReceiver.getEncoded()) )
                                        + "\n\n- Concepto: " +
                                        ((t.concept.getClass().isInstance(new Boleto())) ?  "\n\n----\nBoleto\n" +((Boleto) t.concept).ruta+ "\n"+ ((Boleto) t.concept).periodo+"\nPrecio: " +((Boleto) t.concept).precio+" CC\n---" : t.concept.toString()+ " CC" ));
                                arrTransactions.add((Transaccion) t);
                            }
                        }else{
                            if(Utilities.encode(t.publicKeyReceiver.getEncoded()).equals(Utilities.encode(pk.getEncoded())) ){
                                arrTransactions.add((Transaccion) t);
                                objects.add("Recompensa por minar: " + t.concept.toString() + " CC");
                            }

                        }

                    }
                }

                for(int l = objects.size()-1; l > -1; l--)
                    newList.add(objects.get(l));



                //this.list.setAdapter(adapter);

            }

        }


        @Override
        protected Void doInBackground(Blockchain... blockchains) {
            this.readHistorial(blockchains[0], this.pk );
            return null;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Leyendo cadena de bloques...");
            this.dialog.show();
        }


        @Override
        protected void onPostExecute(Void v) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(api.transactionExists(arrTransactions.get(position))){
                        Toast.makeText(getApplicationContext(), "Transacción válida y se encuentra almacenada en la cadena", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Transacción inválida", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Por defecto utiliza una configuración predeterminada
            ArrayAdapter adapter = new ArrayAdapter(cx,android.R.layout.simple_list_item_1 ,newList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            LV1.setAdapter(adapter);

        }

    }

}
