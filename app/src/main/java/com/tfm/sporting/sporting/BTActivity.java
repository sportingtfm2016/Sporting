package com.tfm.sporting.sporting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;

import java.util.UUID;

public class BTActivity extends AppCompatActivity {
    final UUID appUuid = UUID.fromString("6092637b-8f58-4199-94d8-c606b1e45040");
    private TextView tvEstadoConectado;
    private Switch swPebble,swPulsometro;
    public String mDeviceName;
    public String mDeviceAddress;
    public boolean cPebble = false;
    public boolean cPulsometro = false;
    boolean conectado;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String FLAG_PEBBLE = "BTPEBBLE";
    public static final String FLAG_PULSOMETRO = "BTPULSOMETRO";
    private Button btnAceptarConfigBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        Context context = getApplicationContext();

        btnAceptarConfigBT = (Button) findViewById(R.id.btnAceptarConfigBT);
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
                        cPebble = true;
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
                    cPulsometro = true;
                    //Llamar a la función para activar
                    Intent ActividadDV = new Intent(getApplicationContext(), DeviceScanActivity.class);
                    startActivity(ActividadDV);
                } else {
                    //Poner 0 en flag de coexionpulsometro
                }
            }
        });

        //check the current state before we display the screen
        if(swPebble.isChecked()){
            if(conectado) {
                tvEstadoConectado.setText("CONECTADO Y HABILITADO");
                cPebble = true;
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

        //check the current state before we display the screen
        if(swPulsometro.isChecked()){
            cPulsometro = true;
            //Llamar a la función para activar
            Intent ActividadDV = new Intent(getApplicationContext(), DeviceScanActivity.class);
            startActivity(ActividadDV);
        } else {

        }

        //Tarea boton Login
        btnAceptarConfigBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), Sporting.class);
                intent.putExtra(Sporting.FLAG_PEBBLE, cPebble);
                intent.putExtra(Sporting.FLAG_PULSOMETRO, cPulsometro);
                intent.putExtra(Sporting.EXTRAS_DEVICE_NAME, mDeviceName);
                intent.putExtra(Sporting.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                startActivity(intent);
            }
        });
    }

    public void onResume(){
        super.onResume();
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        cPulsometro = true;
    }

}
