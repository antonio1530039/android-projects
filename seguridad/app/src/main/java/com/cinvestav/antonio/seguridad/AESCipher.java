/*
 * AESCipher.java         

 * This module is part of the --DET-ABE API--, a Java library for 
 * part of the SCABE project (Secure Storage and Sharing of data in 
 * the Cloud by using Attribute based encryption).
 * The DET-ABE library is free software. This library is distributed 
 * on an "AS IS" basis,WITHOUT WARRANTY OF ANY KIND, either expressed 
 * or implied.
 
 * Copyright (c) Miguel Morales-Sandoval (morales.sandoval.miguel@gmail.com)
 * Created:    02 de octubre de 2014
 * Last modification: 05 junio de 2015 (CBC mode)
 */

package com.cinvestav.antonio.seguridad;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.math.BigInteger;

import com.cinvestav.antonio.seguridad.Utils;

/**
 * This class performs AES-CBC encryption and decryption over an array of bytes.
 *
 * @author      Miguel Morales-Sandoval    
 * @version     1.0
 * @since       2014-11-19
 */ 
public class AESCipher{
   /**
    * the initialization vector needed in CBC mode of AES encryption.
    */
   public static byte[] iv = null;
   
   /**
    * timing for: 
    * <p> timing[0] - key generation </p>
    * <p> timing[1] - AES-CBC encryption </p>
    * <p> timing[2] - AES-CBC decryption </p>        
    *
    */
   public static double timing[] = new double[3];
   
   /**
    * current AES-key as a {@link SecretKey} object
    */
   public static SecretKey skey = null;
   
   /**
    * the current AES-key as a byte array
    */
   public static byte[] key = null;
   
   private static KeyGenerator kgen;
   private static Cipher cipher; 
   
   static{
      try{
         kgen = KeyGenerator.getInstance("AES");
         cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");     //CBC
      }
      catch(Exception e){
         kgen = null;
         cipher = null;
         System.out.println("AES MODULE: EXCEPTION WHEN INITIALIZING MODULE");
      }
   }

      
   /**
    * generates a new random AES-key for a specific security level. 
    *
    * @param secLevel the security level to be supported. It defines the key-lenght (128, 192, 256).
    */                
   public static boolean genSymmetricKey(int secLevel){
      
      int level = 0;      
      key = null;
      
      if(secLevel == 80 || secLevel == 112 || secLevel == 128)
         level = 128;
      else if (secLevel == 192)
         level = 192;
      else if(secLevel == 256)
         level = 256;
      else
         return false;
         
      
      try{
         kgen.init(level); // security level
         
         long startTime = System.nanoTime(); 
         skey = kgen.generateKey();
         long endTime = System.nanoTime(); 
         timing[0] = (double)(endTime - startTime)/1000000000.0;
         
         key = skey.getEncoded();
         System.out.println("AES MODULE: AES key of " + level + "-bit created for the " + secLevel + "-bit security level.\n");
         return true;  
      }
      catch(Exception e){
         System.out.println("AES MODULE: EXCEPTION when generating the " + secLevel + "-bit AES-key...");
         e.printStackTrace();
         System.out.println("---- END -----");
         return false;
      }      
   }

  
/**
 * encrypts a byte-array of data using the current AES-key in AES-CBC mode.
 * 
 * @param data the data to be encrypted
 * @return a byte array corresponding to the data encrypted in CBC mode.
 */
   public static byte[] encrypt(byte data[]){
   
      if (key == null)
         return null;
      
      byte[] cipherText = null;
      
      try{
         long startTime = System.nanoTime();         
         cipher.init(Cipher.ENCRYPT_MODE, skey); 
         cipherText = cipher.doFinal(data);
         long endTime = System.nanoTime();
         
         timing[1] = (double)(endTime - startTime)/1000000000.0;
         
         iv = cipher.getIV();
                  
         System.out.println("   DONE! " + data.length + " bytes encrypted with \n\t key = " +
                             new BigInteger(1,key) + 
                             "\n\t iv = " +
                             new BigInteger(1,iv) + 
            
                             "  \n\t" + cipherText.length + " bytes produced as ciphertext.\n");
         
                                   
      }
      catch(Exception e){
         System.out.println("AES MODULE: EXCEPTION");
         e.printStackTrace();
         System.out.println("---------------------------");
      }
      return cipherText;
   }
   
/**
 * decrypts a byte-array using AES in CBC mode.
 *
 * @param cipherText the encrypted data
 * @param iv the initialization vector using in encryption
 * @param sessionKey the AES-key used during encryption
 * @return the data decrypted as a byte-array
 */
   public static byte[] decrypt(byte[] cipherText,  byte[] iv, byte[] sessionKey){
    
      byte[] data = null;
      System.out.println("AES MODULE: DEcrypting " + cipherText.length + ", with key: \n\t" +
                             new BigInteger(1,sessionKey) + 
                              ",\n\t and IV = " +
                             new BigInteger(1,iv) + ",\n\t  wait...");      
                             
      try{
      
         long startTime = System.nanoTime();
         
         IvParameterSpec ivParam = new IvParameterSpec(iv);
         SecretKeySpec skeySpec = new SecretKeySpec(sessionKey, "AES");
         cipher.init(Cipher.DECRYPT_MODE,skeySpec, ivParam);  //CBC
         data = cipher.doFinal(cipherText);
         
         long endTime = System.nanoTime();
         timing[2] = (double)(endTime - startTime)/1000000000.0;
         
         System.out.println("   DONE!. " + data.length + " bytes produced from DEcryption." );
      
      }
      catch(Exception e){
         e.printStackTrace();
      }
      return data;
   }   
         
}