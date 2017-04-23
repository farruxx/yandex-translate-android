package com.farruxx.yandextranslator.data;

import com.farruxx.yandextranslator.model.AvailableLanguages;
import com.farruxx.yandextranslator.model.TranslateRequest;
import com.farruxx.yandextranslator.model.TranslateResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Farruxx on 10.04.2017.
 */

public class TranslateProviderImpl implements TranslateProvider {
    private static final String KEY = "trnsl.1.1.20170409T121317Z.c46466df3b671d56.a5612c784dd6e7ddbc7ba0f73f9e787ae12fb21e";
    OkHttpClient okHttpClient = new OkHttpClient();
    RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
    Gson gson = new Gson();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://translate.yandex.net/api/v1.5/tr.json/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(rxAdapter)
            .build();

    TranslateService translateService = retrofit.create(TranslateService.class);

    @Override
    public Observable<TranslateResult> translate(TranslateRequest request) {
        Observable<TranslateResult> result;
        //include request to result
        if (request.text.length() != 0) {
            result = Observable.combineLatest(
                    translateService.translate(request.text, request.origin + "-" + request.dest, KEY),
                    Observable.just(request),
                    (translateResult, request1) -> translateResult.withRequest(request1));
        } else {
            result = Observable.just(null);
        }
        return result;
    }

    public Observable<String> getLanguages(String locale) {
        return translateService.languages(locale, KEY)
                .map(responseBody -> {
                    try {
                        return responseBody.string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public Observable<AvailableLanguages> getAvailableLanguages(Locale locale) {
        return getLanguages(locale.getLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    try {
                        return new JSONObject(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(jsonObject -> {
                    try {
                        return new AvailableLanguages(jsonObject);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
