package com.dayaner.sheetsdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dayaner.sheetsdemo.view.BottomSheetLayout;
import com.dayaner.sheetsdemo.view.DetailView;
import com.dayaner.sheetsdemo.view.LinkedScrollView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DetailView mDetailView;

    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetailView = findViewById(R.id.dv_content);
        initToolbar();
        initTopLayout();
        initBottomLayout();
    }

    private void initToolbar() {
     mDetailView.setBackgroundColor(getResources().getColor(R.color.white));
     mDetailView.toolbarView.setup("标题详情",getResources().getColor(R.color.purple_500))
             .setListeners(v ->  Toast.makeText(MainActivity.this,"返回",Toast.LENGTH_LONG).show());
    }

    private void initTopLayout() {
        arrayList = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            arrayList.add("条目："+i);
        }
        mDetailView.topRecyclerView.setAdapter(new DetailAdapter(arrayList));
        mDetailView.topRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initBottomLayout() {
//        mDetailView.bottomSheetLayout.setBackgroundColor(Color.WHITE);
        LayoutInflater.from(this).inflate(R.layout.view_bottom_content, mDetailView.bottomLayout,true);
        mDetailView.bottomSheetLayout.setProcess(1f,true);
    }
}