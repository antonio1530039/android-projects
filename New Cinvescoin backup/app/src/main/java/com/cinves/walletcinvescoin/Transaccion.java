/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cinves.walletcinvescoin;

import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Blockchain.Transaction;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Transaccion extends Transaction implements Serializable{

    Cartera from;

    public Transaccion(Cartera from, PublicKey to, Boleto concept, int sequence) {
        super(from.address, to, concept, sequence, from.privateKey);
        this.from = from;
    }

    public Transaccion(Cartera from, PublicKey to, double concept, int sequence) {
        super(from.address, to, concept, sequence, from.privateKey);
        this.from = from;
    }

    
    /**
     * Constructor para dar transaccion como incentivo o regalo
     * @param to
     * @param concept
     * @param sequence 
     */
    public Transaccion(PublicKey to, double concept, int sequence) {
        super(to, concept, sequence);
        this.from = null;
    }

    @Override
    public boolean processTransaction() {
        return super.processTransaction();
    }

    @Override
    public boolean addressHasEnoughResources(PublicKey from, Object concept) {

        if (this.from == null) {
            return true;
        } else {
            double saldo = this.from.miSaldo();
            if (concept.getClass().isInstance(new Boleto())) {
                return saldo >= ((Boleto) concept).precio;
            } else {
                return saldo >= Double.parseDouble(concept.toString());
            }
        }

    }

}
