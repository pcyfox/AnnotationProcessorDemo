package com.demo.ap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.demo.annotationlib.annotations.BindViews;


public class MainActivity extends AppCompatActivity {

    @BindViews(R.id.tv_hello)
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
