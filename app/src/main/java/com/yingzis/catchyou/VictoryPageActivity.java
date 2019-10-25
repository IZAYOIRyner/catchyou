package com.yingzis.catchyou;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;

public class VictoryPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.win_page);

        //xml(page) transition when click "back to home" button
        Button backButton = findViewById(R.id.win_back_button);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(VictoryPageActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
