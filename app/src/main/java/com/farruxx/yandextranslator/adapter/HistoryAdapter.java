package com.farruxx.yandextranslator.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.farruxx.yandextranslator.R;
import com.farruxx.yandextranslator.model.Translate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Farruxx on 23.04.2017.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    List<Translate> translateList;
    LayoutInflater inflater;

    public HistoryAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.translate_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Translate translate = translateList.get(position);
        holder.origin.setText(translate.origin);
        holder.translate.setText(translate.dest);
        if(translate.originLanguage != null && translate.destLanguage != null) {
            holder.direction.setText(translate.originLanguage.toUpperCase()
                    + " - "
                    + translate.destLanguage.toUpperCase());
        }else {
            holder.direction.setText("");
        }
        holder.favorite.setChecked(translate.favorite);
    }

    @Override
    public int getItemCount() {
        return translateList != null ? translateList.size() : 0;
    }

    public void setTranslateList(List<Translate> translateList) {
        this.translateList = translateList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.origin_tv)
        TextView origin;
        @BindView(R.id.translate_tv)
        TextView translate;

        @BindView(R.id.direction_tv)
        TextView direction;

        @BindView(R.id.favorite)
        CheckBox favorite;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
