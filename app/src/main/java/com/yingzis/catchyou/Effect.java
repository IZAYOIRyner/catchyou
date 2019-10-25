package com.yingzis.catchyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// The effect on the person object, such as tied
public class Effect {

    private Bitmap[] bitmap;

    private int x;
    private int y;
    // the person which effect on
    private Person follow;
    private Context context;

    public Effect(Context context, Person p) {
        follow = p;
        bitmap = new Bitmap[2];
        bitmap[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.net);
        bitmap[0] = Bitmap.createScaledBitmap(bitmap[0], p.getBitmap().getWidth(), p.getBitmap().getHeight(), false);
        bitmap[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.shield);
        bitmap[1] = Bitmap.createScaledBitmap(bitmap[1], p.getBitmap().getWidth(), p.getBitmap().getHeight(), false);
        x = p.getX();
        y = p.getY();

    }

    // update location
    public void update(){
        x = follow.getX();
        y = follow.getY();
    }

    public Bitmap getBitmap() {
        if(follow.isInvincible()){
            return bitmap[1];
        }else if(follow.isTied()){
            return bitmap[0];
        }
        return null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
