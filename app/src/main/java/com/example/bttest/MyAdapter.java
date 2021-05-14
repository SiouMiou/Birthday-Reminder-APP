package com.example.bttest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        Button INFO;
        private List<ScannedData> arrayList = new ArrayList<>();
        private Activity activity;

        public MyAdapter(ArrayList<ScannedData> arrayList) {
            this.arrayList = arrayList;
        }
        /**清除搜尋到的裝置列表*/
        public void clearDevice(){
            this.arrayList.clear();
            notifyDataSetChanged();
        }
        /**若有不重複的裝置出現，則加入列表中*/
        public void addDevice(ArrayList<ScannedData> arrayList){
            this.arrayList = arrayList;
            notifyDataSetChanged();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName,tvAddress,tvInfo,tvRssi;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.textView_DeviceName);
                tvAddress = itemView.findViewById(R.id.textView_Address);
                tvRssi = itemView.findViewById(R.id.textView_Rssi);
                INFO=itemView.findViewById(R.id.ButInfo);
                //tvInfo = itemView.findViewById(R.id.textView_ScanRecord);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);

            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvName.setText(arrayList.get(position).getDeviceName());
            holder.tvAddress.setText("裝置位址："+arrayList.get(position).getAddress());
            //holder.tvInfo.setText("裝置挾帶的資訊：\n"+arrayList.get(position).getDeviceByteInfo());
            holder.tvRssi.setText("訊號強度："+arrayList.get(position).getRssi());
            INFO.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, Info.class);//建立一個inent物件，並建立一個MA2的活動意圖
                    Bundle bundle =new Bundle();
                    bundle.putString("infomation",arrayList.get(position).getDeviceByteInfo());//要傳送的資料 key&value
                    bundle.putString("DN",arrayList.get(position).getDeviceName());//要傳送的資料 key&value
                    bundle.putString("MA",arrayList.get(position).getAddress());//要傳送的資料 key&value
                    intent.putExtras(bundle);//把bundle設給intent

                    context. startActivity(intent);

                }
            } );

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public void call_info(){

        }

    }