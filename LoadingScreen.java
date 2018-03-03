package com.example.tanapone.smartcashier;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.example.tanapone.smartcashier.DatabaseHelper.DatabaseHelperClass;
import com.example.tanapone.smartcashier.Models.Store;

public class LoadingScreen extends Activity {
    private static int SPLASH_TIME_OUT = 2000;
    private DatabaseHelperClass myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new DatabaseHelperClass(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide Title Bar [extends Activity]
        setContentView(R.layout.activity_loading_screen);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!myDB.foundData("Store")) {
                        Intent intent = new Intent(LoadingScreen.this, Register.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            },SPLASH_TIME_OUT);
        }
}
