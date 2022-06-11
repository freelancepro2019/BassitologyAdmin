package com.bassitology_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.ChapterRowBinding;
import com.bassitology_admin.model.ChapterModel;
import com.bassitology_admin.uis.activity_chapters.ChaptersActivity;

import java.util.List;

public class ChaptersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChapterModel> list;
    private Context context;
    private LayoutInflater inflater;
    private ChaptersActivity activity;

    public ChaptersAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (ChaptersActivity) context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ChapterRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.chapter_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setTitle(list.get(position).getName());


        myHolder.binding.edit.setOnClickListener(view -> {
            activity.edit(list.get(myHolder.getAdapterPosition()));

        });

        myHolder.binding.delete.setOnClickListener(view -> {
            activity.delete(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());
        });

        myHolder.itemView.setOnClickListener(view -> {
            activity.navigateToChapterLessonsActivity(list.get(myHolder.getAdapterPosition()));
        });


    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }



    public static class MyHolder extends RecyclerView.ViewHolder {
        private ChapterRowBinding binding;

        public MyHolder(ChapterRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

    public void updateList(List<ChapterModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void removeItem(int adapterPosition) {
        list.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }
}
