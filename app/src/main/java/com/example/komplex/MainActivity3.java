package com.example.komplex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity implements SelectListener{

    private TextView tv_bag;
    private List<Item> bagItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        bagItems = new ArrayList<Item>();
        tv_bag= findViewById(R.id.tv_bag);

        List<Item> items = new ArrayList<Item>();
        items.add(new Item("Tej", 300, R.drawable.tej, false));
        items.add(new Item("Tej1", 300, R.drawable.tej, false));
        items.add(new Item("Tej2", 300, R.drawable.tej, false));
        items.add(new Item("Tej3", 300, R.drawable.tej, false));
        items.add(new Item("Tej4", 300, R.drawable.tej, false));
        items.add(new Item("Tej5", 300, R.drawable.tej, false));
        items.add(new Item("Tej6", 300, R.drawable.tej, false));
        items.add(new Item("Tej7", 300, R.drawable.tej, false));
        items.add(new Item("Tej8", 300, R.drawable.tej, false));
        items.add(new Item("Tej9", 300, R.drawable.tej, false));
        items.add(new Item("Tej10", 300, R.drawable.tej, false));
        items.add(new Item("Tej11", 300, R.drawable.tej, false));
        items.add(new Item("Tej12", 300, R.drawable.tej, false));
        items.add(new Item("Tej13", 300, R.drawable.tej, false));
        items.add(new Item("Tej14", 300, R.drawable.tej, false));
        items.add(new Item("Tej15", 300, R.drawable.tej, false));



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(), items, this));

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    @Override
    public void onItemClicked(Item item) {

        item.setChecked(!item.getChecked());
        if(item.getChecked() == true){
            bagItems.add(item);
        } else if (item.getChecked() == false) {
            bagItems.remove(item);
        }
        tv_bag.setText(String.valueOf(bagItems.size()));
        Toast.makeText(this, item.isChecked(), Toast.LENGTH_SHORT).show();
    }
}