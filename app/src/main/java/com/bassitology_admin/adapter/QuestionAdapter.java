package com.bassitology_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.QuestionRowBinding;
import com.bassitology_admin.model.QuestionModel;
import com.bassitology_admin.uis.activity_lesson_exam_questions.LessonExamQuestionsActivity;
import com.bassitology_admin.uis.activity_chapter_exam_questions.ChapterExamQuestionActivity;
import com.bassitology_admin.uis.activity_subject_exam_questions.SubjectExamQuestionsActivity;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<QuestionModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity appCompatActivity;

    public QuestionAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        appCompatActivity = (AppCompatActivity) context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        QuestionRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.question_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        AnswerAdapter adapter = new AnswerAdapter(context);
        adapter.updateList(list.get(position).getAnswers());
        myHolder.binding.recView.setLayoutManager(new GridLayoutManager(context, 2));
        myHolder.binding.recView.setAdapter(adapter);
        myHolder.binding.recView.setHasFixedSize(true);

        myHolder.binding.edit.setOnClickListener(view -> {
            if (appCompatActivity instanceof LessonExamQuestionsActivity){
                LessonExamQuestionsActivity activity = (LessonExamQuestionsActivity) appCompatActivity;
                activity.edit(list.get(myHolder.getAdapterPosition()));
            }else  if (appCompatActivity instanceof ChapterExamQuestionActivity){
                ChapterExamQuestionActivity activity = (ChapterExamQuestionActivity) appCompatActivity;
                activity.edit(list.get(myHolder.getAdapterPosition()));
            }else  if (appCompatActivity instanceof SubjectExamQuestionsActivity){
                SubjectExamQuestionsActivity activity = (SubjectExamQuestionsActivity) appCompatActivity;
                activity.edit(list.get(myHolder.getAdapterPosition()));
            }
        });


        myHolder.binding.delete.setOnClickListener(view -> {
            if (appCompatActivity instanceof LessonExamQuestionsActivity){
                LessonExamQuestionsActivity activity = (LessonExamQuestionsActivity) appCompatActivity;
                activity.delete(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            }else if (appCompatActivity instanceof ChapterExamQuestionActivity){
                ChapterExamQuestionActivity activity = (ChapterExamQuestionActivity) appCompatActivity;
                activity.delete(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            }else if (appCompatActivity instanceof SubjectExamQuestionsActivity){
                SubjectExamQuestionsActivity activity = (SubjectExamQuestionsActivity) appCompatActivity;
                activity.delete(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            }
        });


    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public static class MyHolder extends RecyclerView.ViewHolder {
        private QuestionRowBinding binding;

        public MyHolder(QuestionRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }

    public void updateList(List<QuestionModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void removeItem(int adapterPosition) {
        list.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }
}
