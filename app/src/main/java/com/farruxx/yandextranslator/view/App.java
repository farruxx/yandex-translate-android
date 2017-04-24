package com.farruxx.yandextranslator.view;

import android.app.Application;
import android.content.Context;

import com.farruxx.yandextranslator.data.HelperFactory;

import java.io.File;

/**
 * Created by Farruxx on 23.04.2017.
 */

public class App extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        HelperFactory.setHelper(this);
    }
}
