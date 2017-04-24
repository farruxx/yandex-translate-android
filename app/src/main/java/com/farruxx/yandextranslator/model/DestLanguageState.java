package com.farruxx.yandextranslator.model;

import com.farruxx.yandextranslator.data.AvailableLanguages;
//helper class for handling composite data
public class DestLanguageState{
  public final AvailableLanguages availableLanguages;
  public final String originLanguage;

  public DestLanguageState(AvailableLanguages availableLanguages, String originLanguage) {
    this.availableLanguages = availableLanguages;
    this.originLanguage = originLanguage;
  }
}