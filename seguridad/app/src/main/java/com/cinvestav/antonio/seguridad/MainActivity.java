package com.cinvestav.antonio.seguridad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    //Variables para ligar los controles
    Button seg_baja, seg_media, seg_alta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ligar las variables a los controles

        seg_baja = (Button) findViewById(R.id.btn_baja);
        seg_media = (Button) findViewById(R.id.btn_media);
        seg_alta = (Button) findViewById(R.id.btn_alta);

        //Definir eventos de clic para cada boton

        seg_baja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Crear intent para iniciar la siguiente actividad
                Intent newActivity = new Intent(MainActivity.this, Start.class);
                //Enviar parametros por el intent
                // parametro: tipo ... corresponde al tipo de seguridad (BAJA, MEDIA, ALTA)
                // parametro: tiempo ... corresponde al cálculo de tiempo realizado por algun procedimiento (a manera de ejemplo se usara: 10)
                newActivity.putExtra("tipo", "BAJA");
                //newActivity.putExtra("tiempo", "10");
                //Iniciar la actividad
                startActivity(newActivity);
            }
        });

        seg_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Crear intent para iniciar la siguiente actividad
                Intent newActivity = new Intent(MainActivity.this, Start.class);
                //Enviar parametros por el intent
                // parametro: tipo ... corresponde al tipo de seguridad (BAJA, MEDIA, ALTA)
                // parametro: tiempo ... corresponde al cálculo de tiempo realizado por algun procedimiento (a manera de ejemplo se usara: 20)
                newActivity.putExtra("tipo", "MEDIA");
                //newActivity.putExtra("tiempo", "20");
                //Iniciar la actividad
                startActivity(newActivity);
            }
        });

        seg_alta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Crear intent para iniciar la siguiente actividad
                Intent newActivity = new Intent(MainActivity.this, Start.class);
                //Enviar parametros por el intent
                // parametro: tipo ... corresponde al tipo de seguridad (BAJA, MEDIA, ALTA)
                // parametro: tiempo ... corresponde al cálculo de tiempo realizado por algun procedimiento (a manera de ejemplo se usara: 30)
                newActivity.putExtra("tipo", "ALTA");
                //newActivity.putExtra("tiempo", "30");
                //Iniciar la actividad
                startActivity(newActivity);
            }
        });

    }
}
