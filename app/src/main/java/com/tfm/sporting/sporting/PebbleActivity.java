package com.tfm.sporting.sporting;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.Random;
import java.util.UUID;

public class PebbleActivity extends Activity {

    private static final UUID WATCHAPP_UUID = UUID.fromString("6092637b-8f58-4199-94d8-c606b1e45040");
    private static final String WATCHAPP_FILENAME = "android-example.pbw";

    private static final int
            KEY_BUTTON = 0,
            KEY_VIBRATE = 1,
            KEY_AXISX=2,
            KEY_AXIS=3,
            KEY_AXISZ =4,
            KEY_KM = 5,
            BUTTON_UP = 0,
            BUTTON_SELECT = 1,
            BUTTON_DOWN = 2;

    private Handler handler = new Handler();
    private PebbleDataReceiver appMessageReciever;
    private TextView whichButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pebble);

        // Add vibrate Button behavior
        Button vibrateButton = (Button)findViewById(R.id.button_vibrate);
        vibrateButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Send KEY_VIBRATE to Pebble
                PebbleDictionary out = new PebbleDictionary();
                Random random = new Random();
                int numero = random.nextInt();
                out.addInt32(KEY_VIBRATE, numero);
                PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, out);
            }

        });
        // Add output TextView behavior
        whichButtonView = (TextView)findViewById(R.id.which_button);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PebbleTarea2 tarea2 = new PebbleTarea2();
        tarea2.start();

        PebbleTarea1 tarea = new PebbleTarea1();
        tarea.execute();


        // Define AppMessage behavior

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister AppMessage reception
        if(appMessageReciever != null) {
            unregisterReceiver(appMessageReciever);
            appMessageReciever = null;
        }
    }

    private class PebbleTarea1 extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            //if(appMessageReciever == null) {
                appMessageReciever = new PebbleDataReceiver(WATCHAPP_UUID) {

                    @Override
                    public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                        // Always ACK
                        PebbleKit.sendAckToPebble(context, transactionId);

                        // What message was received?
                        if(data.getInteger(KEY_AXISX) != null) {
                            // KEY_BUTTON was received, determine which button
                            final int button = data.getInteger(KEY_AXISX).intValue();
                            final String b = String.valueOf(button);
                            // Update UI on correct thread
                            whichButtonView.setText(b);


                        }
                    }

                };

            PebbleDictionary out = new PebbleDictionary();
            Random random = new Random();
            int numero = random.nextInt();
            out.addInt32(KEY_VIBRATE, numero);
            PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, out);

                // Add AppMessage capabilities
                PebbleKit.registerReceivedDataHandler(getApplicationContext(), appMessageReciever);

            //}
            return resul;
        }

        protected void onPostExecute(Boolean result) {

        }
    }

     class PebbleTarea2 extends Thread{

         public void run() {

            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean resul = true;
                PebbleDictionary out = new PebbleDictionary();
                Random random = new Random();
                int numero = random.nextInt();
                out.addInt32(KEY_VIBRATE, numero);
                PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, out);
            }

         }


     }

}