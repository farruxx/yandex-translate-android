package com.farruxx.yandextranslator.view;

import android.app.Application;

import com.farruxx.yandextranslator.data.HelperFactory;

/**
 * Created by Farruxx on 23.04.2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(this);
    }
}
