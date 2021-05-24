package com.example.androidauthmongodbnodejs;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class My_Class_Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__class__information);
        TextView textview = findViewById(R.id.text1);

        Intent intent = getIntent();
        textview.setText(intent.getStringExtra("zzz"));

    }
}