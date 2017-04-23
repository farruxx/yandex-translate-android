package com.farruxx.yandextranslator.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.farruxx.yandextranslator.R;
import com.farruxx.yandextranslator.adapter.HistoryAdapter;
import com.farruxx.yandextranslator.data.HelperFactory;

import java.sql.SQLException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Farruxx on 23.04.2017.
 */
public class HistoryFragment extends android.support.v4.app.Fragment {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        toolbar.setTitle(R.string.history);
        adapter = new HistoryAdapter(getActivity().getLayoutInflater());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible){
            Observable.just(null)
                    .subscribeOn(Schedulers.io())
                    .map(o -> {
                        try {
                            return HelperFactory.getHelper().getTranslateDao().getAllTranslations();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(adapter::setTranslateList, error -> {
                        Log.e("HISTORY FRAGMENT", error.getMessage());
                    });
        }
    }
}
