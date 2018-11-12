package com.cinvestav.antonio.seguridad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Informacion extends AppCompatActivity {


    //Variables para ligar los controles
    Button regresar, btn_cifrar, btn_descifrar, btn_cambiar_llave;
    TextView tv_tipo_seguridad, tv_tiempo_llave, tv_tiempo_cifrado, tv_tiempo_descifrado;
    LinearLayout linear;
    EditText txt_llave, txt_texto_cifrado, txt_mensaje, txt_mensaje_descifrado;

    int keySize = 128;
    byte[] encryptedMessage = {};
    byte[] key = {};
    byte[] iv = {};
    double time[] = new double[3];

    //Varibale de contexto
    Context CX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion);

        //Ligar las variables a los controles
        tv_tipo_seguridad = (TextView) findViewById(R.id.i_tipo_seguridad);
        regresar = (Button) findViewById(R.id.btn_regresar);
        linear = (LinearLayout) findViewById(R.id.i_linear_layout);

        txt_llave = (EditText) findViewById(R.id.txt_llave);
        btn_cifrar = (Button) findViewById(R.id.btn_cifrar);
        txt_mensaje = (EditText) findViewById(R.id.txt_mensaje);
        txt_texto_cifrado = (EditText) findViewById(R.id.txt_texto_cifrado);
        txt_mensaje_descifrado = (EditText) findViewById(R.id.txt_texto_descifrado);
        btn_descifrar = (Button) findViewById(R.id.btn_descifrar);
        btn_cambiar_llave = (Button) findViewById(R.id.btn_cambiar_llave);

        tv_tiempo_cifrado = (TextView) findViewById(R.id.txt_tiempo_cifrado);
        tv_tiempo_descifrado = (TextView) findViewById(R.id.txt_tiempo_descifrado);
        tv_tiempo_llave = (TextView) findViewById(R.id.txt_tiempo_llave);

        CX = this;

        //Obtener los parametros enviados desde la actividad MainActivity,java
        Intent intent = getIntent();
        //Obtener parametro de tipo de seguridad
        final String extra_tipo_seguridad = intent.getStringExtra("tipo");

        //Colocar los textos correspondientes en funcion de los parametros

        //Asignar información y color de fondo
        if(extra_tipo_seguridad.equals("BAJA")){
            //AES 128 bits
            this.keySize = 128;
            tv_tipo_seguridad.setText("Los mensajes se cifrarán con una llave criptográfica de 128-bits"); //colocar legenda
        }else if(extra_tipo_seguridad.equals("MEDIA")){
            //AES 192 bits
            this.keySize = 192;
            tv_tipo_seguridad.setText("Los mensajes se cifrarán con una llave criptográfica de 192-bits"); //colocar legenda
        }else{
            //AES 256 bits
            this.keySize = 256;
            tv_tipo_seguridad.setText("Los mensajes se cifrarán con una llave criptográfica de 256-bits"); //colocar legenda
        }

        //Generar llave con el tamaño indicado
        AESCipher.genSymmetricKey(keySize);

        //Guardar llave en la clase
        this.key = AESCipher.key;

        //mostrar la llave en el EditText
        txt_llave.setText("0x"+Hash.toHexadecimal(this.key));

        //mostrar tiempo tomado de la llave
        tv_tiempo_llave.setText("Tardó: "+String.format("%.2f", (AESCipher.timing[0]*1000)) + "mseg");

        //Definir el evento clic del boton

        btn_cifrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cifrar mensaje : retorna un array de bytes del mensaje cifrado
                encryptedMessage = AESCipher.encrypt(txt_mensaje.getText().toString().getBytes());
                //Obtener vector de inicialización para el descifrado
                iv = AESCipher.iv;
                //Mostrar mensaje en el cuadro de texto de Mensaje cifrado
                txt_texto_cifrado.setText("0x"+Hash.toHexadecimal(encryptedMessage));
                //mostrar el tiempo tomado
                tv_tiempo_cifrado.setText("Tardó: "+String.format("%.2f", (AESCipher.timing[1]*1000)) + "mseg");
                Toast.makeText(CX, "Mensaje cifrado", Toast.LENGTH_LONG).show();

            }
        });

        btn_cambiar_llave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Generar otra llave del tamaño seleccionado en el tipo de seguridad
                AESCipher.genSymmetricKey(keySize);
                //Guardar nueva llave
                key = AESCipher.key;
                //mostrar nueva llave en editText
                txt_llave.setText("0x"+Hash.toHexadecimal(key));
                //mostrar el tiempo tomado
                tv_tiempo_llave.setText("Tardó: "+String.format("%.2f", (AESCipher.timing[0]*1000)) + "mseg");
            }
        });


        btn_descifrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //Descifrar mensaje y Mostrar mensaje
                    txt_mensaje_descifrado.setText(new String(AESCipher.decrypt(encryptedMessage, iv, key)));
                    //mostrar el tiempo tomado
                    tv_tiempo_descifrado.setText("Tardó: "+String.format("%.2f", (AESCipher.timing[2]*1000)) + "mseg");
                    Toast.makeText(CX, "Mensaje descifrado", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(CX, "Error! llave incorrecta", Toast.LENGTH_LONG).show();
                }

            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Finalizar actividad
                finish();
            }
        });

    }
}
