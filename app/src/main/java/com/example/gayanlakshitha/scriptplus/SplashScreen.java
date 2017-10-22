package com.example.gayanlakshitha.scriptplus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("shared",0);
        final boolean acc_status = sharedPreferences.getBoolean("status",false);

        Thread timerThread = new Thread()
        {
            public  void run()
            {
                try
                {
                    sleep(1500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally {
                    if(acc_status)
                        startActivity(new Intent(SplashScreen.this,MainPage.class));
                    else
                        startActivity(new Intent(SplashScreen.this,Account.class));
                }

            }
        };

        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
