package com.yingzis.catchyou;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import android.content.Intent;

public class MainActivity extends AppCompatActivity{

    private GameView gameView;
    private int id;
    private String serverString = "3.15.200.225";
    private int port = 9999;

    private DatagramSocket socket = null ;
    private InetAddress host = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Log.d("X and Y size", "X = " + point.x + ", Y = " + point.y);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        gameView = new GameView(this, point.x, point.y);
        setContentView(gameView);


        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor gs = manager.getSensorList(Sensor.TYPE_GYROSCOPE).get(0);
        Sensor ac = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener gslistener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] > 0){
                    gameView.goRight(event.values[1]);
                }
                else if (event.values[1] < 0){
                    gameView.goLeft(event.values[1]);
                }else{
                    gameView.hold();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        SensorEventListener aclistener = new SensorEventListener() {
            private final int interval = 100;
            private long lastTime = System.currentTimeMillis();;
            private float lastX, lastY, lastZ;
            private int shakeThreshold = 800;

            @Override
            public void onSensorChanged(SensorEvent event) {
                long current = System.currentTimeMillis();
                long deltaTime = current - lastTime;
                if (deltaTime < interval) {
                    return;
                }
                lastTime = current;
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                float deltaX = x - lastX;
                float deltaY = y - lastY;
                float deltaZ = z - lastZ;
                lastX = x;
                lastY = y;
                lastZ = z;
                float delta = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / deltaTime * 10000);

                Log.d("debug", Float.toString(delta));

                if (delta > shakeThreshold) {
                    gameView.getPlayer().struggle();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        manager.registerListener(gslistener, gs, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(aclistener, gs, SensorManager.SENSOR_DELAY_GAME);

        try {
            socket = new DatagramSocket();

            host = InetAddress.getByName(serverString);
        } catch( Exception e )
        {
            Log.d("adam", "Exception");
            Log.e("adam", Log.getStackTraceString(e));
        }

        Thread sendData = new Thread() {
            @Override
            public void run() {

                while (true) {
                    byte[] data;
                    if(gameView.getState()){
                        data = new byte[]{(byte) 3,(byte) id};
                    }else{
                        float netSent = gameView.getNetSent();
                        int state = 0;
                        if(gameView.getPlayer().isInvincible()){
                            state = 1;
                        }else if(gameView.getPlayer().isTied()){
                            state = 2;
                        }
                        if (netSent >= 0) {
                            data = new byte[]{(byte) id, (byte) Math.round(gameView.getPlayer().getPos() * 100), (byte) state, (byte) 1, (byte) Math.round(netSent * 100)};
                        } else {
                            data = new byte[]{(byte) id, (byte) Math.round(gameView.getPlayer().getPos() * 100), (byte) state, (byte) 0};
                        }
                    }

                    DatagramPacket sPacket = new DatagramPacket(data, data.length, host, port);

                    try {
                        socket.send(sPacket);
                        gameView.resetNetSent();
                        Log.d("adam", "Packet sent");
                        if(gameView.getState()){
                            break;
                        }
                        Thread.sleep(500);
                    } catch (Exception e) {
                        Log.d("adam", "Exception");
                        Log.e("adam", Log.getStackTraceString(e));
                    }
                }
//                if (socket != null) {
//                    socket.close();
//                }
            }
        };
        sendData.start();

        Thread receiveData = new Thread() {
            @Override
            public void run() {
                try {
                    while(true){
                        byte [] rdata = new byte[1024];
                        DatagramPacket rPacket = new DatagramPacket(rdata, rdata.length);
                        socket.receive(rPacket);
                        Log.d("adam", "Packet received" );
                        int mode = rPacket.getData()[0];
                        if(mode == 1 || mode == 2){
                            if(mode != id){
                                float posO = (float)rPacket.getData()[1] / 100;
                                gameView.getOpponent().setPos(1-posO);
                                int state = rPacket.getData()[2];
                                if(state == 1){
                                    gameView.getOpponent().setInvincible(true);
                                }else if(state == 2){
                                    gameView.getOpponent().setTied(true);
                                }else{
                                    gameView.getOpponent().setInvincible(false);
                                    gameView.getOpponent().setTied(false);
                                }
                            }
                            int shoot = rPacket.getData()[3];
                            if(shoot != 0) {
                                float posS = (float) rPacket.getData()[4] / 100;
                                if (mode == id) {
                                    gameView.addNet(posS, true);
                                } else {
                                    gameView.addNet(posS, false);
                                }
                            }

                        }else if(mode == 3){
                            int pos = rPacket.getData()[1];
                            gameView.addWall(pos);
                        }else if(mode == 4){
                            int type = rPacket.getData()[1];
                            float pos = (float)rPacket.getData()[2] / 100;
                            gameView.addTool(pos,type);
                        }else if(mode == 5){
                            int type = rPacket.getData()[1];
                            if(type == 0){
                                startActivity(new Intent(MainActivity.this, LostPageActivity.class));
                                finish();
                            }else if(type == 1){
                                startActivity(new Intent(MainActivity.this, VictoryPageActivity.class));
                                finish();
                            }
                            break;
                        }
                    }

                } catch( Exception e )
                {
                    Log.d("adam", "Exception");
                    Log.e("adam", Log.getStackTraceString(e));
                }
                finally
                {
//                    if( socket != null ) {
//                        socket.close();
//                    }
                }
            }
        };
        receiveData.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

}
