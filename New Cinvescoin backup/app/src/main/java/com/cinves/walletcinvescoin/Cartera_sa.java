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

import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Blockchain.Transaction;

public class Cartera_sa implements Serializable{

    public String user;
    public String password;
    public String stringAddress;

    KeyPair kp;

    public Cartera_sa() {

    }



}
