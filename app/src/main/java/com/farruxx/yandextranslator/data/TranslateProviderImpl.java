package com.farruxx.yandextranslator.data;

import com.farruxx.yandextranslator.model.TranslateRequest;
import com.farruxx.yandextranslator.model.TranslateResult;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
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
        if (request.text.length() != 0) {
            result = translateService.translate(request.text, request.dir, KEY);
        } else {
            result = Observable.just(null);
        }
        return result;
    }

    @Override
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

}