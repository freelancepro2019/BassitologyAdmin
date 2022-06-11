package com.bassitology_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.AddAnswerRowBinding;
import com.bassitology_admin.databinding.AnswerRowBinding;
import com.bassitology_admin.model.AnswerModel;
import com.bassitology_admin.uis.activity_add_exam_lesson_questions.AddExamLessonQuestionsActivity;
import com.bassitology_admin.uis.activity_lesson_exam_questions.LessonExamQuestionsActivity;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AnswerModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity appCompatActivity;

    public AnswerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        appCompatActivity = (AppCompatActivity) context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        AnswerRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.answer_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        if (appCompatActivity instanceof LessonExamQuestionsActivity){
            myHolder.binding.rb.setEnabled(false);
        }




    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private AnswerRowBinding binding;

        public MyHolder(AnswerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

    public void updateList(List<AnswerModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

}
