package com.tfm.sporting.sporting;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class PebbleService extends Service {

    PebbleTarea2 tareaP;
    TextView tv_NumeroPasos;
    String texto;
    public static final String action_envio = "Sporting.ENVIO";

    private static final UUID WATCHAPP_UUID = UUID.fromString("6092637b-8f58-4199-94d8-c606b1e45040");
    private static final String WATCHAPP_FILENAME = "android-example.pbw";

    private static final int
            KEY_BUTTON = 0,
            KEY_VIBRATE = 1,
            KEY_AXISX=2,
            KEY_AXISY=3,
            KEY_AXISZ =4,
            KEY_KM = 5,
            BUTTON_UP = 0,
            BUTTON_SELECT = 1,
            BUTTON_DOWN = 2;

    private Handler handler = new Handler();
    private PebbleKit.PebbleDataReceiver appMessageReciever;

    private int ejeX;
    private int ejeY;
    private int ejeZ;
    public int pasos;

    public PebbleService() {
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int dato = intent.getIntExtra("prueba", 5);
        PebbleTarea2 tarea = new PebbleTarea2();
        tarea.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //int dato = intent.getIntExtra("prueba", 5);
        return null;
    }

    class PebbleTarea2 extends Thread{

        public void run() {

            while(true) {
                if(appMessageReciever == null) {
                    appMessageReciever = new PebbleKit.PebbleDataReceiver(WATCHAPP_UUID) {

                        @Override
                        public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                            // Always ACK
                            PebbleKit.sendAckToPebble(context, transactionId);
                            if(data.getString(KEY_AXISX) != null) {
                                // KEY_BUTTON was received, determine which button
                                final String button = data.getString(KEY_AXISX).toString();
                                texto = button;
                                // Update UI on correct thread
                            }
                            Intent bcIntent = new Intent();
                            bcIntent.setAction(action_envio);
                            bcIntent.putExtra("dato", texto);
                            sendBroadcast(bcIntent);
                        }

                        //TODO: Comunicacion con la actividad
                    };




                    // Add AppMessage capabilities
                    PebbleKit.registerReceivedDataHandler(getApplicationContext(), appMessageReciever);
                }

            }
            }

        }


    }

