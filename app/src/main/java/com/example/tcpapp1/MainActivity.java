package com.example.tcpapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.app.Dialog;
import android.content.Intent;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText editTextActivityMain1,editTextActivityMain2,editTextActivityMain3;
    Button buttonActivityMain1,buttonActivityMain2,buttonActivityMain3;
    TextView textViewActivityMain4;
    CheckBox checkBoxActivityMain1,checkBoxActivityMain2;
    Socket socket;

    MyHandler myHandler;
    private OutputStream outputStream;
    private InputStream inputStream;

    byte[] TcpReceiveData = new byte[1024];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myHandler = new MyHandler();


        editTextActivityMain1=findViewById(R.id.editTextActivityMain1);
        editTextActivityMain2=findViewById(R.id.editTextActivityMain2);
        editTextActivityMain3 = findViewById(R.id.editTextActivityMain3);

        buttonActivityMain1=findViewById(R.id.buttonActivityMain1);
        buttonActivityMain2=findViewById(R.id.buttonActivityMain2);

        buttonActivityMain3 = findViewById(R.id.buttonActivityMain3);
        textViewActivityMain4 = findViewById(R.id.textViewActivityMain4);
        checkBoxActivityMain1 = findViewById(R.id.checkBoxActivityMain1);
        checkBoxActivityMain2 = findViewById(R.id.checkBoxActivityMain2);

        buttonActivityMain1.setText("连接");

        buttonActivityMain1.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View view){
                if(buttonActivityMain1.getText().toString() == "连接") {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket = new Socket(editTextActivityMain1.getText().toString(),Integer.valueOf(editTextActivityMain2.getText().toString()));
                                if (socket.isConnected()) {
                                    Log.e("MainActivity", "isconnected");
                                    Message msg = myHandler.obtainMessage();
                                    msg.what = 1;
                                    myHandler.sendMessage(msg);

                                    outputStream = socket.getOutputStream();
                                    inputStream = socket.getInputStream();
                                    TcpClientReceive();
                                }

                            } catch (Exception e) {
                                Log.e("MainActivity", e.toString());

                            }

                        }
                    }).start();
                }
                else {
                    try {Log.e("MainActivity",  "socket exception");socket.close();}  catch(Exception e) { }
                    try {Log.e("MainActivity", "inputstream exception");inputStream.close(); } catch (Exception e) {}

                    buttonActivityMain1.setText("连接");
                }
            }
        });

        buttonActivityMain2.setOnClickListener((view) {
            new Thread((Runnable) ()   {


            }
        }

    }



    public void TcpClientReceive(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(socket.isConnected()){
                    try {
                        int TcpReceiveDataLen = 0;
                        try {
                            TcpReceiveDataLen = inputStream.read(TcpReceiveData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(TcpReceiveDataLen !=-1){
                            byte[] Buffer = new byte[TcpReceiveDataLen];
                            System.arraycopy(TcpReceiveData,0,Buffer,0,TcpReceiveDataLen);
                            Message msg = myHandler.obtainMessage();
                            msg.what = 2;
                            msg.obj = Buffer;
                            myHandler.sendMessage(msg);
                        }
                        else {
                            Message msg = myHandler.obtainMessage();
                            msg.what = 9;
                            myHandler.sendMessage(msg);
                            Log.e("mainActivety", "receive no data");
                            //break;
                        }
                    }
                    catch (Exception e) {
                        Message msg = myHandler.obtainMessage();
                        msg.what = 9;
                        myHandler.sendMessage(msg);
                        Log.e("mainActivety", "no connect");
                        //break;

                    }
                }
            }
        }).start();
    }

    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                buttonActivityMain1.setText("断开");
            }
            else if (msg.what ==2) {
                try{
                    byte[] TcpReadData = (byte[]) msg.obj;
                    if(checkBoxActivityMain1.isChecked()) {
                        textViewActivityMain4.append(byteToHexStr(TcpReadData));
                    }
                    else {
                        textViewActivityMain4.append(new String(TcpReadData));
                    }
                    Log.e("MainActivity",new String(TcpReadData));
                }
                catch(Exception e) {}
            }
            else if (msg.what == 9) {
                try{ socket.close();} catch (Exception e ){}
                try { inputStream.close();}  catch(Exception e) {}
                buttonActivityMain1.setText("连接");
                Log.e("MainActivity","和服务器断开连接");
            }
        }
    }

    /**
     * 16进制byte转16进制String--用空格隔开
     * @param bytes
     * @return
     */
    public static String byteToHexStr(byte[] bytes)
    {
        String str_msg = "";
        for (int i = 0; i < bytes.length; i++){
            str_msg = str_msg + String.format("%02X",bytes[i])+" ";
        }
        return str_msg;
    }
}
