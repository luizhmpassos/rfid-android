package com.example.luizh.eyehelper2;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
{
    private boolean ConnectSuccess = true; //if it's here, it's almost connected
    private ProgressDialog progress;
    private String address;
    private Context activity;
    private Handler mHandler;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;


    public ConnectBT(Context activity, String bt_mac_address, Handler handler){
        this.address = bt_mac_address;
        this.activity = activity;
        this.mHandler = handler;
    }


    @Override
    protected void onPreExecute()
    {
        progress = ProgressDialog.show( activity, "Connecting...", "Please wait!!!");  //show a progress dialog
    }

    @Override
    protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
    {
        try
        {
            if (btSocket == null || !isBtConnected)
            {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();//start connection

                ConnectedThread listener = new ConnectedThread(btSocket, mHandler);
                listener.start();
            }
        }
        catch (IOException e)
        {
            ConnectSuccess = false;//if the try failed, you can check the exception here
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
    {
        super.onPostExecute(result);

        if (!ConnectSuccess)
        {
            //msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
            Log.d("Error", "Connection Failed. Is it a SPP Bluetooth? Try again.");

        }
        else
        {
            //msg("Connected.");
            Log.d("Success", "Conectado ao dispositivo Bluetooth");
            isBtConnected = true;
        }
        progress.dismiss();
    }


    private void msg(String s)
    {
        Toast.makeText(this.activity,s,Toast.LENGTH_LONG).show();
    }
}

