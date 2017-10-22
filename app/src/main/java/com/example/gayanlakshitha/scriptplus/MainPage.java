package com.example.gayanlakshitha.scriptplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gayan Lakshitha on 9/26/2017.
 */

public class MainPage extends AppCompatActivity {

    ListView list;
    SQLiteDatabase note_db;
    LinearLayout layout;
    private String username;
    int count_cloud=0;
    int count_internal=0;
    boolean exist=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        list =  (ListView)findViewById(R.id.lst_notes);
        list.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorPrimary)));
        list.setDividerHeight(1);
        note_db = openOrCreateDatabase("Notedb.db",MODE_PRIVATE,null);
        note_db.execSQL("CREATE TABLE IF NOT EXISTS tbl_notes(topic text, content text, protected boolean, password text)");

        //list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String note = list.getItemAtPosition(position).toString();
                Cursor cursor = note_db.rawQuery("SELECT protected FROM tbl_notes WHERE topic='"+note+"'",null);
                cursor.moveToFirst();
                Intent intent;

                if(Boolean.parseBoolean(cursor.getString(0)))
                {
                    intent = new Intent(MainPage.this,GetPassword.class);
                }
                else
                {
                    intent = new Intent(MainPage.this,NewNote.class);
                }

                Bundle b = new Bundle();
                b.putString("topic",note);
                intent.putExtras(b);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_newnote,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.btn_addnewnote)
        {
            startActivity(new Intent(MainPage.this,NewNote.class));
        }

        if(id==R.id.btn_sync)
        {
            if(isRegistered())
            {
                syncData();
                Refresh(note_db);
                Toast.makeText(getApplicationContext(),"Sync Process Complete!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
                builder.setTitle("Script+ Account");
                builder.setMessage("You have to create a Script+ Account first!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainPage.this,SignUp.class));
                    }
                });
                builder.create().show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncData() {

        //Get # of Docs in internal Database
        final Cursor cursor = note_db.rawQuery("SELECT topic FROM tbl_notes",null);
        count_internal = cursor.getCount();

        //Get # of Docs in Cloud Database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReferenceFromUrl("https://scriptplus-cde67.firebaseio.com/Scripts/"+username+"/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count_cloud = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(count_cloud>=count_internal)
        {
            //Import Docs if Cloud>
            DatabaseReference cloud = db.getReferenceFromUrl("https://scriptplus-cde67.firebaseio.com/Scripts/"+username+"/");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren())
                    {
                        Script script = child.getValue(Script.class);
                        cursor.moveToFirst();
                        while(cursor.moveToNext())
                        {
                            String topic = cursor.getString(0);
                            if(topic.equals(script.getTitle()))
                            {
                                exist = true;
                                break;
                            }
                        }

                        if(!exist)
                        {
                            String sql = "INSERT INTO tbl_notes (topic,content) VALUES(?,?)";
                            SQLiteStatement statement = note_db.compileStatement(sql);
                            statement.bindString(1,script.getTitle());
                            statement.bindString(2,script.getContent());
                            statement.executeInsert();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else
        {
            //Export Docs if Internal>Cloud
            Cursor internal = note_db.rawQuery("SELECT * FROM tbl_notes",null);
            DatabaseReference cloud = db.getReferenceFromUrl("https://scriptplus-cde67.firebaseio.com/Scripts/"+username+"/");
            while(internal.moveToNext())
            {
                String topic = internal.getString(0);
                String content = internal.getString(1);
                Script script = new Script(topic,content);
                cloud.child(topic).setValue(script);
            }
        }

    }

    private void Refresh(SQLiteDatabase note_db)
    {
        Cursor cursor = note_db.rawQuery("SELECT * FROM tbl_notes",null);
        List<String> list_set = new ArrayList<>();
        if(cursor.getCount()==0)
        {
            layout = (LinearLayout)findViewById(R.id.layout);
            layout.setBackgroundResource(R.drawable.xhdpi_empty);
        }
        else
        {
            layout = (LinearLayout)findViewById(R.id.layout);
            layout.setBackgroundColor(Color.BLACK);
            while(cursor.moveToNext())
                list_set.add(cursor.getString(0));
        }

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,list_set);
        list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Refresh(note_db);
    }

    private boolean isRegistered()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared",0);
        boolean acc_status = sharedPreferences.getBoolean("status",false);
        username = sharedPreferences.getString("username","");
        return acc_status;
    }

}
