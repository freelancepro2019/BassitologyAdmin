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
import com.bassitology_admin.model.AnswerModel;
import com.bassitology_admin.uis.activity_add_exam_chapter_question.AddExamChapterQuestionsActivity;
import com.bassitology_admin.uis.activity_add_exam_lesson_questions.AddExamLessonQuestionsActivity;
import com.bassitology_admin.uis.activity_add_exam_subject_questions.AddExamSubjectQuestionActivity;

import java.util.List;

public class AddAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AnswerModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity appCompatActivity;

    public AddAnswerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        appCompatActivity = (AppCompatActivity) context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        AddAnswerRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.add_answer_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.itemView.setOnClickListener(view -> {
            if (appCompatActivity instanceof AddExamLessonQuestionsActivity){
                AddExamLessonQuestionsActivity activity = (AddExamLessonQuestionsActivity) appCompatActivity;
                activity.updateAnswer(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            }else if (appCompatActivity instanceof AddExamChapterQuestionsActivity){
                AddExamChapterQuestionsActivity activity = (AddExamChapterQuestionsActivity) appCompatActivity;
                activity.updateAnswer(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            }else if (appCompatActivity instanceof AddExamSubjectQuestionActivity){
                AddExamSubjectQuestionActivity activity = (AddExamSubjectQuestionActivity) appCompatActivity;
                activity.updateAnswer(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            }
        });

        myHolder.binding.delete.setOnClickListener(view -> {
            if (appCompatActivity instanceof AddExamLessonQuestionsActivity){
                AddExamLessonQuestionsActivity activity = (AddExamLessonQuestionsActivity) appCompatActivity;
                activity.deleteAnswer(myHolder.getAdapterPosition());
            }else if (appCompatActivity instanceof AddExamChapterQuestionsActivity){
                AddExamChapterQuestionsActivity activity = (AddExamChapterQuestionsActivity) appCompatActivity;
                activity.deleteAnswer(myHolder.getAdapterPosition());
            }else if (appCompatActivity instanceof AddExamSubjectQuestionActivity){
                AddExamSubjectQuestionActivity activity = (AddExamSubjectQuestionActivity) appCompatActivity;
                activity.deleteAnswer(myHolder.getAdapterPosition());
            }
        });




    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private AddAnswerRowBinding binding;

        public MyHolder(AddAnswerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

    public void updateList(List<AnswerModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

}
