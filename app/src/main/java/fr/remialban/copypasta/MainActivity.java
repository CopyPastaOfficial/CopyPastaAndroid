package fr.remialban.copypasta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

/**
 * This is the First Activity. It is a SplashScreen
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);

                    Intent intent = new Intent(getApplicationContext(),SelectDeviceActivity.class);
                    startActivity(intent);
                    finish();


                } catch (Exception e)
                {

                }

            }
        });
        thread.start();
    }
}