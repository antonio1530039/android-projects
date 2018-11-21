/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cinves.walletcinvescoin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import amp_new.*;
import amp_new.AppInterface.MinerNode;
import amp_new.AppInterface.Node;
import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Blockchain.Transaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import amp_new.Security.*;

/**
 *
 * @author cti
 */
public class API extends AsyncTask<apiTransaction, Void, Boolean> implements Serializable {

    public Blockchain blockchain;
    //ArrayList<com.cinves.walletcinvescoin.Node> nodes;
    int difficultyOfPow;
    boolean incentiveControlV;
    String ruta;
    Context CX;
    private ProgressDialog dialog;

    /**
     * Constructor
     *
     * @param difficultyOfPow
     */
    public API(int difficultyOfPow, String ruta, Context c) {
        //Cargar cadena de bloques
        this.blockchain = Utilities.readMyBlockchain(ruta);
        this.ruta = ruta;
        if (this.blockchain == null) {
            this.blockchain = new Blockchain(difficultyOfPow); //Instancia de la cadena de bloques
            this.saveChain();
        }
        this.CX = c;
        dialog = new ProgressDialog(c);
        //this.nodes = new ArrayList<com.cinves.walletcinvescoin.Node>();
        this.difficultyOfPow = difficultyOfPow;
        this.incentiveControlV = false;
        /**
         * Aplicacion -- Incentive control
         */
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                incentiveControl();
            }
        }, 0, 1000 * 60);
    }



    public void addNode(com.cinves.walletcinvescoin.Node n, Transaccion t) throws CloneNotSupportedException {
        //this.nodes.add(n);
        //n.setAPI(this);
        //Cartera c = (Cartera) n;
        //if(!c.verifyFirstIncentive()){
          //  this.makeTransaction(t);
        //}

    }

    public boolean makeTransaction(Transaccion transaction, com.cinves.walletcinvescoin.Node node) {
       //Toast.makeText(CX, "Espere...", Toast.LENGTH_SHORT).show();
        if (propagateTransaction(transaction, node)) {
            return true;
        }else{
            //Toast.makeText(CX, "Transacción no aprobada... No tienes monedas suficientes", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean propagateTransaction(Transaccion transaction, com.cinves.walletcinvescoin.Node node) {
            //boolean success = true;
            //Verificar que la transaccion sea correcta
            //for (com.cinves.walletcinvescoin.Node n : this.nodes) {
             //   if (!n.verifyTransaction(transaction)) {
             //       success = false;
             //   }
           // }

            if (transaction.processTransaction()) {
                //Enviar transaccion a mineros
                
                //com.cinves.walletcinvescoin.MinerNode mn = (com.cinves.walletcinvescoin.MinerNode) node;
                //mn.registerTransaction(transaction, this);



                ((com.cinves.walletcinvescoin.MinerNode)node).registerTransaction(transaction, this);




                /*for (Node n : this.nodes) {
                    //Verificar si es instancia de un nodo minero
                    MinerNode mn = (MinerNode) n;
                    mn.registerTransaction(transaction);
                     System.out.println("=========Transactiond sent to one miner==========");
                }*/
                //Toast.makeText(this.CX, "Transacción pendiente... Minando", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }

    }

    public void newBlockMined(com.cinves.walletcinvescoin.MinerNode node, Block block) {
        this.blockchain = Utilities.readMyBlockchain(ruta);
        boolean exists = false;
        //Verificar que el bloque no este en la cadena ya

       // for (Block b : this.blockchain.chain) {
         //   if (b.hash.equals(block.hash)){
           //     System.out.println("..........................................................................................................");
             //   exists = true;
                //break;
            //}
        //}

       // if (!exists) {
            if (node.verifyBlock(block)) {
                this.blockchain.addBlock(block);
                node.clearTransactionsSaved();
                this.saveChain();
                //Toast.makeText(this.CX, "Transacción registrada!", Toast.LENGTH_SHORT).show();
                //Crear transaccion con incentivo
                if (this.incentiveControlV) {
                    this.incentiveControl();
                   // Toast.makeText(CX, "Espere...", Toast.LENGTH_SHORT).show();
                    this.makeTransaction((Transaccion) node.rewardOfPow(), node);
                }

            }
        //}
        
        node.clearTransactionsSaved();

    }

    public void incentiveControl() {
        this.incentiveControlV = !this.incentiveControlV;
    }
    
    public String getLastHashInChain(){
        return this.blockchain.getLashHashInChain();
    }
    
    public int getDifficultyOfPow(){
        return this.blockchain.getDifficultyOfPow();
    }
    
    public void saveChain(){
        try {
            //File f = new File(this.ruta);
            //if(f.exists()){
            //    f.delete();
            //}
            FileOutputStream fileOut = new FileOutputStream(this.ruta);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.blockchain);
            out.close();
            fileOut.close();
        } catch (IOException var4) {
            //System.out.println("Excepcion: " + var4.printStackTrace());
            var4.printStackTrace();
        }

        //this.blockchain.saveBlockchain(this.ruta);
    }

    public boolean transactionExists(Transaccion t){
        return this.blockchain.transactionExists(t);
    }


    public boolean makeTransaction(apiTransaction ap){
        //Toast.makeText(CX, "Espere...", Toast.LENGTH_SHORT).show();
        if (propagateTransaction( (Transaccion) ap.transaction, ap.node)) {
            return true;
        }else{
            //Toast.makeText(CX, "Transacción no aprobada... No tienes monedas suficientes", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    protected Boolean doInBackground(apiTransaction... apiTransactions) {
        if(this.makeTransaction(apiTransactions[0]))
            return true;
        else
            return false;

    }

    /** application context. */
    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Procesando transacción...");
        this.dialog.show();
    }


    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
            if(success)
                Toast.makeText(CX, "Transacción registrada", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(CX, "Transacción no registrada! Verifique que tenga suficientes monedas para realizar la transacción", Toast.LENGTH_LONG).show();
        }
        // Setting data to list adapter
    }

}
