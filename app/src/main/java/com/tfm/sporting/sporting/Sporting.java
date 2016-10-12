package com.tfm.sporting.sporting;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Sporting extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String coord;
    String sEjeX;
    String sEjeY;
    String sEjeZ;
    int EjeX;
    int EjeY;
    int EjeZ;
    TextView tvPasos;
    TextView tvPulsaciones;

    //State

    private static final int NUM_SAMPLES = 5;
    private ArrayList<String> data;
    private Thread thread;
    private ArrayList z_buffer;
    private int new_sample,old_sample;
    private int min,max,ave,aux;
    private int steps;
    private int steps_each_2s;
    int z_ant = 0;
    int y_ant = 0;
    int x_ant = 0;
    float alpha = 0.7f;
    double A[] = new double[2];
    double angle,q0,q1,q2;
    private int steps_past;
    private float speed,calories,distance;
    private int time;
    private int value;
    private int samples;
    private float weight,height;
    private Iterator<Integer> it;
    private int sample_up,sample_down;

//pulsometro
private final static String TAG = Sporting.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    // ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";


    private IntentFilter mIntentFilter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PebbleService.action_envio)) {
                String prog = intent.getStringExtra("dato");
                StringToInt(prog);
                tvPasos.setText(String.valueOf(steps));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sporting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new_sample = 0;
        old_sample = 0;
        z_buffer = new ArrayList();
        samples = 0;
        time = 0;
        weight = 68.0f;
        height = 1.98f;
        calories = 0.001f;
        distance = 0.001f;

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(PebbleService.action_envio);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent msgIntent = new Intent(getApplicationContext(),  PebbleService.class);
                msgIntent.putExtra("prueba", 10);
                startService(msgIntent);
                //startService(new Intent(getApplicationContext(), PebbleService.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        tvPasos = (TextView) findViewById(R.id.tv_NumeroPasos);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sporting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_actividad) {
            // Handle the camera action
        } else if (id == R.id.nav_actividades) {
            Intent ActividadDV = new Intent(getApplicationContext(), DeviceScanActivity.class);
            startActivity(ActividadDV);
        } else if (id == R.id.nav_conexiones) {
            Intent ActividadBT = new Intent(getApplicationContext(), BTActivity.class);
            startActivity(ActividadBT);

        } else if (id == R.id.nav_perfil) {

        } else if (id == R.id.nav_planes) {
            Intent ActividadPlanes = new Intent(getApplicationContext(), PlanesEntrenamientoActivity.class);
            startActivity(ActividadPlanes);

        } else if (id == R.id.nav_objetivos) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onResume() {
        super.onResume();
        registerReceiver(receiver, mIntentFilter);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mDataField = (TextView) findViewById(R.id.tvNumeroPulsaciones);
        //getActionBar().setTitle(mDeviceName);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    public void StringToInt (String texto){
        int i = 0;
        int sp1 = 0;
        int sp2 = 0;

        while(i<texto.length()){
            if(texto.charAt(i)=='y'){
                sp1 = i;
            }
            if(texto.charAt(i)=='z'){
                sp2 = i;
            }
            i++;
        }

        sEjeX = texto.substring(0,sp1);
        sEjeY = texto.substring(sp1+1,sp2);
        sEjeZ = texto.substring(sp2+1,texto.length());

        EjeX = Integer.parseInt(sEjeX);
        EjeY = Integer.parseInt(sEjeY);
        EjeZ = Integer.parseInt(sEjeZ);

        CoordToSteps(EjeX,EjeY,EjeZ);
    }

    public void CoordToSteps(int x, int y, int z){

        //for (int i = 0; i < NUM_SAMPLES; i++) {

            // Filter signal and rotate Pebble frame to body frame
            z = filterAndRotate(x, y, z);


            // Discard variations lower than 35 mili-g. High pass filter to reduce
            // high frequency noise.
            if (Math.abs(z - new_sample) > 35) {
                old_sample = new_sample;
                new_sample = z;
            } else old_sample = new_sample;


            z_buffer.add(new_sample);

            // Refresh variable threshold each 50 samples (10 msec)
            if (z_buffer.size() == 250) {

                aux = min;
                min = max;
                max = aux;

                it = z_buffer.iterator();
                while (it.hasNext()) {
                    value = it.next();
                    // Update max and min values
                    if (value < min) min = value;
                    if (value > max) max = value;
                }
                z_buffer.clear();
            }

            // Calculate the average
            ave = (min + max) / 2;


            // Cross down to up the threshold
            if (new_sample > ave && new_sample > old_sample) sample_up = samples;


            // Cross up to down the threshold
           if (old_sample > ave && ave > new_sample) {
                sample_down = samples;

                // Check time windows. If samples difference is between 10-100 samples then the step
                // is between 0.2s - 2s for sampling frequency of 50Hz and the step is valid.
                if (sample_down - sample_up > 10 && sample_down - sample_up < 100 && sample_down != 0) {

                    steps++;

                    // Restart variables
                    samples = 0;
                    sample_down = 0;
                    sample_up = 0;
                }
            }

            time++;

            if (time % 100 == 0) {

                steps_each_2s = steps - steps_past;
                steps_past = steps;

                switch (steps_each_2s) {
                    case 0: case 1:
                        distance += steps_each_2s * height / 5;
                        speed = steps_each_2s * height / 10;
                        if (speed != 0) calories += speed * weight / 400;
                        else calories += weight / 1800;
                        break;
                    case 2:
                        distance += steps_each_2s * height / 4;
                        speed = steps_each_2s * height / 8;
                        if (speed != 0) calories += speed * weight / 400;
                        else calories += weight / 1800;

                        break;
                    case 3:
                        distance += steps_each_2s * height / 3;
                        speed = steps_each_2s * height / 9;
                        if (speed != 0) calories += speed * weight / 400;
                        else calories += weight / 1800;
                        break;
                    case 4:
                        distance += steps_each_2s * height / 2;
                        speed = steps_each_2s * height / 4;
                        if (speed != 0) calories += speed * weight / 400;
                        else calories += weight / 1800;
                        break;
                    case 5:
                        distance += steps_each_2s * height / 1.2f;
                        speed = steps_each_2s * height / 2.4f;
                        if (speed != 0) calories += speed * weight / 400;
                        else calories += weight / 1800;
                        break;
                    case 6:
                    case 7:
                        distance += steps_each_2s * height;
                        speed = steps_each_2s * height;
                        if (speed != 0) calories += speed * weight / 400;
                        else calories += weight / 1800;
                        break;
                    default:
                        distance += steps_each_2s * height * 1.2f;
                        speed = steps_each_2s * height * 2.4f;
                        if (speed != 0) calories += speed * weight / 400;
                        else calories += weight / 1800;
                        break;
                }
            }

            // Restart counter each 100 samples (2sec)
            if (samples < 100) samples++;
            else samples = 0;

        }
   // }

    public int filterAndRotate(int x,int y,int z){

        // Filter data using IIR Filter order 1
        x = Math.round((1-alpha)*x+alpha*x_ant);
        x_ant = x;

        y = Math.round((1-alpha)*y+alpha*y_ant);
        y_ant = y;

        z = Math.round((1-alpha)*z+alpha*z_ant);
        z_ant = z;

        // Calcule the angle between accelerometer frame and body frame
        A[0] = -y/Math.sqrt(x*x+y*y);
        A[1] = -A[0]*x/y;
        angle = (Math.acos(-z/Math.sqrt(x*x+y*y+z*z)))/2;

        // Build quaternion Q = {q0,q1,q2,0}
        q0 = Math.cos(angle);
        q1 = Math.sin(angle)*A[0];
        q2 = Math.sin(angle)*A[1];

        // Rotate vector through rotation matrix
        z = (int)(2*q0*(q1*y-q2*x)+z*(1-2*(q1*q1+q2*q2)));

        return z;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            int valor1 = uuid.compareTo("0000180d-0000-1000-8000-00805f9b34fb");
            if(valor1 ==0) {
                gattServiceData.add(currentServiceData);

                ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                        new ArrayList<HashMap<String, String>>();
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas =
                        new ArrayList<BluetoothGattCharacteristic>();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    if(gattCharacteristic.getUuid().toString().compareTo("00002a37-0000-1000-8000-00805f9b34fb")==0) {
                        charas.add(gattCharacteristic);
                        HashMap<String, String> currentCharaData = new HashMap<String, String>();
                        uuid = gattCharacteristic.getUuid().toString();
                        currentCharaData.put(
                                LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                        currentCharaData.put(LIST_UUID, uuid);

                        gattCharacteristicGroupData.add(currentCharaData);
                    }
                }
                mGattCharacteristics.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }
        }

        final BluetoothGattCharacteristic characteristic =
                mGattCharacteristics.get(0).get(0);
        final int charaProp = characteristic.getProperties();

        if (mGattCharacteristics != null) {

            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }
        }


        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        //mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

}


