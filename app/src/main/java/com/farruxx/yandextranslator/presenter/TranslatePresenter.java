package com.farruxx.yandextranslator.presenter;

import com.farruxx.yandextranslator.view.TranslateView;

/**
 * Created by Farruxx on 09.04.2017.
 */

public interface TranslatePresenter extends BasePresenter<TranslateView> {
    void onLanguageSwitchClicked();
    void onLanguageClicked(int id);
    void onClearTextClicked();

}
