package com.example.luizh.eyehelper2;


import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;

class ConnectedThread extends Thread {

    private final InputStream mmInStream;
    private Handler handler;

    // Codigos de tipos de mensagem
    static final int MESSAGE_READ = 1;
    static final int MESSAGE_ERROR = 2;


    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        InputStream tmpIn = null;

        this.handler = handler;

        // Get the input stream, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        while (true) {
            try {
                // Read from the InputStream
                int bytesread = 0;
                String code = "";
                String tmpRead = "";

                // Send the obtained bytes to the UI activity
                do{
                    bytes = mmInStream.read(buffer);
                    bytesread += bytes;
                    tmpRead = new String(buffer, 0, bytes - 1, "UTF-8");

                    Log.d("ReadBuffer", tmpRead + ": " + Integer.toString(bytes));
                    int character = buffer[bytes-1];
                    Log.d("Bytes",  Integer.toString(character));

                    code = code + " " + tmpRead;
                    Log.d("Code", code);
                }while(tmpRead.indexOf((char)(10)) <= 0 && tmpRead.indexOf((char)(13)) <= 0);

                code = code.trim();
                Log.d("Final Code", code);

                Message mensagem = new Message();
                mensagem.what = MESSAGE_READ;
                mensagem.obj = code.toUpperCase();

                handler.sendMessage(mensagem);

            } catch (IOException e) {
                Message mensagem = new Message();
                mensagem.what = MESSAGE_ERROR;
                mensagem.obj = "Erro";
                this.interrupt();
                break;
            }
        }

    }
}
