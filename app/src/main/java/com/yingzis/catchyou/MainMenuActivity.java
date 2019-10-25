package com.yingzis.catchyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import android.media.MediaPlayer;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button play;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        play = findViewById(R.id.playButton);
        play.setOnClickListener(this);
        player=MediaPlayer.create(this, R.raw.ylzz);
        player.setLooping(true);
        player.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playButton:
                startActivity(new Intent(this, WaitingActivity.class));
                break;
        }
    }
}
