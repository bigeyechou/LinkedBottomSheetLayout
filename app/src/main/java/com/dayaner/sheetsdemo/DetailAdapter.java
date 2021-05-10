package com.dayaner.sheetsdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：5/10/21 5:23 PM
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> datas = new ArrayList<>();

    public DetailAdapter(ArrayList<String> datas) {
        this.datas = datas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder){
            ((MyHolder) holder).iv1.setBackgroundResource(R.mipmap.ic_launcher_round);
            ((MyHolder) holder).tv1.setText("名字："+datas.get(position));
            ((MyHolder) holder).tv2.setText("描述："+datas.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv1,tv2;
        private ImageView iv1;
        public MyHolder(View view) {
            super(view);
            tv1 = view.findViewById(R.id.text1);
            tv2 = view.findViewById(R.id.text2);
            iv1 = view.findViewById(R.id.image1);
        }
    }
}
