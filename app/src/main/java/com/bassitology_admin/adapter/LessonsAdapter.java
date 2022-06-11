package com.bassitology_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.LessonsRowBinding;
import com.bassitology_admin.model.LessonsModel;
import com.bassitology_admin.uis.activity_chapter_lessons.ChapterLessonsActivity;
import com.bassitology_admin.uis.activity_lessons.LessonsActivity;

import java.util.List;

public class LessonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<LessonsModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity appCompatActivity;

    public LessonsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        appCompatActivity = (AppCompatActivity) context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LessonsRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.lessons_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));



        myHolder.binding.edit.setOnClickListener(view -> {
            if (appCompatActivity instanceof LessonsActivity) {
                LessonsActivity activity = (LessonsActivity) appCompatActivity;
                activity.edit(list.get(myHolder.getAdapterPosition()));
            } else if (appCompatActivity instanceof ChapterLessonsActivity) {
                ChapterLessonsActivity activity = (ChapterLessonsActivity) appCompatActivity;
                activity.edit(list.get(myHolder.getAdapterPosition()));
            }

        });

        myHolder.binding.delete.setOnClickListener(view -> {
            if (appCompatActivity instanceof LessonsActivity) {
                LessonsActivity activity = (LessonsActivity) appCompatActivity;
                activity.delete(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());

            } else if (appCompatActivity instanceof ChapterLessonsActivity) {
                ChapterLessonsActivity activity = (ChapterLessonsActivity) appCompatActivity;
                activity.delete(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());
            }
        });

        myHolder.itemView.setOnClickListener(view -> {
            if (appCompatActivity instanceof LessonsActivity) {
                LessonsActivity activity = (LessonsActivity) appCompatActivity;
                activity.setItemData(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());

            }
        });


    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private LessonsRowBinding binding;

        public MyHolder(LessonsRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

    public void updateList(List<LessonsModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void removeItem(int adapterPosition) {
        list.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public void updateItem(int adapterPosition,LessonsModel model) {
        model.setCanDelete(true);
        list.set(adapterPosition,model);
        notifyItemChanged(adapterPosition);
    }
}
