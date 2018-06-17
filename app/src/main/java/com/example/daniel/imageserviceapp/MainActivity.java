package com.example.daniel.imageserviceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startService(View view) {
        Intent intent = new Intent(this, ImageService.class);

        Button start_button = (Button) findViewById(R.id.StartService);
//        Button stop_button = (Button) findViewById(R.id.stopService_b);
        start_button.setVisibility(View.VISIBLE);
//        stop_button.setVisibility(View.VISIBLE);

        startService(intent);
    }
}
