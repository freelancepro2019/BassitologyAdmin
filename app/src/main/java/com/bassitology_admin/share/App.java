package com.bassitology_admin.share;


import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.bassitology_admin.language.Language;


public class App extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Language.updateResources(newBase,"ar"));
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }
}

