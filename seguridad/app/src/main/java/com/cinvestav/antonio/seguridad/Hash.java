package com.cinvestav.antonio.seguridad;

import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;

import com.cinvestav.antonio.seguridad.Utils;

import com.cinvestav.antonio.seguridad.Files;
public class Hash{ 
   public static String MD2 = "MD2"; 
   public static String MD5 = "MD5"; 
   public static String SHA1 = "SHA-1";
   public static String SHA224 = "SHA-224";
   public static String SHA256 = "SHA-256";
   public static String SHA384 = "SHA-384";
   public static String SHA512 = "SHA-512";  
   static double timing = 0;
   
   public static String toHexadecimal(byte[] digest){ 
      String hash = ""; 
      for(byte aux : digest) { 
         int b = aux & 0xff; 
         if (Integer.toHexString(b).length() == 1) hash += "0"; 
         hash += Integer.toHexString(b); 
      } 
      return hash; 
   }    
   
   public static java.math.BigInteger toBigInteger(byte[] digest){ 
      java.math.BigInteger integer = new java.math.BigInteger(1,digest);
      
      return integer;
   }
   
   /*
   public static String getStringMessageDigest(byte[] buffer, String algorithm){ 
      byte[] digest = null; 
      try { 
         MessageDigest messageDigest = MessageDigest.getInstance(algorithm); 
         messageDigest.reset(); 
         messageDigest.update(buffer); 
         digest = messageDigest.digest(); 
      } 
      catch (NoSuchAlgorithmException ex) { 
         System.out.println("Error creando Digest"); 
      } 
      return toHexadecimal(digest); 
   }
  */
   public static byte[] getStringMessageDigest(byte[] buffer, String algorithm){ 
      byte[] digest = null; 
      try { 
         long starTime = System.nanoTime();
         
         MessageDigest messageDigest = MessageDigest.getInstance(algorithm); 
         messageDigest.reset(); 
         messageDigest.update(buffer); 
         digest = messageDigest.digest(); 
         
         long endTime = System.nanoTime();
         timing = (double)(endTime - starTime)/1000000000.0;
         
      } 
      catch (NoSuchAlgorithmException ex) { 
         System.out.println("Error creando Digest"); 
      } 
      return digest; 
   }

 /*
   public static String getStringMessageDigest(String message, String algorithm){      
      byte[] buffer = message.getBytes(); 
      return getStringMessageDigest(buffer,algorithm); 
   } 
   */
   public static void main(String args[]){
   
      String FILE_NAME = "PrivateLetter.pdf"; //1MB, 10MB, 100MB, 1000MB
      
   
   
   
      byte[] data = Files.readBytesFromFile(FILE_NAME);
      
      
      int secLevel[] = {80,112,128,256};
      
      String[] alg = {MD2,MD5,SHA1,SHA224,SHA256,SHA384,SHA512};
   
   String shaAlg = "";
   
      for(int i = 0; i < secLevel.length; i++){
      
         String s = toHexadecimal(getStringMessageDigest(data,Utils.selectSha(secLevel[i])));
           
         //System.out.println(alg[i] + "(" + FILE_NAME + ") = " + s);
         System.out.println(secLevel[i] + ": " + timing);
         
      }
   
   }
}
