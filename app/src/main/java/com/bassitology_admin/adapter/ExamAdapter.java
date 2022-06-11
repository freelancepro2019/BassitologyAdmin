package com.bassitology_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.ExamRowBinding;
import com.bassitology_admin.model.ExamModel;
import com.bassitology_admin.uis.activity_chapter_exams.ChapterExamsActivity;
import com.bassitology_admin.uis.activity_lesson_exams.LessonExamsActivity;
import com.bassitology_admin.uis.activity_subject_exams.SubjectsExamsActivity;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ExamModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity appCompatActivity;

    public ExamAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        appCompatActivity = (AppCompatActivity) context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ExamRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.exam_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setTitle(list.get(position).getTitle());
        myHolder.binding.setCanDelete(list.get(position).isCanDelete());

        myHolder.binding.edit.setOnClickListener(view -> {
            if (appCompatActivity instanceof LessonExamsActivity) {
                LessonExamsActivity activity = (LessonExamsActivity) appCompatActivity;
                activity.edit(list.get(myHolder.getAdapterPosition()));

            } else if (appCompatActivity instanceof ChapterExamsActivity) {
                ChapterExamsActivity activity = (ChapterExamsActivity) appCompatActivity;
                activity.edit(list.get(myHolder.getAdapterPosition()));

            }else if (appCompatActivity instanceof SubjectsExamsActivity) {
                SubjectsExamsActivity activity = (SubjectsExamsActivity) appCompatActivity;
                activity.edit(list.get(myHolder.getAdapterPosition()));

            }

        });

        myHolder.binding.delete.setOnClickListener(view -> {
            if (appCompatActivity instanceof LessonExamsActivity) {
                LessonExamsActivity activity = (LessonExamsActivity) appCompatActivity;
                activity.delete(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());

            } else if (appCompatActivity instanceof ChapterExamsActivity) {
                ChapterExamsActivity activity = (ChapterExamsActivity) appCompatActivity;
                activity.delete(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());

            }else if (appCompatActivity instanceof SubjectsExamsActivity) {
                SubjectsExamsActivity activity = (SubjectsExamsActivity) appCompatActivity;
                activity.delete(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());

            }
        });

        myHolder.itemView.setOnClickListener(view -> {
            if (appCompatActivity instanceof LessonExamsActivity) {
                LessonExamsActivity activity = (LessonExamsActivity) appCompatActivity;
                activity.setItemData(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());

            } else if (appCompatActivity instanceof ChapterExamsActivity) {
                ChapterExamsActivity activity = (ChapterExamsActivity) appCompatActivity;
                activity.setItemData(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());

            }else if (appCompatActivity instanceof SubjectsExamsActivity) {
                SubjectsExamsActivity activity = (SubjectsExamsActivity) appCompatActivity;
                activity.setItemData(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());

            }
        });


    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public static class MyHolder extends RecyclerView.ViewHolder {
        private ExamRowBinding binding;

        public MyHolder(ExamRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

    public void updateList(List<ExamModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void removeItem(int adapterPosition) {
        list.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public void updateItem(int adapterPosition, ExamModel model) {
        model.setCanDelete(true);
        list.set(adapterPosition, model);
        notifyItemChanged(adapterPosition);
    }
}
