package com.example.KC.danmutest;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.ArrayList;

public class ListView extends AppCompatActivity {
    String[] data;
    public String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        data = doSearch("/sdcard");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ListView.this, android.R.layout.simple_list_item_1, data
        );
        android.widget.ListView listView = (android.widget.ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent, View view, int position,
                                    long id) {
                PlayerView.path = Environment.getExternalStorageDirectory() + "/" + data[position];
                PlayerView.fileName = getFileNameNoEx(data[position]).toLowerCase();

                Intent intent = new Intent(ListView.this, PlayerView.class);

                startActivity(intent);
            }
        });

    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    private String[] doSearch( String path) {
        ArrayList<String> fileTempList = new ArrayList<String>();
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fileArray = file.listFiles();
                for (File f : fileArray) {
                    if (f.isDirectory()) {
                        doSearch(f.getPath());
                    }
                    else {
                        if(f.getName().endsWith("mp4") || f.getName().endsWith("rvmb"))
                        {
                            fileTempList.add(f.getName());
                        }
                    }
                }
            }
        }
        String[] fileList = (String[])fileTempList.toArray(new String[0]);
        return fileList;
    }
}
