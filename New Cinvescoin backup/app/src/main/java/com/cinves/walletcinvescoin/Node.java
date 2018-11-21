package com.cinves.walletcinvescoin;

import amp_new.AppInterface.API;
import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Blockchain.Transaction;
import amp_new.Security.DigitalSignature;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;


public class Node implements Serializable {
    public PublicKey address;
    public PrivateKey privateKey;
    
    
    public Node(){
       //Agregar proveedor
       //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
       Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }


    public boolean verifyTransaction(Transaction transaction){
        return transaction.processTransaction();
    }
    
    public boolean verifyBlock(Block block){
        if(block.hash.equals(block.calculateHash(true)))
            return true;
        else
            return false;
    }
    
    
}
