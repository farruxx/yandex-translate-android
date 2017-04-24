package com.farruxx.yandextranslator.model;

import java.util.List;

/**
 * Created by Farruxx on 10.04.2017.
 */
//retrofit response structure
public class TranslateResult {
    public List<String> text;
    public String lang;
    public int code;
    public TranslateRequest request;
    public boolean checked;

    public TranslateResult withRequest(TranslateRequest request) {
        this.request = request;
        return this;
    }

    public TranslateResult withFavoritesChecked(boolean checked) {
        this.checked = checked;
        return this;
    }
}
