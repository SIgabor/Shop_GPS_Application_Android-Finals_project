package com.example.komplex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private Button btn_shopOne;
    private Button btn_shopTwo;
    private int desiredShop = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn_toActivity2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity3();
            }
        });

        btn_shopOne = findViewById(R.id.btn_shopOne);
        btn_shopTwo = findViewById(R.id.btn_shopTwo);

        btn_shopOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desiredShop = 0;
            }
        });

        btn_shopTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desiredShop = 1;
            }
        });
        




    }

    /*private void openActivity2() {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_rigth, R.anim.slide_to_left);
    }*/

    private void openActivity3() {
        Intent intent = new Intent(this, MainActivity3.class);
        intent.putExtra("desiredShop", desiredShop);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
    }
}