package com.farruxx.yandextranslator;

/**
 * Created by Farruxx on 09.04.2017.
 */

public interface BasePresenter <T>{
    void setView(T view);
    void onStart(T view);
    void onCreate();
    void onStop();

    void onDestroy();
}
