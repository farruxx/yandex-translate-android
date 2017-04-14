package com.farruxx.yandextranslator.model;

public class DestLanguageState{
  public final AvailableLanguages availableLanguages;
  public final String originLanguage;

  public DestLanguageState(AvailableLanguages availableLanguages, String originLanguage) {
    this.availableLanguages = availableLanguages;
    this.originLanguage = originLanguage;
  }
}