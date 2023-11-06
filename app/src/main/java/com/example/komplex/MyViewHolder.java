package com.example.komplex;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageview;
    TextView nameView, priceView;
    CardView cardView;

    public MyViewHolder(@NonNull View each_item) {
        super(each_item);
        imageview = each_item.findViewById(R.id.imageview);
        nameView = each_item.findViewById(R.id.name);
        priceView = each_item.findViewById(R.id.price);
        cardView = each_item.findViewById(R.id.eachCardView);
    }
}
