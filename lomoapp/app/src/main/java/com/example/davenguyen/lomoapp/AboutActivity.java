package com.example.davenguyen.lomoapp;


import android.os.Bundle;

public class AboutActivity extends BaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_about;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
    }

    private void initializeUI() {

    }
}
