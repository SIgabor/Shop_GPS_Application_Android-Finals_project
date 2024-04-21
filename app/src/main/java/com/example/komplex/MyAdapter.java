package com.example.komplex;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<Item> items;
    private SelectListener listener;


    public MyAdapter(Context context, List<Item> items, SelectListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }
    

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Collections.sort(items, new AbcComparator());
        Log.d("MyTag", "onCreateViewHolder sorted");
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.each_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Collections.sort(items, new AbcComparator());
        Collections.sort(items, new CustomComparator().reversed());
        Log.d("MyTag", "onBindViewHolder");
        holder.nameView.setText(items.get(position).getName());
        holder.priceView.setText(items.get(position).getPrice() + " Ft");
        holder.imageview.setImageResource(items.get(position).getImage());

        if(items.get(position).getChecked()){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#b5f5d1"));
        }else{
            holder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(items.get(holder.getAdapterPosition()));

                Log.d("MyTag", "sorted");
                notifyDataSetChanged();
            }
        });



    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class CustomComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return Boolean.compare(o1.getChecked(), o2.getChecked());
        }
    }

    public class AbcComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public void setFilteredList(List<Item> filteredList){
        this.items = filteredList;
        notifyDataSetChanged();
    }
}
