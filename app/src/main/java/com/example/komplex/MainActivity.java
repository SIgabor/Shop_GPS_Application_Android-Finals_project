package com.example.komplex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        btn_shopOne = findViewById(R.id.btn_shopOne);
        btn_shopTwo = findViewById(R.id.btn_shopTwo);

        button.setEnabled(false);
        button.setAlpha(0.3f);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });

        btn_shopOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desiredShop = 0;
                button.setEnabled(true);
                button.setAlpha(1f);
            }
        });

        btn_shopTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desiredShop = 1;
                button.setEnabled(true);
                button.setAlpha(1f);
            }
        });

    }

    private void openActivity2() {
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("desiredShop", desiredShop);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
    }
}