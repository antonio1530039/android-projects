/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cinves.walletcinvescoin;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import org.spongycastle.pqc.math.ntru.util.Util;

import amp_new.AppInterface.MinerNode;
import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Blockchain.Transaction;
import amp_new.Security.DigitalSignature;
import amp_new.Security.Utilities;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

public class Cartera extends com.cinves.walletcinvescoin.MinerNode implements Serializable{
    public int secLevel;
    public String algorithm;
    public int transaccionesRealizadas;
    public Cartera(int secLevel, String algorithm) {
        super();
        this.secLevel = secLevel;
        this.algorithm = algorithm;
        this.transaccionesRealizadas = 0;
    }

    public boolean verifyFirstIncentive(){
        Blockchain bc = Utilities.readMyBlockchain(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin/blockchain.x");
        for (Block b : bc.chain) {
            for (Transaction t : b.transactions) {
                if (t.publicKeySender == null && !t.concept.getClass().isInstance(new Boleto()) && Utilities.encode(t.publicKeyReceiver.getEncoded()).equals(
                        Utilities.encode(this.address.getEncoded()))
                ) {
                    if( Double.valueOf(t.concept.toString()) == 500.00)
                        return true;
                }

            }
        }

        return false;
    }


    public Transaccion makeTransfer(PublicKey address, double value){
        if(!Utilities.encode(address.getEncoded()).equals(Utilities.encode(this.address.getEncoded()))){
            this.transaccionesRealizadas++;
            return new Transaccion(this, address, value, this.transaccionesRealizadas, secLevel, algorithm);
        }else{
            return null;
        }

    }

     public double miSaldo() {
        //Leer cadena de bloques
        Blockchain bc = Utilities.readMyBlockchain(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/cinvescoin/blockchain.x");
        if(bc == null)
            return 0.0;
        double balance = 0.0;
        for (Block b : bc.chain) {
            for (Transaction t : b.transactions) {
                if (t.publicKeySender != null) {
                    if (Utilities.encode(t.publicKeySender.getEncoded()).equals(Utilities.encode(address.getEncoded())) ) {
                        balance -= getConceptValue(t.concept);
                    } else if (Utilities.encode(t.publicKeyReceiver.getEncoded()).equals(Utilities.encode(address.getEncoded()))  ) {
                        balance += getConceptValue(t.concept);
                    }
                } else if (Utilities.encode(t.publicKeyReceiver.getEncoded()).equals(Utilities.encode(address.getEncoded())) ) {
                   balance += getConceptValue(t.concept);
                }
            }
        }
        return balance;
    }

    public void setKeyPair(PublicKey pk, PrivateKey prK){
        this.address = pk;
        this.privateKey = prK;
    }
    
    public double getConceptValue(Object concept){
        if(concept.getClass().isInstance(new Boleto()))
            return ((Boleto) concept).precio;
        else
            return Double.parseDouble(concept.toString());
    }

    public Transaccion comprarBoleto(Boleto concept) {
        if(!Utilities.encode(concept.vendedor.getEncoded()).equals(Utilities.encode(this.address.getEncoded()))){
            Transaccion t = new Transaccion(this, concept.vendedor, concept, this.transaccionesRealizadas, this.secLevel, this.algorithm);
            this.transaccionesRealizadas++;
            return t;
        }else{
            return null;
        }

    }


    public Transaccion getFirstIncentive(){
        Transaccion t = new Transaccion(this.address, 500.00, this.transaccionesRealizadas, this.secLevel, this.algorithm);
        this.transaccionesRealizadas++;
        return t;
    }



    @Override
    public Transaction rewardOfPow() {
        return new Transaccion(this.address, 50.00, 0, this.secLevel, this.algorithm);
    }

    @Override
    public void createBlock(API api) {
        Block block = new Block(112); //crear bloque
        //Agregar transacciones al bloque
        for (Transaction t : super.transactionsSaved) {
            block.addTransaction(t);
        }
        //Agregar hash previo
        block.prevHash = api.getLastHashInChain();
        //Minar bloque
        block.mineBlock(api.getDifficultyOfPow());
        //Enviar bloque
        api.newBlockMined(this, block);
    }
    
    @Override
    public void registerTransaction(Transaction transaction, API api) {
        this.transactionsSaved.add(transaction);
        createBlock(api);
        this.transactionsSaved.clear();
    }


}
