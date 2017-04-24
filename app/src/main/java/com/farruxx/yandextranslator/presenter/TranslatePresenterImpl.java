package com.farruxx.yandextranslator.presenter;

import android.util.Log;

import com.farruxx.yandextranslator.data.HelperFactory;
import com.farruxx.yandextranslator.data.TranslateProvider;
import com.farruxx.yandextranslator.data.TranslateProviderImpl;
import com.farruxx.yandextranslator.model.Translate;
import com.farruxx.yandextranslator.model.TranslateDirection;
import com.farruxx.yandextranslator.model.TranslateResult;
import com.farruxx.yandextranslator.view.TranslateView;
import com.farruxx.yandextranslator.data.AvailableLanguages;
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
    private AvailableLanguages availableLanguages;

    public TranslatePresenterImpl() {
        provider = new TranslateProviderImpl();
        subscriptionList = new SubscriptionList();

    }

    /**
     * create all observables when view attached
     * @param view
     */
    @Override
    public void attachView(TranslateView view) {
        super.attachView(view);
/**
 * main translate observable- combines typing, selecting language flows, and performs network request
 * and publishes result for history and favorite observables
 */
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

        /**
         * show the translation result
         */
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

        /**
         *  manages favorites button activate or not to show if translation favorite
         */
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
        /**
         * history observable- reminds not faster than 5 seconds after success translation
         */
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

        /**
         * fetching available languages from server observable
         */
        availableLanguagesObservable
                = provider.getAvailableLanguages(Locale.getDefault()).share();

        /**
         * when languages fetched, set them to origin language spinner
         */
        subscriptionList.add(

                availableLanguagesObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(availableLanguages1 -> availableLanguages1 != null)
                        .subscribe(availableLanguages -> {
                            this.availableLanguages = availableLanguages;
                            List<TranslateDirection> data = availableLanguages.getOriginDirections();
                            int position = 0;
                            String selectedValue;
                            if (availableLanguages.swap) {
                                selectedValue = getView().destLanguageCode();
                                if (selectedValue == null) {
                                    selectedValue = "ru";
                                }
                                if (data != null) {
                                    for (int i = 0; i < data.size(); i++) {
                                        TranslateDirection direction = data.get(i);
                                        if (direction != null && selectedValue.equals(direction.code)) {
                                            position = i;
                                        }
                                    }
                                }
                                getView().setOriginLanguages(data, position);
                            } else {
                                selectedValue = getView().originLanguageCode();
                                if (selectedValue == null) {
                                    selectedValue = "ru";
                                }
                                if (data != null) {
                                    for (int i = 0; i < data.size(); i++) {
                                        TranslateDirection direction = data.get(i);
                                        if (direction != null && selectedValue.equals(direction.code)) {
                                            position = i;
                                        }
                                    }
                                }
                                getView().setOriginLanguages(data, position);
                            }
                        }, error -> error.printStackTrace())
        );

        /**
         * than combine lastest values of fetching available languages and selected origin
         * language, show dest languages
         */
        subscriptionList.add(

                Observable.combineLatest(availableLanguagesObservable,
                        getView().originLanguage(), DestLanguageState::new)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(tuple -> {
                            List<TranslateDirection> data = tuple.availableLanguages.getAvailableDirections(tuple.originLanguage);
                            getView().setDestLanguages(data, 0);
                        }, error -> error.printStackTrace())
        );
        /**
         * clear button logic
         */
        subscriptionList.add(

                getView().clearButton()
                        .subscribe(_void -> getView().setInput(""), error -> error.printStackTrace())
        );
        /**
         * when there is an available translate and favorite button clicked save
         * translation as favorite, if it already in history, make it favorite
         */
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
        /**
         * connect multiple subscribers into one observer
         */
        subscriptionList.add(

                translateObservable.connect()
        );
    }

    /**
     * unsubscribe when view destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptionList.unsubscribe();
    }
}
