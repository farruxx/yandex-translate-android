package com.farruxx.yandextranslator.model;

/**
 * Created by Farruxx on 10.04.2017.
 */
public class TranslateRequest {
    public String text;
    public String dir;

    public TranslateRequest(String text, String dir) {
        this.text = text;
        this.dir = dir;
    }
}
