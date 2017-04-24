package com.farruxx.yandextranslator.model;

/**
 * Created by Farruxx on 10.04.2017.
 */

//Translating request unit
public class TranslateRequest {
    public String text;
    public String origin;
    public String dest;

    public TranslateRequest(String origin, String dest) {
        this.origin = origin;
        this.dest = dest;
    }
    public TranslateRequest withText(String text){
        this.text = text;
        return this;
    }

}
