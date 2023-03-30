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
        // Initialize objects
        this.list = list;
        this.context = context;
        db = new MyDatabase(context);
    }

    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // View holder for recycler view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        // Retrieve and initialize relevant database info for recycler view
        String[] results = (list.get(position).toString()).split(",");
        holder.amountTextView.setText(results[0]);
        holder.currencyTextView.setText(results[1]);
        holder.typeTextView.setText(results[2]);
        holder.dateTextView.setText(results[5]);
        // Database item id
        int id = Integer.parseInt(results[4]);
        // Recycler view item position
        int purchase = holder.getAdapterPosition();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Debug help
//                Log.d("PURCHASE POSITION", "Position: " + purchase);

                // If user clicks on recycler view item, put current id and open purchase entry details activity
                Intent i = new Intent(view.getContext(), PurchaseEntryDetails.class);
                i.putExtra("purchase_id", id);
                view.getContext().startActivity(i);
            }
        });

        // If user long clicks on recycler view item, show alert to delete item from database
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Create an alert confirming deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setMessage("Do you want to delete?");

                // Ok option on alert
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Delete item from database
                        Toast.makeText(context, "Success deletion", Toast.LENGTH_SHORT).show();

                        list.remove(purchase);
                        notifyItemRemoved(purchase);
                        db.deleteData(id);
                        notifyDataSetChanged();

                        // Debug help
//                        Log.d("DELETION", "SUCCESSFULLY DELETED id = " + id);
                    }
                });

                // Cancel option on alert
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Debug help
//                        Log.d("CANCEL DELETION", "CANCELLED DELETION");
                        Toast.makeText(context, "Cancel deletion", Toast.LENGTH_SHORT).show();
                    }
                });

                // Create and show alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView amountTextView, currencyTextView, typeTextView, dateTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            // Show amount, currency, and type of expense in recycler view item
            amountTextView = (TextView) itemView.findViewById(R.id.amountList);
            currencyTextView = (TextView) itemView.findViewById(R.id.currencyList);
            typeTextView = (TextView) itemView.findViewById(R.id.typeList);
            dateTextView = (TextView) itemView.findViewById(R.id.dateList);
        }

        @Override
        public void onClick(View view) {
            // If users clicks on item in recycler view, show item details
            Intent i = new Intent(view.getContext(), PurchaseEntryDetails.class);
            view.getContext().startActivity(i);
        }
    }
}