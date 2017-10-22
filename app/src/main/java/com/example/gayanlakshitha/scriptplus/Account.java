package com.example.gayanlakshitha.scriptplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Account extends Activity {

    private Button btn_signup;
    private Button btn_skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        btn_signup = (Button)findViewById(R.id.btn_register);
        btn_skip = (Button)findViewById(R.id.btn_skip);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Account.this,SignUp.class));
                finish();
            }
        });

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Account.this,MainPage.class));
                finish();
            }
        });
    }
}
