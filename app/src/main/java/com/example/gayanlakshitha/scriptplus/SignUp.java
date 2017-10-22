package com.example.gayanlakshitha.scriptplus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends Activity {

    private Button btn_register;
    private EditText txt_username;
    private EditText txt_password;
    private EditText txt_repeat;
    private String username;
    private String password;
    private String repeat;
    private boolean check=false;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btn_register = (Button)findViewById(R.id.btn_register_signup);
        txt_username = (EditText)findViewById(R.id.txt_signup_username);
        txt_password = (EditText)findViewById(R.id.txt_signup_password);
        txt_repeat = (EditText)findViewById(R.id.txt_signup_repeat);
        txt_username.requestFocus();
        user = new User();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("shared",0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txt_username.getText().toString();
                password = txt_password.getText().toString();
                repeat = txt_repeat.getText().toString();

                if(username.isEmpty())
                    txt_username.setError("Username Cannot be Empty!");
                if(password.isEmpty())
                    txt_password.setError("Password Cannot be Empty!");
                if(repeat.isEmpty())
                    txt_repeat.setError("This Field Cannot be Empty!");

                if(!password.equals(repeat))
                    txt_repeat.setError("Invalid Password!");

                if(!username.isEmpty()&&!password.isEmpty()&&!repeat.isEmpty()&&password.equals(repeat))
                {

                    final DatabaseReference myref = database.getReferenceFromUrl("https://scriptplus-cde67.firebaseio.com/Accounts/");
                    myref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot child : dataSnapshot.getChildren())
                            {
                                user = child.getValue(User.class);
                                if(username.equals(user.getUsername()))
                                {
                                    check=true;
                                    break;
                                }
                            }

                            if(check)
                            {
                                if(password.equals(user.getPassword()))
                                {
                                    Toast.makeText(getApplicationContext(),"Registration Complete.",Toast.LENGTH_SHORT).show();
                                    editor.putBoolean("status",true);
                                    editor.putString("username",username);
                                    editor.commit();
                                    startActivity(new Intent(SignUp.this,MainPage.class));
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"You are an existing user\nIncorrect Password",Toast.LENGTH_SHORT).show();
                                    txt_password.setError("Incorrect Password");
                                }

                            }
                            else
                            {
                                try
                                {
                                    User new_user = new User(username,password);
                                    myref.child(username).setValue(new_user);
                                    editor.putBoolean("status",true);
                                    editor.putString("username",username);
                                    editor.commit();
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Sync Error",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
