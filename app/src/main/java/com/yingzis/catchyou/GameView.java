package com.yingzis.catchyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

// Game controller class
public class GameView extends SurfaceView implements Runnable {

    private Thread thread;

    // pause or not
    private volatile boolean isPlaying;

    private Player player;
    private Opponent opponent;

    // effect on player and opponent
    private Effect effectP;
    private Effect effectO;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    // List of nets, walls, tools
    private ArrayList<Net> nets;
    private ArrayList<Wall> walls;
    private ArrayList<Tool> tools;

    private int screenX, screenY;

    // Bitmap of background
    private Bitmap bgBitmap;

    // Bitmap of net number ui
    private Bitmap netlogoBitmap;

    // Game over or not
    private volatile boolean mIsGameOver;

    // Positon of throw a net
    private float netSent;

    public GameView(Context context, int screenSizeX, int screenSizeY) {
        super(context);
        Sound.init(context);
        screenX = screenSizeX;
        screenY = screenSizeY;
        paint = new Paint();
        surfaceHolder = getHolder();
        reset();
        netSent = -1f;
        bgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        bgBitmap = Bitmap.createScaledBitmap(bgBitmap, screenSizeX, screenSizeY, false);
        netlogoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.netlogo);
        netlogoBitmap = Bitmap.createScaledBitmap(netlogoBitmap, screenSizeX/8, screenSizeX/8, false);
    }

    // reset all
    void reset() {
        player = new Player(getContext(), screenX, screenY);
        opponent = new Opponent(getContext(), screenX, screenY);
        effectP = new Effect(getContext(), player);
        effectO = new Effect(getContext(), opponent);
        walls = new ArrayList<>();
        nets = new ArrayList<>();
        tools = new ArrayList<>();
        mIsGameOver = false;
    }

    @Override
    public void run() {
        while (isPlaying) {
            if (!mIsGameOver) {
                update();
                draw();
            }
        }
        Log.d("GameThread", "Run stopped");
    }

    // update all statu - player, opponent, nets, walls, tools, effects
    public void update() {
        player.update();
        opponent.update();

        // Update walls & react with player & do deletion
        for (Wall w : new ArrayList<>(walls)) {
            w.update();
            if (w.getSide() && Rect.intersects(w.getCollision(), player.getCollision())) {
                w.destroy();
                if(!player.isInvincible()){
//                    mIsGameOver = true;
                }

            }
        }

        boolean find = true;
        while(find){
            int i;
            find = false;
            for (i = 0; i < walls.size(); i++) {
                if (walls.get(i).getY() > screenY || walls.get(i).getY() < 0) {
                    find = true;
                    break;
                }
            }
            if(find)
                walls.remove(i);
        }

        // Update nets & react with player & do deletion
        for (Net n : new ArrayList<>(nets)) {
            n.update();
            if (!n.getSide() && Rect.intersects(n.getCollision(), player.getCollision())) {
                n.destroy();
                player.tied();
            }
        }

        find = true;
        while(find){
            int i;
            find = false;
            for (i = 0; i < nets.size(); i++) {
                if (nets.get(i).getY() > screenY || nets.get(i).getY() < opponent.getY()) {
                    find = true;
                    break;
                }
            }
            if(find)
                nets.remove(i);
        }

        // Update tools & react with player & do deletion
        for (Tool t : new ArrayList<>(tools)) {
            t.update();
            if (t.getSide() && Rect.intersects(t.getCollision(), player.getCollision())) {
                t.destroy();
                t.effect(player);
            }
            if (!t.getSide() && Rect.intersects(t.getCollision(), opponent.getCollision())) {
                t.destroy();
            }
        }

        find = true;
        while(find){
            int i;
            find = false;
            for (i = 0; i < tools.size(); i++) {
                if (tools.get(i).getY() > screenY || tools.get(i).getY() < -tools.get(i).getBitmap().getHeight()) {
                    find = true;
                    break;
                }
            }
            if(find)
                tools.remove(i);
        }

        // Update effects
        effectP.update();
        effectO.update();

    }

    // draw all the objects
    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(bgBitmap, 0, 0, paint);
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);

            Bitmap emapP = effectP.getBitmap();
            if(emapP != null){
                canvas.drawBitmap(emapP, effectP.getX(), effectP.getY(), paint);
            }
            canvas.drawBitmap(opponent.getBitmap(), opponent.getX(), opponent.getY(), paint);
            Bitmap emapO = effectO.getBitmap();
            if(emapO != null){
                canvas.drawBitmap(emapO, effectO.getX(), effectO.getY(), paint);
            }

            for (Wall m : new ArrayList<>(walls)) {
                canvas.drawBitmap(m.getBitmap(), m.getX(), m.getY(), paint);
            }
            for (Tool t : new ArrayList<>(tools)) {
                canvas.drawBitmap(t.getBitmap(), t.getX(), t.getY(), paint);
            }
            drawMid();
            for (Net n : new ArrayList<>(nets)) {
                canvas.drawBitmap(n.getBitmap(), n.getX(), n.getY(), paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    void drawMid() {
        paint.setColor(Color.rgb(0, 132, 236));
        paint.setStrokeWidth(0);
        canvas.drawRect(0, screenY /2 - 20, screenX, screenY /2 + 20, paint);
        canvas.drawBitmap(netlogoBitmap, 0, screenY /2 - screenX /16, paint);
        paint.setTextSize(90);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(Integer.toString(player.getNetNum()), screenX /16, screenY /2 + 30, paint);
    }

    void gameOver() {
        Paint gameOver = new Paint();
        gameOver.setTextSize(100);
        gameOver.setTextAlign(Paint.Align.CENTER);
        gameOver.setColor(Color.WHITE);
        canvas.drawText("GAME OVER", screenX / 2, screenY / 2, gameOver);
        Paint highScore = new Paint();
        highScore.setTextSize(50);
        highScore.setTextAlign(Paint.Align.CENTER);
        highScore.setColor(Color.WHITE);

    }

    public void goLeft(float speed) {
        player.goLeft(speed);
    }

    public void goRight(float speed) {
        player.goRight(speed);
    }

    public void hold() {
        player.hold();
    }


    public void pause() {
        Log.d("GameThread", "Main");
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                if (mIsGameOver){
//                    ((Activity) getContext()).finish();
//                    getContext().startActivity(new Intent(getContext(), MainMenuActivity.class));
//                    break;
//                }
                if (netSent < 0){
                    if(player.throwNet()){
                        netSent = player.getPos();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public Player getPlayer(){
        return player;
    }

    public Opponent getOpponent(){
        return opponent;
    }

    public boolean getState(){
        return mIsGameOver;
    }
    public float getNetSent(){
        return netSent;
    }

    public void resetNetSent(){
        netSent = -1f;
    }

    public void addWall(int pos){
        for(int i = 0; i <= 3; i++){
            if(i != pos){
                walls.add(new Wall(getContext(), screenX, screenY, i, true));
                walls.add(new Wall(getContext(), screenX, screenY, i, false));
            }

        }
    }
    public void addNet(float pos, boolean myside){
        if(myside){
            nets.add(new Net(getContext(), screenX, screenY, pos, player.getBitmap().getWidth(), player.getBitmap().getHeight(),myside));
            Sound.play(3,0);
        }else{
            nets.add(new Net(getContext(), screenX, screenY, pos, opponent.getBitmap().getWidth(), opponent.getBitmap().getHeight(),myside));
        }
        Log.d("adam", "debug 6");
    }

    public void addTool(float pos, int type){
        tools.add(new Tool(getContext(), screenX, screenY, type, pos, true));
        tools.add(new Tool(getContext(), screenX, screenY, type, pos, false));
    }


}
