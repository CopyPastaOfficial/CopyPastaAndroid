package fr.remialban.copypasta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import fr.remialban.copypasta.R;

/**
 * This is the First Activity. It is a SplashScreen
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(i);
                finish();
            }

        }, 1500);
    }
}