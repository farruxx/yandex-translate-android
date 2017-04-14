package com.farruxx.yandextranslator;

import com.farruxx.yandextranslator.model.AvailableLanguages;
import com.farruxx.yandextranslator.model.LanguagesResult;
import com.farruxx.yandextranslator.model.TranslateResult;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Farruxx on 10.04.2017.
 */

public interface TranslateService {
    @GET("translate")
    Observable<TranslateResult> translate(@Query("text") String text,
                                          @Query("lang") String lang,
                                          @Query("key") String key);

    @GET("getLangs")
    Observable<ResponseBody> languages(@Query("ui") String locale,
                                       @Query("key") String key);
}
