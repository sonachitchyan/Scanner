package com.example.asus.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class RecAdapter extends RecyclerView.Adapter<RecAdapter.MyViewHolder> {
    private List<Data> dataList;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Data data = dataList.get(position);
        holder.final_code.setText(data.getCode());
        holder.final_name.setText(data.getName());
        holder.final_count.setText(data.getCount()+"/" +data.getCount_db());
        holder.final_price.setText(String.valueOf(data.getPrice()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public RecAdapter(List<Data> dataList) {
        this.dataList = dataList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView final_code, final_name, final_count, final_price;
        public MyViewHolder(View itemView) {
            super(itemView);
            final_code = (TextView) itemView.findViewById(R.id.final_code);
            final_name = (TextView) itemView.findViewById(R.id.final_name);
            final_count = (TextView) itemView.findViewById(R.id.final_count);
            final_price = (TextView) itemView.findViewById(R.id.final_price);
        }
    }
}
