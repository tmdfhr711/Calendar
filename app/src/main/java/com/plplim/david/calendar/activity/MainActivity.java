package com.plplim.david.calendar.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.plplim.david.calendar.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivity(intent);
        finish();
    }
}
