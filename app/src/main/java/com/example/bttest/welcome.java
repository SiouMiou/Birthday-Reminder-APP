package com.example.bttest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

    public class welcome  extends AppCompatActivity {
        Button StartButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.welcome);
            StartButton = findViewById(R.id.StartButton);
            StartButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(welcome.this, MainActivity.class);//建立一個inent物件，並建立一個MA2的活動意圖



                    startActivity(intent);
                    finish();
                }
            } );
        }
}
