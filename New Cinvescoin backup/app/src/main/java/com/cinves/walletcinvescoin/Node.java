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
    //public int difficultyOfPow;
    
    
    public Node(){
       //Agregar proveedor
       //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
       Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
       //Instancia de firma digital
       //DigitalSignature ds = new DigitalSignature();
       //Generar llaves del nodo
      // KeyPair myKeys = ds.generateKeyPair();
       //Asignar llaves
       //this.address = myKeys.getPublic();
       //this.privateKey = myKeys.getPrivate();
        System.out.println("===========NODE CREATED===================");
       //System.out.println("Public key: " + amp_new.Tools.Utilities.encode(this.address.getEncoded()));
       //System.out.println("Private key: " + amp_new.Tools.Utilities.encode(this.privateKey.getEncoded()));
       System.out.println("==============================");
       //this.difficultyOfPow = difficultyOfPow;
       //blockchain = new Blockchain(difficultyOfPow);
    }
    
    /*public void setP2P(PeerToPeerNetwork p2p){
        this.p2p = p2p;
    }
    
    public void registerNode(Node node){
        this.p2p.nodes.add(node);
    }*/

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
