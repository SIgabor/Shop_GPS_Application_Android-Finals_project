package com.example.komplex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    TextView textView;
    String str;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d("MyTag", "onCreate done");

        textView =  findViewById(R.id.tv_baggedItems);

        Intent recieverIntent = getIntent();
        Bundle args = recieverIntent.getBundleExtra("BUNDLE");
        List<Item> bagItems = new ArrayList<Item>();
        bagItems = (ArrayList<Item>) args.getSerializable("BAG_ITEMS");
        Log.d("MyTag", "Intent received");

        for(int i = 0; i < bagItems.size(); i++){
            if(i == 0){
                str = bagItems.get(i).getName() + "\n";
            }else {
                str = str + bagItems.get(i).getName() + "\n";
            }
        }

        textView.setText(str);


    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_rigth);
    }
}