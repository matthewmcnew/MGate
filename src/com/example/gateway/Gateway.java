package com.example.gateway;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Gateway extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gateway, menu);
        return true;
    }
}
