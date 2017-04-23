package com.farruxx.yandextranslator.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.farruxx.yandextranslator.R;
import com.farruxx.yandextranslator.adapter.TranslateDirectionAdapter;
import com.farruxx.yandextranslator.model.TranslateDirection;
import com.farruxx.yandextranslator.model.TranslateResult;
import com.farruxx.yandextranslator.presenter.TranslatePresenterImpl;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Farruxx on 23.04.2017.
 */
public class TranslateFragment extends android.support.v4.app.Fragment implements TranslateView, View.OnClickListener {

    TranslatePresenterImpl presenter = new TranslatePresenterImpl();

    private TranslateDirectionAdapter originAdapter;
    private TranslateDirectionAdapter destAdapter;
    private Observable<String> sharedOrigin;

    @BindView(R.id.clear_button)
    View clearButton;

    @BindView(R.id.input_ev)
    EditText inputEv;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.output_tv)
    TextView outputTv;

    @BindView(R.id.origin_spinner)
    Spinner originSpinner;

    @BindView(R.id.dest_spinner)
    Spinner destSpinner;

    @BindView(R.id.copyright)
    View copyright;
    @BindView(R.id.favorite)
    CheckBox favoriteCb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.translate_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        originAdapter = new TranslateDirectionAdapter(getActivity());
        destAdapter = new TranslateDirectionAdapter(getActivity());
        originSpinner.setAdapter(originAdapter);
        destSpinner.setAdapter(destAdapter);
        copyright.setOnClickListener(this);
        sharedOrigin = RxAdapterView.itemSelections(originSpinner)
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(position -> position >= 0)
                .map(position -> originAdapter.getItem(position).code)
                .share();

        presenter.attachView(this);
        presenter.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void setInput(String input) {
        inputEv.setText(input);
    }

    @Override
    public void setTranslation(TranslateResult translation) {
        if (translation != null && translation.text.size() > 0) {
            outputTv.setText(translation.text.get(0));
        } else {
            outputTv.setText("");
        }
    }

    @Override
    public void setDestLanguages(List<TranslateDirection> translateDirectionList) {
        destAdapter.setItems(translateDirectionList);
    }

    @Override
    public void setOriginLanguages(List<TranslateDirection> translateDirectionList) {
        originAdapter.setItems(translateDirectionList);
    }

    @Override
    public Observable<CharSequence> inputChanges() {
        return RxTextView.textChanges(inputEv)
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> originLanguage() {
        return sharedOrigin;
    }

    @Override
    public Observable<String> destLanguage() {
        return RxAdapterView.itemSelections(destSpinner)
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(position -> position >= 0)
                .map(position -> destAdapter.getItem(position).code);
    }

    @Override
    public Observable<Void> clearButton() {
        return RxView.clicks(clearButton);
    }

    @Override
    public Observable<Boolean> favorite() {
        return RxCompoundButton.checkedChanges(favoriteCb)
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void setFavoritesChecked(boolean value) {
        favoriteCb.setChecked(value);
    }

    //It is better than butterknife's onclick annotation coz references are explicit
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copyright: {
                Uri webpage = Uri.parse(getString(R.string.url));
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            }
        }
    }
}