package com.farruxx.yandextranslator.presenter;

/**
 * Created by Farruxx on 09.04.2017.
 */

public interface BasePresenter <T>{
    void attachView(T view);
    void onStart();
    void onCreate();
    void onStop();

    void onDestroy();
}
