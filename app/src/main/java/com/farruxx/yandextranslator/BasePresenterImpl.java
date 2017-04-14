package com.farruxx.yandextranslator;

/**
 * Created by Farruxx on 09.04.2017.
 */

public class BasePresenterImpl<T> implements BasePresenter<T> {
    T view;
    @Override
    public void setView(T view) {
        this.view = view;
    }

    public T getView() {
        return view;
    }

    @Override
    public void onStart(T view) {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        this.view = null;
    }
}
