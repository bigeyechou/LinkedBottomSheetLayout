package com.dayaner.sheetsdemo;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dayaner.sheetsdemo.view.BottomSheetLayout;
import com.dayaner.sheetsdemo.view.LinkedScrollView;

public class MainActivity extends AppCompatActivity {

    private TextView tvTitle;
    private LinkedScrollView scrollView;
    private BottomSheetLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = findViewById(R.id.tv_title);
        scrollView = findViewById(R.id.scroll_linked);
        bottomSheetLayout = findViewById(R.id.bottom_sheet);

        FrameLayout bottomContainer = scrollView.bottomContainer;
//        bottomContainer.setLayoutParams();

//        scrollView.bottomContainer.setLayoutParams(new LayoutPar);

        initBottomLayout();
    }

    private void initBottomLayout() {

    }
}