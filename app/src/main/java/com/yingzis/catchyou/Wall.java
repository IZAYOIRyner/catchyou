package com.yingzis.catchyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


public class Wall {

    private Bitmap bitmap;
    private int x;
    private int y;
    private int maxX;
    private int minX;
    private int maxY;
    private int minY;

    private int speed;
    private Rect collision;
    private int screenX;
    private int screenY;
    private boolean isMySide;

    public Wall(Context context, int screenSizeX, int screenSizeY, int pos, boolean myside){
        isMySide = myside;
        screenX = screenSizeX;
        screenY = screenSizeY;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        bitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX /4, screenSizeX /32, false);

        if(myside){
            speed = 4;
            x = pos * screenSizeX /4;
            y = screenSizeY /2;
        }else{
            speed = -4;
            x = (3-pos) * screenSizeX /4;
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

    public void destroy(){
        if(isMySide){
            y = screenY;
        }else{
            y = -bitmap.getHeight();
        }
        Sound.play(2,0);
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
