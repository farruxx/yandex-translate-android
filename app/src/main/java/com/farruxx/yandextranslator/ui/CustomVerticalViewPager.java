package com.farruxx.yandextranslator.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class CustomVerticalViewPager extends VerticalViewPager{

    private boolean enabled;

    public CustomVerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

}