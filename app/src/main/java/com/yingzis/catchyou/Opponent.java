package com.yingzis.catchyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Opponent extends Person{
    private int maxX;
    private int margin = 16;
    private Context context;
    private int screenX, screenY;
    private long lastMove;
    private int aim;
    private int moveInterval = 500;

    public Opponent(Context context, int screenSizeX, int screenSizeY) {
        screenX = screenSizeX;
        screenY = screenSizeY;
        this.context = context;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.front);
        bitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX /9, screenSizeX /9, false);

        tied = false;
        invincible = false;

        maxX = screenSizeX - bitmap.getWidth();

        x = screenSizeX/2 - bitmap.getWidth()/2;
        y = margin;
        aim = x;
        collision = new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }


    public void update(){
        long current = System.currentTimeMillis();
        if(x != aim){
            int delta = (aim - x) * (int)(current - lastMove) / moveInterval;
            if((delta >= 0 && (x + delta) >= aim) || (delta < 0 && (x + delta) <= aim)){
                x = aim;
            }else{
                x += delta;
            }
        }
        collision.left = x;
        collision.top = y;
        collision.right = x + bitmap.getWidth();
        collision.bottom = y + bitmap.getHeight();
    }

    public void setPos(float f){
        lastMove = System.currentTimeMillis();
        aim = Math.round(f * maxX);
    }
}
