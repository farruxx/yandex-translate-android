package com.farruxx.yandextranslator.presenter;

import android.util.Log;

import com.farruxx.yandextranslator.data.HelperFactory;
import com.farruxx.yandextranslator.data.TranslateProvider;
import com.farruxx.yandextranslator.data.TranslateProviderImpl;
import com.farruxx.yandextranslator.model.Translate;
import com.farruxx.yandextranslator.model.TranslateDirection;
import com.farruxx.yandextranslator.model.TranslateResult;
import com.farruxx.yandextranslator.view.TranslateView;
import com.farruxx.yandextranslator.model.AvailableLanguages;
import com.farruxx.yandextranslator.model.TranslateRequest;
import com.farruxx.yandextranslator.model.DestLanguageState;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;


/**
 * Created by Farruxx on 09.04.2017.
 */

public class TranslatePresenterImpl extends BasePresenterImpl<TranslateView> implements BasePresenter<TranslateView> {
    private final SubscriptionList subscriptionList;
    private TranslateProvider provider;
    private Observable<AvailableLanguages> availableLanguagesObservable;

    public TranslatePresenterImpl() {
        provider = new TranslateProviderImpl();
        subscriptionList = new SubscriptionList();

    }

    @Override
    public void attachView(TranslateView view) {
        super.attachView(view);

        ConnectableObservable<TranslateResult> translateObservable =
                Observable.combineLatest(
                        getView().inputChanges()
                                .map(charSequence -> charSequence.toString().trim())
                                .throttleLast(2000, TimeUnit.MILLISECONDS),

                        Observable.combineLatest(
                                getView().originLanguage(),
                                getView().destLanguage(),
                                TranslateRequest::new)

                        , (text, request) -> request.withText(text))

                        .flatMap(request -> provider.translate(request))
                        .onErrorReturn(error -> null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .publish();

//        getView().swapButton()
        subscriptionList.add(

                translateObservable
                        .subscribe(value -> {
                            if (getView() != null) {
                                getView().setTranslation(value);
                            }
                        }, error -> {
                            if (getView() != null) {
                                getView().setTranslation(null);
                            }
                        }));
        subscriptionList.add(

                translateObservable
                        .filter(translateResult -> translateResult != null && translateResult.text != null && translateResult.text.size() > 0)
                        .map(translateResult -> {
                            try {
                                return HelperFactory.getHelper().getTranslateDao().isInFavorites(
                                        translateResult.request.origin,
                                        translateResult.request.dest,
                                        translateResult.request.text
                                );
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }).subscribe(value -> {
                    if (getView() != null) {
                        getView().setFavoritesChecked(value);
                    }
                }, error -> error.printStackTrace()));
        subscriptionList.add(

                translateObservable
                        .throttleLast(5000, TimeUnit.MILLISECONDS)
                        .filter(translateResult -> translateResult != null && translateResult.text != null && translateResult.text.size() > 0)
                        .subscribe(translateResult -> {
                            try {
                                TranslateRequest request = translateResult.request;
                                HelperFactory.getHelper().getTranslateDao().save(
                                        new Translate(
                                                request.origin,
                                                request.dest,
                                                request.text,
                                                translateResult.text.get(0),
                                                System.currentTimeMillis()));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            Log.e("TranslatePresenter", error.getMessage());
                        }));


        availableLanguagesObservable
                = provider.getAvailableLanguages(Locale.getDefault()).share();

        subscriptionList.add(

                availableLanguagesObservable

                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(availableLanguages -> {
                            List<TranslateDirection> data = availableLanguages.getOriginDirections();
                            int position = 0;
                            if (data != null) {
                                for (int i = 0; i < data.size(); i++) {
                                    TranslateDirection direction = data.get(i);
                                    if (direction != null && "ru".equals(direction.code)) {
                                        position = i;
                                    }
                                }
                            }
                            getView().setOriginLanguages(data, position);
                        }, error -> error.printStackTrace())
        );

        subscriptionList.add(

                Observable.combineLatest(availableLanguagesObservable,
                        getView().originLanguage(), DestLanguageState::new)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(tuple -> {
                            List<TranslateDirection> data = tuple.availableLanguages.getAvailableDirections(tuple.originLanguage);
                            int position = 0;
                            if (data != null) {
                                for (int i = 0; i < data.size(); i++) {
                                    TranslateDirection direction = data.get(i);
                                    if (direction != null && "en".equals(direction.code)) {
                                        position = i;
                                    }
                                }
                            }
                            getView().setDestLanguages(data, position);
                        }, error -> error.printStackTrace())
        );

        subscriptionList.add(

                getView().clearButton()
                        .subscribe(_void -> getView().setInput(""), error -> error.printStackTrace())
        );

        subscriptionList.add(
                Observable.combineLatest(
                        translateObservable.filter(translateResult -> translateResult != null
                                && translateResult.text != null
                                && translateResult.text.size() > 0),
                        getView().favorite(),
                        (translateResult, checked) -> translateResult.withFavoritesChecked(checked)
                )
                        .subscribe(translateResult -> {
                            try {
                                TranslateRequest request = translateResult.request;
                                HelperFactory.getHelper().getTranslateDao()
                                        .adjustFavorites(
                                                new Translate(request.origin,
                                                        request.dest,
                                                        request.text,
                                                        translateResult.text.get(0),
                                                        System.currentTimeMillis())
                                                        .withFavorites(translateResult.checked));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }, error -> error.printStackTrace())
        );
        subscriptionList.add(

                translateObservable.connect()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptionList.unsubscribe();
    }
}
