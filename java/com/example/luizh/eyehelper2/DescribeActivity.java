package com.example.luizh.eyehelper2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DescribeActivity extends AppCompatActivity {

    // Elementos da interface
    Handler mHandler; // Handler para mensagens
    private ProgressDialog progress; // Elemento de tela
    ToneGenerator beep1;
    TextView dynamicText;

    // Variaveis do Bluetooth
    String address = null; // Mac Address da placa bluetooth


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decribe);

        // Inicializacao da comunicacao bluetooth
        Intent intent = getIntent();
        String bluetoothMAC = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);
        address = bluetoothMAC;


        // Elementos de interface
        beep1 = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        dynamicText = findViewById(R.id.textView);
        // Exibe o MAC ADDRESS da placa bluetooth
        TextView textView = findViewById(R.id.textMessage);
        textView.setText(bluetoothMAC);


        // Construcao e configuracao do handler
        // thread independente, sera chamada a cada
        // recebimento de mensagem via bluetooth
        mHandler = new Handler() {
            @Override
            // Controle do texto da tela
            public void handleMessage(Message msg) {
                String texto = (String) msg.obj;
                checkItem(texto);
            }
        };

        // Cria uma conexao com o modulo bluetooth
        new ConnectBT(DescribeActivity.this, address, mHandler).execute();
    }


    public void checkItem(String code){

        ImageView imgInvalid =  findViewById(R.id.imageView);
        ImageView imgValid = findViewById(R.id.imageView2);

        code = code.trim();
        String pattern = "(([A-F0-9]{2}) ){3}([A-F0-9]{2})"; // Padrão do código;

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(code);
        String item = "";

        if(m.find()){
            imgInvalid.setVisibility(View.INVISIBLE);
            imgValid.setVisibility(View.VISIBLE);

            item = getItem(code);

            dynamicText.setText(item);
            beep1.startTone(ToneGenerator.TONE_CDMA_ANSWER, 200);



            // check cadastro;
        }
        else{
            imgInvalid.setVisibility(View.VISIBLE);
            imgValid.setVisibility(View.INVISIBLE);

            dynamicText.setText("Token inválido: [" + code + "]");

            try {
                TimeUnit.SECONDS.sleep(2);
            }catch (Exception ex){
                dynamicText.setText("Pronto");
            }
        }

    }




/*
    private void writeBT()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("Hoy".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }
*/

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    protected String getItem(String itemId){

        Map<String, String> items = new HashMap<String, String>();

        items.put("10 21 1A A4", "Camisa Verde");
        items.put("46 23 1C 7E", "Calça Jeans Clara");
        items.put("86 2C 6C AC", "Camisa estampada amarela");
        items.put("DA 89 5B AA", "Molho de Tomate, Vence: 12 de Julho");
        items.put("DA 88 5B AA", "Ervilha, Validade: 15 de agosto");


        String returnValue = items.get(itemId);

        if (returnValue != null){
            return returnValue;
        }

        return "Item não identificado";

    }



    // posicionamento da class ConnectedThread


    // posicionamento da class ConnectBT

}
