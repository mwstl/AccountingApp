package com.example.expensestracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    public ArrayList<String> list;
    Context context;
    MyDatabase db;

    public CustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
        db = new MyDatabase(context);
    }

    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item,parent,false);
//        MyViewHolder viewHolder = new MyViewHolder(v);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        String[] results = (list.get(position).toString()).split(",");
        holder.amountTextView.setText(results[0]);
        holder.currencyTextView.setText(results[1]);
        holder.typeTextView.setText(results[2]);
        int id = Integer.parseInt(results[4]);
        int purchase = holder.getAdapterPosition();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("PURCHASE POSITION", "Position: " + purchase);
                Intent i = new Intent(view.getContext(), PurchaseEntryDetails.class);
                i.putExtra("purchase_id", id);
                view.getContext().startActivity(i);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setMessage("Do you want to delete?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Toast.makeText(context, "Success deletion", Toast.LENGTH_SHORT).show();

                        list.remove(purchase);
                        notifyItemRemoved(purchase);
                        db.deleteData(id);
                        notifyDataSetChanged();
                        Log.d("DELETION", "SUCCESSFULLY DELETED id = " + id);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Log.d("CANCEL DELETION", "CANCELLED DELETION");
                        Toast.makeText(context, "Cancel deletion", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });
    }

//    private void result(Cursor c, int id) {
//        if (c.getCount() > 0) {
//            db.delete(Constants.TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
//            notifyDataSetChanged();
//        } else {
//            Toast.makeText(context, "Error removing item", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView amountTextView, currencyTextView, typeTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            amountTextView = (TextView) itemView.findViewById(R.id.amountList);
            currencyTextView = (TextView) itemView.findViewById(R.id.currencyList);
            typeTextView = (TextView) itemView.findViewById(R.id.typeList);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), PurchaseEntryDetails.class);
            view.getContext().startActivity(i);
        }
    }
}