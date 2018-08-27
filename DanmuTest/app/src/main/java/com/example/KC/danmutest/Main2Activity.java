package com.example.KC.danmutest;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final EditText editText = (EditText) findViewById(R.id.InternetURL);
        Button Input = (Button) findViewById(R.id.Input);

        Input.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String inputText = editText.getText().toString();
                PlayerView.path = "";
                //PlayerView.resid =  getResources().getIdentifier("defaults" , "raw", getPackageName());
                Intent intent = new Intent(Main2Activity.this, PlayerView.class);
                if (!inputText.isEmpty()) {
                    PlayerView.uri = Uri.parse(inputText);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "the URL is null", Toast.LENGTH_SHORT).show();
                }

            }
        });
        Button itself = (Button) findViewById(R.id.itself);
        itself.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(Main2Activity.this, ListView.class);
                //PlayerView.path = Environment.getExternalStorageDirectory() + "/Pixel.mp4";
                startActivity(intent);
            }
        });

    }


}
