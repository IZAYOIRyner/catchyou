package com.yingzis.catchyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Net {

    private Bitmap bitmap;
    private int x;
    private int y;
    private Rect collision;
    private int screenX;
    private int screenY;
    private boolean isMySide;
    private int speed;


    public Net(Context context, int screenSizeX, int screenSizeY, float pos, int playerW, int playerH, boolean myside){
        screenX = screenSizeX;
        screenY = screenSizeY;
        isMySide = myside;
        if(myside){
            speed = -8;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.netp);
            bitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX / 5, screenSizeX / 5, false);
            y = screenSizeY -  playerH  - bitmap.getHeight();


        }else{
            speed = 8;
            y = playerH;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.neto);
            bitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX / 5, screenSizeX / 5, false);
        }
        x = Math.round(pos * (screenSizeX - playerW)) + (playerW / 2) - (bitmap.getWidth() / 2);
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

    public void destroy(){
        if (isMySide){
            y = 0 - bitmap.getHeight();
        }else{
            y = screenY;
        }

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
