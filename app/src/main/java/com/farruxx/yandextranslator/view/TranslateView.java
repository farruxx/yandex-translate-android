package com.farruxx.yandextranslator.view;


import com.farruxx.yandextranslator.model.Translate;
import com.farruxx.yandextranslator.model.TranslateDirection;
import com.farruxx.yandextranslator.model.TranslateRequest;
import com.farruxx.yandextranslator.model.TranslateResult;

import java.util.List;

import rx.Observable;

/**
 * Created by Farruxx on 09.04.2017.
 */

public interface TranslateView {
    void setInput(String input);
    void setTranslation(TranslateResult translation);

    void setDestLanguages(List<TranslateDirection> translateDirectionList, int position);

    void setOriginLanguages(List<TranslateDirection> translateDirectionList, int position);

    Observable<CharSequence> inputChanges();
    Observable<String> originLanguage();
    Observable<String> destLanguage();
    Observable<Void> clearButton();
    Observable<Boolean> favorite();
    Observable<Void> swapButton();

    void setFavoritesChecked(boolean value);

    String getInput();

    String destLanguageCode();
    String originLanguageCode();
}
