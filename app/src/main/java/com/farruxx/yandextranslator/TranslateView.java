package com.farruxx.yandextranslator;


import com.farruxx.yandextranslator.model.TranslateDirection;
import com.farruxx.yandextranslator.model.TranslateResult;

import java.util.List;

import rx.Observable;

/**
 * Created by Farruxx on 09.04.2017.
 */

public interface TranslateView {
    void setInput(String input);
    void setTranslation(TranslateResult translation);

    void setDestLanguages(List<TranslateDirection> translateDirectionList);
    void setOriginLanguages(List<TranslateDirection> translateDirectionList);

    Observable<CharSequence> inputChanges();
    Observable<String> originLanguage();
    Observable<String> destLanguage();
}
