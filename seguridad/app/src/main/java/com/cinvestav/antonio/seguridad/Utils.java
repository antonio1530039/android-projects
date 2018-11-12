/*
 * Utils.java         

 * This module is part of the --DET-ABE API--, a Java library  
 * of the SSCABE project (Secure Storage and Sharing of data in the 
 * Cloud by using Attribute based encryption).
 * The DET-ABE library is free software. This library is distributed 
 * on an "AS IS" basis,WITHOUT WARRANTY OF ANY KIND, either expressed 
 * or implied.
 
 * Copyright (c) Miguel Morales-Sandoval (morales.sandoval.miguel@gmail.com)
 * Created:           November 10, 2014
 * Last modification: 
 */

package com.cinvestav.antonio.seguridad;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.jpbc.Element;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Several auxiliary functions required to implement other classes.
 *
 * @author      Miguel Morales-Sandoval    
 * @version     1.0
 * @since       2015-02-20
 */
public class Utils{


   public static String selectSha(int secLevel){
      String SHAAlg = "SHA-1";
      
      if(secLevel == 80)
         SHAAlg = "SHA-1";
      if(secLevel == 112)
         SHAAlg = "SHA-224";
      else if(secLevel == 128)
         SHAAlg = "SHA-256";
      else if (secLevel == 192)
         SHAAlg = "SHA-384";   
      else if (secLevel == 256)
         SHAAlg = "SHA-512";
         
      return SHAAlg;
   
   }
     
   /**
    * shows the size fo the given element in G_0 or G_1 in bits.
    */
   public static void showSizeBits(Element message){
      String str = message.toString();
   
   }
           
   /**
    * @return the file name of the parameters associtaed to a given security level.
    */
   public static String fileNameParams(int secLevel,String type){
      String file = "";
      file = type.toLowerCase() + "_" + secLevel + "_params";
   
      return file;
   }
}