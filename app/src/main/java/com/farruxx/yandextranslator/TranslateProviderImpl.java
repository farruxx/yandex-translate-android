package com.farruxx.yandextranslator;

import com.farruxx.yandextranslator.model.AvailableLanguages;
import com.farruxx.yandextranslator.model.LanguagesResult;
import com.farruxx.yandextranslator.model.TranslateRequest;
import com.farruxx.yandextranslator.model.TranslateResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;
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

    TranslateService translateService= retrofit.create(TranslateService.class);

    @Override
    public Observable<TranslateResult> translate(TranslateRequest request){
        return translateService.translate(request.text, request.dir, KEY);
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
