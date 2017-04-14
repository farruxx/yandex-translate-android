package com.farruxx.yandextranslator.data;

import com.farruxx.yandextranslator.model.TranslateRequest;
import com.farruxx.yandextranslator.model.TranslateResult;

import rx.Observable;

/**
 * Created by Farruxx on 10.04.2017.
 */

public interface TranslateProvider {
    Observable<TranslateResult> translate(TranslateRequest request);

    Observable<String> getLanguages(String locale);
}
