package com.farruxx.yandextranslator.presenter;

/**
 * Created by Farruxx on 09.04.2017.
 */

public class BasePresenterImpl<T> implements BasePresenter<T> {
    T view;
    @Override
    public void attachView(T view) {
        this.view = view;
    }

    public T getView() {
        return view;
    }

    @Override
    public void onStart() {

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
