package com.yingzis.catchyou;

import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract class Person {

    protected Bitmap bitmap;
    protected int x;
    protected int y;
    protected Rect collision;
    protected boolean invincible;
    protected boolean tied;

    public Rect getCollision() {
        return collision;
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

    public boolean isTied(){
        return tied;
    }

    public boolean isInvincible(){
        return invincible;
    }

    public void setTied(boolean b){
        tied = b;
    }

    public void setInvincible(boolean b){
        invincible = b;
    }
}
