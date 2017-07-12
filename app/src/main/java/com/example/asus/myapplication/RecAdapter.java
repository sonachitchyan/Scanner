package com.example.asus.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class RecAdapter extends RecyclerView.Adapter<RecAdapter.MyViewHolder> {
    private List<Data> dataList;
    DataBaseHandler db;


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Data data = dataList.get(position);
        holder.final_code.setText(data.getCode());
        holder.final_count.setText(String.valueOf(data.getCount()));
        holder.final_price.setText(String.valueOf(data.getPrice()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public RecAdapter(List<Data> dataList, Context context) {
        this.dataList = dataList;
        db = new DataBaseHandler(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView final_code, final_count, final_price;
        public MyViewHolder(final View itemView) {
            super(itemView);
            final_code = (TextView) itemView.findViewById(R.id.final_code);
            final_count = (TextView) itemView.findViewById(R.id.final_count);
            final_price = (TextView) itemView.findViewById(R.id.final_price);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());
                    alertDialog.setTitle("ՔԱՆԱԿ");
                    alertDialog.setMessage("Փոխել քանակը");
                    final EditText input = new EditText(view.getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setPositiveButton("ՓՈԽԵԼ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int a = Integer.valueOf(input.getText().toString());
                            int pos = getAdapterPosition();
                            Data data = dataList.get(pos);
                            data.setCount(a);
                            dataList.remove(pos);
                            dataList.add(data);
                            db.updateInfoByBarcode(data);
                            setDataList(dataList);
                            notifyDataSetChanged();
                        }
                    });
                    alertDialog.setNegativeButton("ՋՆՋԵԼ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int pos = getAdapterPosition();
                            Data d =dataList.get(pos);
                            d.setCount(0);
                            db.updateInfoByBarcode(d);
                            dataList.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    });

                    alertDialog.show();
                }
            });
        }
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    public List<Data> getDataList() {
        return dataList;
    }
}
