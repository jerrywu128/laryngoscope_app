package com.honestmc.laryngoscopeapp.View.Activity;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.honestmc.laryngoscopeapp.GlobalApp.ExitApp;

public class BaseActivity extends AppCompatActivity {
    private String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ExitApp.getInstance().setCurActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExitApp.getInstance().removeActivity(this);
    }
}
