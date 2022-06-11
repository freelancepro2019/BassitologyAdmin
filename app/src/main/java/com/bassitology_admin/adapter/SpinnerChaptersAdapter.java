package com.bassitology_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.SpinnerRowBinding;
import com.bassitology_admin.model.ChapterModel;
import com.bassitology_admin.model.StageModel;

import java.util.List;

public class SpinnerChaptersAdapter extends BaseAdapter {
    private List<ChapterModel> list;
    private Context context;
    private LayoutInflater inflater;

    public SpinnerChaptersAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return list != null ? list.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SpinnerRowBinding binding = null;
        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.spinner_row, viewGroup, false);
            view = binding.getRoot();
        }
        if (binding != null) {
            binding.setTitle(list.get(i).getName());
        }

        return view;
    }

    public void updateList(List<ChapterModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
