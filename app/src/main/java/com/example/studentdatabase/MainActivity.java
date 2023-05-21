package com.example.studentdatabase;

import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText name, family, weight, lenval;
    ListView listView;
    Button ins, del, upd, view, filter, sort_name, sort_weight;
    SQLiteDatabase db;

    List<String> names, families, weights, lenvals;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.name);
        family = findViewById(R.id.family);
        weight = findViewById(R.id.weight);
        lenval = findViewById(R.id.lengthval);

        ins = findViewById(R.id.insert);
        del = findViewById(R.id.delete);
        upd = findViewById(R.id.update);
        view = findViewById(R.id.view_all);
        filter = findViewById(R.id.filter);
        sort_name = findViewById(R.id.sort);
        sort_weight = findViewById(R.id.sort2);

        names = new ArrayList<>();
        families = new ArrayList<>();
        weights = new ArrayList<>();
        lenvals = new ArrayList<>();

        listView = findViewById(R.id.listView);
        ListAdapter adapter = new ArrayAdapter<>(this,R.layout.row, names);
        listView.setAdapter(adapter);
        db = openOrCreateDatabase("Animals", MODE_PRIVATE, null);
        db.execSQL("create table if not exists animal(name varchar(50) primary key,family varchar(30),weight float,length float)");
        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString();
                String f = family.getText().toString();
                String w = weight.getText().toString();
                String l = lenval.getText().toString();
                db.execSQL(String.format("insert into animal values('%s', '%s', %s, %s)", n, f, w, l));
                Toast.makeText(MainActivity.this, n + " inserted", Toast.LENGTH_SHORT).show();

            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString();
                db.execSQL(String.format("delete from animal where name='%s'", n));
                Toast.makeText(MainActivity.this, n + " deleted", Toast.LENGTH_SHORT).show();
            }
        });
        upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString();
                String f = family.getText().toString();
                String w = weight.getText().toString();
                String l = lenval.getText().toString();
                db.execSQL(String.format("update animal set family='%s', weight=%s, length=%s where name='%s'",f, w, l, n));
                Toast.makeText(MainActivity.this, n + " updated", Toast.LENGTH_SHORT).show();

            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Runnable task = () -> {
                    Cursor c = db.rawQuery("select * from animal", null);
                    if (c.getCount() > 0) {
                        updateView(c);
                    } else {
                        Toast.makeText(MainActivity.this, "no content", Toast.LENGTH_SHORT).show();
                    }
                };

                task.run();

            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = db.rawQuery(String.format("select * from animal where weight<30 and length<140"), null);
                if (c.getCount() > 0) {
                    updateView(c);
                } else {
                    Toast.makeText(MainActivity.this, "no content", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sort_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = db.rawQuery(String.format("select * from animal order by name"), null);
                if (c.getCount() > 0) {
                    updateView(c);
                } else {
                    Toast.makeText(MainActivity.this, "no content", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sort_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = db.rawQuery(String.format("select * from animal order by weight DESC"), null);
                if (c.getCount() > 0) {
                    updateView(c);
                } else {
                    Toast.makeText(MainActivity.this, "no content", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String n = listView.getItemAtPosition(i).toString();
                getInfo(n);
            }
        });

    }

    private void cleanAdapter() {
        names.clear();
        families.clear();
        weights.clear();
        lenvals.clear();
    }

    private void updateView(Cursor cursor) {
        cleanAdapter();
        while (cursor.moveToNext()) {
            names.add(cursor.getString(0));
            families.add(cursor.getString(1));
            weights.add(cursor.getString(2));
            lenvals.add(cursor.getString(3));
        }
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    private void getInfo(String keyName) {
        Cursor c = db.rawQuery(String.format("select * from animal where name='%s'", keyName), null);
        if (c.getCount() > 0) {
            c.moveToNext();
            name.setText(c.getString(0));
            family.setText(c.getString(1));
            weight.setText(c.getString(2));
            lenval.setText(c.getString(3));
        }
    }
}