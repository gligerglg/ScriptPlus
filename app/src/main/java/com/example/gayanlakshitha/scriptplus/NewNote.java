package com.example.gayanlakshitha.scriptplus;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Gayan Lakshitha on 9/26/2017.
 */

public class NewNote extends AppCompatActivity{

    TextView txt_topic;
    TextView txt_content;
    private boolean isSaved = true;
    private boolean success = false;
    private String topic;
    private String content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);

        txt_topic = (TextView)findViewById(R.id.txt_note_topic);
        txt_content = (TextView)findViewById(R.id.txt_note_content);

        try
        {
            Bundle b = getIntent().getExtras();
            topic = (String) b.getCharSequence("topic");

            if(!topic.isEmpty())
            {
                final SQLiteDatabase note_db = openOrCreateDatabase("Notedb.db",MODE_PRIVATE,null);
                Cursor cursor = note_db.rawQuery("SELECT * FROM tbl_notes WHERE topic='"+topic+"'",null);
                cursor.moveToFirst();
                txt_topic.setText(cursor.getString(0));
                txt_content.setText(cursor.getString(1));
            }
        }
        catch (Exception e){}




        txt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isSaved = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                isSaved = false;
            }
        });

        txt_topic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isSaved = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                isSaved = false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.btn_save)
        {
            //Save All
            saveAll();
            if(success)
                Toast.makeText(getApplicationContext(),"Note is Saved Successfully",Toast.LENGTH_SHORT).show();
            isSaved = true;
        }

        if(id==R.id.btn_lock) {
            //Set Password
            saveAll();

            topic =  txt_topic.getText().toString();
            Bundle b = new Bundle();
            Intent intent = new Intent(NewNote.this,SetPassword.class);
            b.putString("topic",topic);
            intent.putExtras(b);
            startActivity(intent);

        }

        if(id==R.id.btn_delete)
        {
                AlertDialog.Builder aleart = new AlertDialog.Builder(NewNote.this);
                aleart.setTitle("Warning");
                aleart.setMessage("Do you want to Delete this Document?");

                aleart.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteDoc(txt_topic.getText().toString());
                        dialog.dismiss();
                    }
                });

                aleart.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                aleart.create().show();
        }

        if(id==R.id.mnu_mail)
        {
            SendMail();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(!isSaved)
        {
            AlertDialog.Builder aleart = new AlertDialog.Builder(NewNote.this);
            aleart.setTitle("Warning");
            aleart.setMessage("Changes will be discaded. Save?");

            aleart.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveAll();
                    //dialog.dismiss();
                    finish();
                }
            });

            aleart.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dialog.dismiss();
                    finish();
                }
            });

            aleart.create().show();
        }
        else
            finish();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_saveall,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void saveAll()
    {
        final SQLiteDatabase note_db = openOrCreateDatabase("Notedb.db",MODE_PRIVATE,null);
        topic =  txt_topic.getText().toString();
        content =  txt_content.getText().toString();
        boolean flag = false;
        success = false;

        if(topic.isEmpty())
            txt_topic.setError("Topic Cannot be Empty");
        if(content.isEmpty())
            txt_content.setError("Content Cannot be Empty");
        if(!topic.isEmpty() && !content.isEmpty())
        {
            try
            {
                Cursor cursor = note_db.rawQuery("SELECT topic FROM tbl_notes",null);
                while(cursor.moveToNext())
                {
                    if(topic.equals(cursor.getString(0)))
                    {
                        flag = true;
                        break;
                    }
                }

                if(flag)
                {
                    String sql = "UPDATE tbl_notes SET content=? WHERE topic=?";
                    SQLiteStatement statement = note_db.compileStatement(sql);
                    statement.bindString(1,content);
                    statement.bindString(2,topic);
                    statement.executeUpdateDelete();
                }

                else
                {
                    String sql = "INSERT INTO tbl_notes (topic,content) VALUES('"+topic+"',?)";
                    SQLiteStatement statement = note_db.compileStatement(sql);
                    statement.bindString(1,content);
                    statement.executeInsert();
                }

                isSaved = true;
                success = true;
            }
           catch (Exception e)
           {
               txt_topic.setError("Illegal characters found");
           }
        }
    }

    public void DeleteDoc(String topic)
    {
        String caption = txt_topic.getText().toString();
        final SQLiteDatabase note_db = openOrCreateDatabase("Notedb.db",MODE_PRIVATE,null);
        Cursor cursor = note_db.rawQuery("SELECT * FROM tbl_notes WHERE topic='"+caption+"'",null);

        if(cursor.getCount()!=0)
        {
            note_db.execSQL("DELETE FROM tbl_notes WHERE topic='"+caption+"'");
            Toast.makeText(getApplicationContext(),"Document Deleted!",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void SendMail()
    {
        String subject = txt_topic.getText().toString();
        String message = txt_content.getText().toString();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT,message);

        try
        {
            startActivity(Intent.createChooser(emailIntent,"Send Mail"));
        }
        catch (android.content.ActivityNotFoundException e)
        {
            Toast.makeText(getApplicationContext(),"There is no email client installed.",Toast.LENGTH_SHORT).show();
        }
    }


}
