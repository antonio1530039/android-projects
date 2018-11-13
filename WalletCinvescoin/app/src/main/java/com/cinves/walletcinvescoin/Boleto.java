/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cinves.walletcinvescoin;

import amp_new.Security.Hash;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author cti
 */
public class Boleto implements Serializable {
    
    public String folio;
    public String periodo;
    public String ruta;
    public double precio;
    public PublicKey vendedor;
    public String fechaCompra;
    public byte[] vendedorRaw;
    public Boleto(String periodo, String ruta, double precio, PublicKey vendedor){
        this.periodo = periodo;
        this.ruta = ruta;
        this.precio = precio;
        this.vendedor = vendedor;
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        //LocalDateTime now = LocalDateTime.now();
        //this.fechaCompra = dtf.format(now);
        this.fechaCompra = "2018";
        this.folio = obtenerFolio();
        Hash h = new Hash();
        System.out.println("Folio: " + this.folio);
    }
    
    public Boleto(){
    }
    
    public String obtenerFolio(){
        Hash h = new Hash();
        return h.getHexValue((this.periodo+ this.ruta + Double.toString(precio) + this.vendedor.getEncoded().toString() + this.fechaCompra).toString());
    }

    /* @Override
    public boolean insert() {
        String sql = "INSERT INTO boleto(folio, periodo, ruta, precio, vendedor, fechaCompra) VALUES(?,?,?,?,?,?)";
        //Serializacion de objeto PublicKey
        byte[] vendedor;
        try {
            vendedor = super.serializeObjectForStorage(this.vendedor);
            try (Connection conn = super.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, this.folio);
                pstmt.setString(2, this.periodo);
                pstmt.setString(3, this.ruta);
                pstmt.setDouble(4, this.precio);
                pstmt.setBytes(5, vendedor);
                pstmt.setString(6, this.fechaCompra);
                pstmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        } catch (IOException ex) {
           return false;
        }
    }*/

    //@Override
    public PublicKey deserializeObject(byte[] path) {
        PublicKey e = null;
      try {
         InputStream myInputStream = new ByteArrayInputStream(path);
         ObjectInputStream in = new ObjectInputStream(myInputStream);
         
         e = (PublicKey) in.readObject();
         in.close();
         myInputStream.close();
         return e;
      } catch (IOException i) {
         //i.printStackTrace();
         return null;
      } catch (ClassNotFoundException c) {
          System.out.println("Error intentando deserializar el objeto");
         //c.printStackTrace();
         return null;
      }
    }
    
}
