package com.farruxx.yandextranslator;

/**
 * Created by Farruxx on 09.04.2017.
 */

interface TranslatePresenter extends BasePresenter<TranslateView>{
    void onLanguageSwitchClicked();
    void onLanguageClicked(int id);
    void onClearTextClicked();

}
