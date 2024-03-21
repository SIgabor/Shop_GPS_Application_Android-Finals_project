package com.example.komplex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity implements SelectListener{

    private TextView tv_bag;
    private MyAdapter myAdapter;
    private List<Item> items;
    private List<Item> bagItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        SearchView searchView = findViewById(R.id.searchView);
        Log.d("MyTag", "searchView found");
        searchView.clearFocus();
        Log.d("MyTag", "searchView focus cleared");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("MyTag", "onQueryTextChange opened");
                filterList(newText);
                return false;
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        bagItems = new ArrayList<>();
        tv_bag= findViewById(R.id.tv_bag);

        items = new ArrayList<>();
        items.add(new Item("Tej", 300, R.drawable.tej, false, 0, 8));
        items.add(new Item("Tej1", 300, R.drawable.tej, false, 0, 1));
        items.add(new Item("Tej2", 300, R.drawable.tej, false, 0, 1));
        items.add(new Item("Tej3", 300, R.drawable.tej, false, 1, 1));
        items.add(new Item("Tej4", 300, R.drawable.tej, false, 1, 1));
        items.add(new Item("Tej5", 300, R.drawable.tej, false, 1, 3));
        items.add(new Item("Tej6", 300, R.drawable.tej, false, 6, 3));
        items.add(new Item("Tej7", 300, R.drawable.tej, false, 2, 3));
        items.add(new Item("Tej8", 300, R.drawable.tej, false, 2, 3));
        items.add(new Item("Alma", 300, R.drawable.tej, false, 3, 5));
        items.add(new Item("Korte", 300, R.drawable.tej, false, 3, 5));
        items.add(new Item("Tej11", 300, R.drawable.tej, false, 3, 5));
        items.add(new Item("Tej12", 300, R.drawable.tej, false, 4, 5));
        items.add(new Item("Szilva", 300, R.drawable.tej, false, 4, 7));
        items.add(new Item("Tej14", 300, R.drawable.tej, false, 4, 7));
        items.add(new Item("Kenyer", 300, R.drawable.tej, false, 5, 7));


        myAdapter = new MyAdapter(getApplicationContext(), items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

    }

    private void filterList(String text) {
        List<Item> filteredList = new ArrayList<>();
        for(Item item : items){
            if(item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
                Log.d("MyTag", "item added");
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this, "Nincs ilyen termék!", Toast.LENGTH_SHORT).show();
        }else{
            myAdapter.setFilteredList(filteredList);
        }
    }


    @Override
    public void onBackPressed(){

        Intent senderIntent = new Intent(this, MainActivity2.class);
        Bundle args = new Bundle();
        args.putSerializable("BAG_ITEMS", (Serializable) bagItems);
        senderIntent.putExtra("BUNDLE", args);
        startActivity(senderIntent);

        Log.d("MyTag", "onCreate: Intent sent");

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    @Override
    public void onItemClicked(Item item) {

        item.setChecked(!item.getChecked());
        if(item.getChecked()){
            bagItems.add(item);
        } else {
            bagItems.remove(item);
        }
        tv_bag.setText(String.valueOf(bagItems.size()));


    }
}