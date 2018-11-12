/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cinves.walletcinvescoin;

import AppInterface.API;
import PeerToPeerNet.MinerNode;
import PeerToPeerNet.Node;
import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Blockchain.Transaction;
import amp_new.Security.DigitalSignature;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

public class Cartera extends MinerNode implements Serializable{

    public int transaccionesRealizadas;

    public dbConnect db;

    public Cartera() {
        super();
        db = new dbConnect();
        this.transaccionesRealizadas = 0;
    }

    public double miSaldo() {
        //Leer cadena de bloques
        Blockchain bc = super.api.blockchain;
        double balance = 0.0;
        for (Block b : bc.chain) {
            for (Transaction t : b.transactions) {
                if (t.publicKeySender != null) {
                    if (t.publicKeySender == address) {
                        balance -= getConceptValue(t.concept);
                    } else if (t.publicKeyReceiver == address) {
                        balance += getConceptValue(t.concept);
                    }
                } else if (t.publicKeyReceiver == address) {
                   balance += getConceptValue(t.concept);
                }

            }
        }
        return balance;
    }
    
    public double getConceptValue(Object concept){
        if(concept.getClass().isInstance(new Boleto()))
            return ((Boleto) concept).precio;
        else
            return Double.parseDouble(concept.toString());
    }

    public Transaccion comprarBoleto(Boleto concept) {
        Transaccion t = new Transaccion(this, concept.vendedor, concept, this.transaccionesRealizadas);
        this.transaccionesRealizadas++;
        return t;
    }

    @Override
    public Transaction rewardOfPow() {
        System.out.println("Giving reward of PoW*-*--*--*-*-*--**--*-*-*-*--*-**-*--*-*");
        return new Transaccion(this.address, 100.00, 0);
    }

    @Override
    public void createBlock() {
        Block block = new Block(); //crear bloque
        //Agregar transacciones al bloque
        for (Transaction t : super.transactionsSaved) {
            block.addTransaction(t);
        }
        //Agregar hash previo
        block.prevHash = super.api.getLastHashInChain();
        //Minar bloque
        block.mineBlock(super.api.getDifficultyOfPow());
        //Enviar bloque
        super.api.newBlockMined(this, block);
        
        System.out.println("SALDO DE VENDEDOR FOR CREATEBLOCK: " + this.miSaldo());
    }
    
    @Override
    public void registerTransaction(Transaction transaction) {
        this.transactionsSaved.add(transaction);
        createBlock();
        this.transactionsSaved.clear();
    }


}
