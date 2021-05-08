package com.dayaner.sheetsdemo;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dayaner.sheetsdemo.view.BottomSheetLayout;
import com.dayaner.sheetsdemo.view.DetailView;
import com.dayaner.sheetsdemo.view.LinkedScrollView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DetailView mDetailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetailView = findViewById(R.id.dv_content);
        initTopLayout();
        initBottomLayout();
    }

    private void initTopLayout() {

    }

    private void initBottomLayout() {

    }
}