package com.farruxx.yandextranslator.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.farruxx.yandextranslator.model.TranslateDirection;

import java.util.List;

/**
 * Created by Farruxx on 13.04.2017.
 */
public class TranslateDirectionAdapter extends BaseAdapter implements SpinnerAdapter {
    List<TranslateDirection> items;
    Activity activity;

    public TranslateDirectionAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    @Override
    public TranslateDirection getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(getItem(position).name);
        textView.setMaxLines(1);
        return convertView;
    }

    public void setItems(List<TranslateDirection> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
