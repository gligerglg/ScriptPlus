package com.example.gayanlakshitha.scriptplus;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Gayan Lakshitha on 9/27/2017.
 */

public class GetPassword extends Activity {

    EditText txt_password;
    Button btn_get;
    String topic;
    String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_password);

        btn_get = (Button)findViewById(R.id.btn_getPassword);
        txt_password = (EditText)findViewById(R.id.txt_getPassword);

        Bundle b = getIntent().getExtras();
        topic = (String) b.getCharSequence("topic");

        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = txt_password.getText().toString();
                if(password.isEmpty())
                    txt_password.setError("This field cannot be empty");
                else
                {
                    SQLiteDatabase note_db = openOrCreateDatabase("Notedb.db",MODE_PRIVATE,null);
                    Cursor cursor = note_db.rawQuery("SELECT password FROM tbl_notes WHERE topic='"+topic+"'",null);
                    cursor.moveToFirst();
                    if(password.equals(cursor.getString(0)))
                    {
                        Intent intent = new Intent(GetPassword.this,NewNote.class);
                        Bundle b = new Bundle();
                        b.putString("topic",topic);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                    else
                        txt_password.setError("Wrong Password");
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
