package com.example.myapplication;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class AppNetwork extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
