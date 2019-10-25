package com.yingzis.catchyou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WaitingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        back = findViewById(R.id.cancelButton);

        back.setOnClickListener(this);


        Thread sendDate = new Thread() {
            @Override
            public void run() {

                String serverString = "3.15.200.225";
                int port = 9999;

                DatagramSocket socket = null ;


                try {
                    socket = new DatagramSocket();

                    InetAddress host = InetAddress.getByName(serverString);
                    byte [] ini = {(byte)0};
                    DatagramPacket iniPacket = new DatagramPacket( ini, ini.length, host, port );
                    socket.send(iniPacket);

                    byte [] ready = new byte[1024];
                    DatagramPacket rdyPacket = new DatagramPacket(ready, ready.length);
                    socket.receive(rdyPacket);
                    while(rdyPacket.getData().length==0 || rdyPacket.getData()[0] == 0){
                        socket.receive(rdyPacket);
                    }
                    if(rdyPacket.getData()[0] == 0){
                        Log.d("adam", "Game in running");
                        return;
                    }
                    Log.d("adam", "as player"+rdyPacket.getData()[0]);
                    toGame(rdyPacket.getData()[0]);

                } catch( Exception e )
                {
                    Log.d("adam", "Exception");
                    Log.e("adam", Log.getStackTraceString(e));
                }
                finally
                {
                    if( socket != null ) {
                        socket.close();
                    }
                }
            }
        };
        sendDate.start();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancelButton:
                finish();
                break;
        }
    }

    private void toGame(int id){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }
}
