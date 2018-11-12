package com.cinvestav.antonio.seguridad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Start extends AppCompatActivity {

    //Variables para ligar los controles
    Button start, regresar;
    TextView tv_tipo_seguridad, tv_texto_seguridad;
    LinearLayout linear;

    //Varibale de contexto
    Context CX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        //Ligar las variables a los controles
        tv_tipo_seguridad = (TextView) findViewById(R.id.tipo_seguridad);
        tv_texto_seguridad = (TextView) findViewById(R.id.texto_seguridad);
        start = (Button) findViewById(R.id.btn_start);
        regresar = (Button) findViewById(R.id.btn_regresar_s);
        linear = (LinearLayout) findViewById(R.id.ll);

        CX = this;

        //Obtener los parametros enviados desde la actividad MainActivity,java
        Intent intent = getIntent();
        //Obtener parametro de tipo de seguridad
        final String extra_tipo_seguridad = intent.getStringExtra("tipo");

        //Colocar los textos correspondientes en funcion de los parametros

        tv_tipo_seguridad.setText("SEGURIDAD " + extra_tipo_seguridad);

        //Asignar información y color de fondo
        if(extra_tipo_seguridad.equals("BAJA")){
            linear.setBackgroundColor(Color.parseColor("#b60000"));
            //AES 128 bits
            //txt.setText(Html.fromHtml("<sup>9</sup><sub>4</sub>Be"));
            tv_texto_seguridad.setText(Html.fromHtml(" 8.822798913x10<sup>16</sup> AÑOS"));
            //tv_texto_seguridad.setText("Un atacante tardaría 8.822798913x10^16 años en romper este esquema");
        }else if(extra_tipo_seguridad.equals("MEDIA")){
            linear.setBackgroundColor(Color.parseColor("#c1b700"));
            //AES 192 bits
            tv_texto_seguridad.setText(Html.fromHtml(" 1.627519136x10<sup>36</sup> AÑOS"));
            //tv_texto_seguridad.setText("Un atacante tardaría 1.627519136x10^36 años en romper este esquema");
        }else{
            linear.setBackgroundColor(Color.parseColor("#06af00"));
            //AES 256 BITS
            tv_texto_seguridad.setText(Html.fromHtml(" 3.002242897x10<sup>55</sup> AÑOS"));
            //tv_texto_seguridad.setText("Un atacante tardaría 3.002242897x10^55 años en romper este esquema");
        }

        //Definir el evento clic del boton
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Crear intent para iniciar la siguiente actividad
                Intent newActivity = new Intent(Start.this, Informacion.class);
                //Enviar parametros por el intent
                // parametro: tipo ... corresponde al tipo de seguridad (BAJA, MEDIA, ALTA)
                // parametro: milisegundos ... corresponde al cálculo de tiempo en milisegundos realizado por algun procedimiento (a manera de ejemplo se usara: 10)
                newActivity.putExtra("tipo", extra_tipo_seguridad);
                //Iniciar la actividad
                startActivity(newActivity);
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
