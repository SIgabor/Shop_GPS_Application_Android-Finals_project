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
    private int desiredShop = -1;


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

        Intent intent = getIntent();
        desiredShop = intent.getIntExtra("desiredShop", -1);
        Log.d("MyTag", "intent recieved: " + desiredShop);

        items = new ArrayList<>();

        switch (desiredShop){
            case 0:
                items.add(new Item("Cappy", 300, R.drawable.cappy, false, 0, 8));
                items.add(new Item("Corona Sör", 300, R.drawable.corona, false, 0, 1));
                items.add(new Item("Csirkemell", 300, R.drawable.csirkemell, false, 0, 1));
                items.add(new Item("Fokhagymás Chips", 300, R.drawable.fokhagymaschips, false, 1, 1));
                items.add(new Item("Hazai Vaj", 300, R.drawable.hazaivaj, false, 1, 1));
                items.add(new Item("HeyHo", 300, R.drawable.heyho, false, 1, 3));
                items.add(new Item("Paprikás Chips", 300, R.drawable.paprikschips, false, 6, 3));
                items.add(new Item("Pecsi Sör", 300, R.drawable.pecsisor, false, 2, 3));
                items.add(new Item("Pilsner Sör", 300, R.drawable.pilsner, false, 2, 3));
                items.add(new Item("Alma", 300, R.drawable.alma, false, 3, 5));
                items.add(new Item("Korte", 300, R.drawable.korte, false, 3, 5));
                items.add(new Item("Ropi", 300, R.drawable.ropi, false, 3, 5));
                items.add(new Item("Sajt", 300, R.drawable.sajt, false, 4, 5));
                items.add(new Item("Szilva", 300, R.drawable.szilva, false, 4, 7));
                items.add(new Item("Sajtos Chips", 300, R.drawable.sajtoschips, false, 4, 7));
                items.add(new Item("Kenyer", 300, R.drawable.kenyer, false, 5, 7));
                items.add(new Item("Saláta", 300, R.drawable.salata, false, 5, 7));
                items.add(new Item("Soproni Sör", 300, R.drawable.soproni, false, 5, 7));
                items.add(new Item("Sós Chips", 300, R.drawable.soschips, false, 5, 7));
                items.add(new Item("Venusz Light Vaj", 300, R.drawable.venusz_light_vaj, false, 5, 7));

                break;
            case 1:
                items.add(new Item("Cappy", 300, R.drawable.cappy, false, 0, 1));
                items.add(new Item("Corona Sör", 300, R.drawable.corona, false, 1, 1));
                items.add(new Item("Csirkemell", 300, R.drawable.csirkemell, false, 2, 3));
                items.add(new Item("Fokhagymás Chips", 300, R.drawable.fokhagymaschips, false, 2, 2));
                items.add(new Item("Hazai Vaj", 300, R.drawable.hazaivaj, false, 2, 4));
                items.add(new Item("HeyHo", 300, R.drawable.heyho, false, 1, 3));
                break;
        }




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

        senderIntent.putExtra("desiredShop", desiredShop);

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