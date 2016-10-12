package com.tfm.sporting.sporting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;

import java.util.UUID;

public class BTActivity extends AppCompatActivity {
    final UUID appUuid = UUID.fromString("6092637b-8f58-4199-94d8-c606b1e45040");
    private TextView tvEstadoConectado;
    private Switch swPebble,swPulsometro;
    boolean conectado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        Context context = getApplicationContext();
        boolean isConnected = PebbleKit.isWatchConnected(context);
        tvEstadoConectado = (TextView)findViewById(R.id.tvEstadoConectado);
        swPebble = (Switch) findViewById(R.id.swPebble);
        swPulsometro = (Switch) findViewById(R.id.swPulsometro);


        if(isConnected) {
            // Launch the sports app
            PebbleKit.startAppOnPebble(context, appUuid);
            tvEstadoConectado.setText("CONECTADO");
            conectado = true;

        } else {
            tvEstadoConectado.setText("NO CONECTADO");
        }

        swPebble.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    if(conectado) {
                        tvEstadoConectado.setText("CONECTADO Y HABILITADO");
                    }
                    else{
                        tvEstadoConectado.setText("NO CONECTADO");
                    }
                } else {
                    if(conectado) {
                        tvEstadoConectado.setText("CONECTADO Y DESHABILITADO");
                    }
                    else{
                        tvEstadoConectado.setText("NO CONECTADO");
                    }
                }
            }
        });

        swPulsometro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    //Llamar a la función para activar
                    Intent ActividadDV = new Intent(getApplicationContext(), DeviceScanActivity.class);
                    startActivity(ActividadDV);
                } else {
                    //Poner 0 en flag de coexionpulsometr
                }
            }
        });

        //check the current state before we display the screen
        if(swPebble.isChecked()){
            if(conectado) {
                tvEstadoConectado.setText("CONECTADO Y HABILITADO");
            }
            else{
                tvEstadoConectado.setText("NO CONECTADO");
            }
        } else {
            if(conectado) {
                tvEstadoConectado.setText("CONECTADO Y DESHABILITADO");
            }
            else{
                tvEstadoConectado.setText("NO CONECTADO");
            }
        }

        //TODO : CONEXION CON PULSOMETRO

    }
}
