package com.yingzis.catchyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Tool {

    private Bitmap bitmap;
    private int x;
    private int y;
    private Rect collision;
    private int screenX;
    private int screenY;

    private int type;
    private int speed;
    private boolean isMySide;

    public Tool(Context context, int screenSizeX, int screenSizeY, int mytype, float pos, boolean myside) {
        type = mytype;
        screenX = screenSizeX;
        screenY = screenSizeY;

        if(type == 1){
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.nettool);
            bitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX /9, screenSizeX /9, false);
        }else if(type == 2){
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shieldtool);
            bitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX /9, screenSizeX /9, false);
        }
        isMySide = myside;

        if(myside){
            speed = 4;
            x = Math.round(pos * (screenSizeX - bitmap.getWidth()));
            y = screenSizeY /2;
        }else{
            speed = -4;
            x = Math.round((1 - pos) * (screenSizeX - bitmap.getWidth()));
            y = screenSizeY /2 - bitmap.getHeight();
        }
        collision = new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

    public void update(){
        y += speed;

        collision.left = x;
        collision.top = y;
        collision.right = x + bitmap.getWidth();
        collision.bottom = y + bitmap.getHeight();
    }

    public Rect getCollision() {
        return collision;
    }

    public void effect(Player p){
        if(isMySide){
            if(type == 1){
                p.getNet();
            }else if(type == 2){
                p.invincible();
            }
        }
    }

    public void destroy(){

        if(isMySide){
            y = screenY;
        }else{
            y = -bitmap.getHeight();
        }
//        mSoundPlayer.playCrash();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getSide() {
        return isMySide;
    }
}
