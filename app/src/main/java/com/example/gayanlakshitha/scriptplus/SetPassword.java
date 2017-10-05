package com.example.gayanlakshitha.scriptplus;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Gayan Lakshitha on 9/27/2017.
 */

public class SetPassword extends Activity {

    EditText txt_password;
    EditText txt_repeat;
    Button btn_set;

    private String password;
    private String repeat;
    private String topic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_password);

        txt_password = (EditText)findViewById(R.id.txt_password);
        txt_password.requestFocus();
        txt_repeat = (EditText)findViewById(R.id.txt_repeat);
        btn_set = (Button)findViewById(R.id.btn_set);

        Bundle b = getIntent().getExtras();
        topic = (String) b.getCharSequence("topic");

        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = txt_password.getText().toString();
                repeat = txt_repeat.getText().toString();

                if(password.isEmpty())
                    txt_password.setError("This field cannot be empty");
                if(repeat.isEmpty())
                    txt_repeat.setError("This field cannot be empty");
                if(!password.isEmpty() && !repeat.isEmpty())
                {
                    if(password.equals(repeat))
                    {
                        final SQLiteDatabase note_db = openOrCreateDatabase("Notedb.db",MODE_PRIVATE,null);
                        String sql = "UPDATE tbl_notes SET protected='"+true+"',password=? WHERE topic=?";
                        SQLiteStatement statement = note_db.compileStatement(sql);
                        statement.bindString(1,password);
                        statement.bindString(2,topic);
                        statement.executeUpdateDelete();
                        finish();
                    }
                    else
                        txt_repeat.setError("Wrong Password");
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
