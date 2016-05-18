package com.example.yanjiang.stockchart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @Bind(R.id.btn)
    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent = new Intent(MainActivity.this, MinutesActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.btn)
    public void onClick() {

        Intent intent = new Intent(MainActivity.this, MinutesActivity.class);
        startActivity(intent);
    }
}
