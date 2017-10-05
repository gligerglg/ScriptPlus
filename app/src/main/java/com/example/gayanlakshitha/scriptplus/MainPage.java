package com.example.gayanlakshitha.scriptplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gayan Lakshitha on 9/26/2017.
 */

public class MainPage extends AppCompatActivity {

    ListView list;
    SQLiteDatabase note_db;
    LinearLayout layout;

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

        return super.onOptionsItemSelected(item);
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

}
