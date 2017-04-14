package com.farruxx.yandextranslator.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;

import com.farruxx.yandextranslator.R;
import com.farruxx.yandextranslator.adapter.TranslateDirectionAdapter;
import com.farruxx.yandextranslator.databinding.ActivityMainBinding;
import com.farruxx.yandextranslator.model.TranslateDirection;
import com.farruxx.yandextranslator.model.TranslateResult;
import com.farruxx.yandextranslator.presenter.TranslatePresenter;
import com.farruxx.yandextranslator.presenter.TranslatePresenterImpl;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements TranslateView{

    ActivityMainBinding binding;
    TranslatePresenter presenter = new TranslatePresenterImpl();
    private Spinner originSpinner;
    private Spinner destSpinner;
    private TranslateDirectionAdapter originAdapter;
    private TranslateDirectionAdapter destAdapter;
    private Observable<String> sharedOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        originSpinner = (Spinner) findViewById(R.id.origin_spinner);
        destSpinner = (Spinner) findViewById(R.id.dest_spinner);
        originAdapter = new TranslateDirectionAdapter(this);
        destAdapter = new TranslateDirectionAdapter(this);
        originSpinner.setAdapter(originAdapter);
        destSpinner.setAdapter(destAdapter);
        sharedOrigin = RxAdapterView.itemSelections(originSpinner)
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(position->position>=0)
                .map(position-> originAdapter.getItem(position).code).share();
        binding.clearButton.setOnClickListener((v)->binding.editText.setText(""));
        presenter.setView(this);
        presenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart(this);
    }

    @Override
    public void setInput(String input) {
        binding.editText.setText(input);
    }

    @Override
    public void setTranslation(TranslateResult translation) {
        if(translation != null && translation.text.size()>0) {
            binding.textView.setText(translation.text.get(0));
        }else {
            binding.textView.setText("");
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
        return RxTextView.textChanges(binding.editText)
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
                .filter(position->position>=0)
                .map(position-> destAdapter.getItem(position).code);
    }
}
