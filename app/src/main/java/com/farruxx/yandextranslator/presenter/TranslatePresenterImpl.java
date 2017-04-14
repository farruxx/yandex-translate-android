package com.farruxx.yandextranslator.presenter;

import com.farruxx.yandextranslator.data.TranslateProvider;
import com.farruxx.yandextranslator.data.TranslateProviderImpl;
import com.farruxx.yandextranslator.view.TranslateView;
import com.farruxx.yandextranslator.model.AvailableLanguages;
import com.farruxx.yandextranslator.model.TranslateRequest;
import com.farruxx.yandextranslator.model.DestLanguageState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Farruxx on 09.04.2017.
 */

public class TranslatePresenterImpl extends BasePresenterImpl<TranslateView> implements TranslatePresenter {
    TranslateProvider provider;
    Subscription subscription;
    private Subscription subscription2;
    private Subscription subscription3;

    @Override
    public void onCreate() {
        super.onCreate();
        provider = new TranslateProviderImpl();

        subscription = Observable.combineLatest(
                getView().inputChanges()
                        .map(charSequence -> charSequence.toString().trim())
                        .throttleLast(2000, TimeUnit.MILLISECONDS),

                Observable.combineLatest(
                        getView().originLanguage(),
                        getView().destLanguage(),
                        (origin, dest) -> origin + "-" + dest
                )

                , TranslateRequest::new)

                .flatMap(request -> provider.translate(request))
                .onErrorReturn(error -> null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    if (getView() != null) {
                        getView().setTranslation(value);
                    }
                }, error -> {
                    if (getView() != null) {
                        getView().setTranslation(null);
                    }
                });

        Observable<AvailableLanguages> availableLanguagesObservable = Observable.just(Locale.getDefault().getCountry())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(locale -> provider.getLanguages(locale))
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
                }).share();

        subscription2 = availableLanguagesObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(availableLanguages -> {
                    getView().setOriginLanguages(availableLanguages.getOriginDirections());

                });

        subscription3 = Observable.combineLatest(availableLanguagesObservable,
                getView().originLanguage(), DestLanguageState::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tuple -> {
                    getView().setDestLanguages(tuple.availableLanguages.getAvailableDirections(tuple.originLanguage));
                });
    }

    @Override
    public void onLanguageSwitchClicked() {

    }

    @Override
    public void onLanguageClicked(int id) {

    }

    @Override
    public void onClearTextClicked() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
        subscription2.unsubscribe();
        subscription3.unsubscribe();
    }
}
