package com.example.expensestracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    public ArrayList<String> list;
    Context context;

    public CustomAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item,parent,false);
//        MyViewHolder viewHolder = new MyViewHolder(v);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomAdapter.MyViewHolder holder, int position) {
        String[] results = (list.get(position).toString()).split(",");
        holder.amountTextView.setText(results[0]);
        holder.currencyTextView.setText(results[1]);
        holder.typeTextView.setText(results[2]);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView amountTextView, currencyTextView, typeTextView;
        public LinearLayout myLayout;

        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            amountTextView = (TextView) itemView.findViewById(R.id.amountList);
            currencyTextView = (TextView) itemView.findViewById(R.id.currencyList);
            typeTextView = (TextView) itemView.findViewById(R.id.typeList);

            itemView.setOnClickListener(this);
            context = itemView.getContext();

        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), PurchaseEntryDetails.class);
            view.getContext().startActivity(i);
        }
    }
}