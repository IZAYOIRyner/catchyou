package com.yingzis.catchyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Player extends Person{

    private int maxX;
    private int minX;
    private int maxY;
    private int minY;
    private int margin = 16;
    private boolean isGoLeft, isGoRight;
    private float speed;
    private Context context;
    private int screenX, screenY;

    private int netNum;

    private long tiedTime;
    private int tiedInterval = 5000;
    private int strugglePower = 1000;

    private long invincibleTime;
    private int invincibleInterval = 4000;
    private boolean invPlayed;

    public Player(Context context, int screenSizeX, int screenSizeY) {
        screenX = screenSizeX;
        screenY = screenSizeY;
        this.context = context;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.back);
        bitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX /9, screenSizeX /9, false);

        maxX = screenSizeX - bitmap.getWidth();
        maxY = screenSizeY - bitmap.getHeight();
        minX = 0;
        minY = 0;
        netNum = 0;
        tied = false;
        invincible = false;
        invPlayed = false;
        x = screenSizeX/2 - bitmap.getWidth()/2;
        y = screenSizeY - bitmap.getHeight() - margin;

        collision = new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

    public void update(){
        if(invincible){
            long current = System.currentTimeMillis();
            if((current - invincibleTime) > invincibleInterval){
                invincible = false;
            }else if(!invPlayed && (current - invincibleTime + 1000) > invincibleInterval){
                Sound.play(6,0);
                invPlayed = true;
            }
        }
        if(tied){
            long current = System.currentTimeMillis();
            if((current - tiedTime) > tiedInterval){
                tied = false;
            }else{
                return;
            }
        }
        if (isGoLeft){
            x -= 20 * speed;
            if (x < minX){
                x = minX;
            }
        }else if (isGoRight){
            x += 20 * speed;
            if (x > maxX){
                x = maxX;
            }
        }
        collision.left = x;
        collision.top = y;
        collision.right = x + bitmap.getWidth();
        collision.bottom = y + bitmap.getHeight();
    }

    public void goRight(float speed){
        isGoLeft = false;
        isGoRight = true;
        this.speed = Math.abs(speed);
    }

    public void goLeft(float speed){
        isGoRight = false;
        isGoLeft = true;
        this.speed = Math.abs(speed);
    }

    public void hold(){
        isGoLeft = false;
        isGoRight = false;
        speed = 0;
    }


    public float getPos() {
        float a = x - minX;
        float b = maxX - minX;
        return a / b;
    }

    public void getNet(){
        netNum += 1;
        Sound.play(1,0);
    }

    public boolean throwNet(){
        if(netNum > 0){
            netNum -= 1;
            return true;
        }
        return false;
    }

    public int getNetNum(){
        return netNum;
    }

    public void tied(){
        if(!invincible){
            tiedTime = System.currentTimeMillis();
            tied = true;
            Sound.play(4,0);
        }
    }
    public void struggle(){
        if(tied){
            tiedTime -= strugglePower;
        }
    }
    public void invincible(){
        invincibleTime = System.currentTimeMillis();
        invincible = true;
        tied = false;
        invPlayed = false;
        Sound.play(5,0);
    }
}
