/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cinves.walletcinvescoin;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import PeerToPeerNet.MinerNode;
import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Blockchain.Transaction;

public class Carterita implements Serializable{

    public PublicKey address;
    public PrivateKey privateKey;
    public String user;
    public String password;

    KeyPair kp;

    public Carterita() {

    }



}
