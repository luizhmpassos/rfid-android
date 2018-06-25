package com.example.luizh.eyehelper2;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    // widgets
    ListView devicelist;
    ArrayList list;
    //final ArrayAdapter adapter;
    ArrayAdapter mAdapter;


    // Variavel de Contexto
    public static final String EXTRA_MESSAGE = "com.example.eyehelper2.MESSAGE";

    // Variaveis Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    String deviceMAC = "";
    public static String EXTRA_ADDRESS = "com.example.eyehelper2.device_address";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicelist = (ListView)findViewById(R.id.listView);
        list = new ArrayList();
        mAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(mAdapter);

        initBluetooth();
    }


    /** Called when the user taps the 'Encontrar' button */
    public void loadFindingView(View view) {
        Intent intent = new Intent(this, FindingActivity.class);
        String message = "Teste 123";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /** Called when the user taps the 'Descrever' button */
    public void loadDescribeView (View view) {
        Intent intent = new Intent(this, DescribeActivity.class);
        String address = deviceMAC;
        intent.putExtra(EXTRA_ADDRESS, address);
        startActivity(intent);
    }


    private void initBluetooth() {
        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }


        pairedDevicesList();
    }


    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
    }


    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            deviceMAC = address;

            if (myBluetooth.isDiscovering()) {
                // Bluetooth is already in modo discovery mode, we cancel to restart it again
                Log.d("Discovery", "Descoberta cancelada");
                myBluetooth.cancelDiscovery();
            }
        }
    };

    public void scanDevices(View view){
        //final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        list.clear();
        //mAdapter.clear();
        devicelist.setAdapter(mAdapter);

        registerReceiver(bReceiver, filter);

        Log.d("Teste", "StartDiscovery()");

        int REQUEST_ACCESS_COARSE_LOCATION = 1;

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION);
        }

        try{
            myBluetooth.startDiscovery();
        }
        catch (Exception e){
            Log.d("Err", e.getMessage());
        }

    }

    private void scanResult(){

        if (list.isEmpty()){
            list.add("Nenhum dispositivo encontrado");
        }

        devicelist.setAdapter(mAdapter);
        Log.d("Status", "Lista atualizada");
    }


    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("Action", action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                //DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                Log.d("Device Found", device.getName());
                list.add(device.getName() + "\n" + device.getAddress()); //Get the device's name and the address

                //devicelist.setAdapter(adapter);
                //devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("Status", "Busca encerrada");
                scanResult();
            }
        }
    };


}

