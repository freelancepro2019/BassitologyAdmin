package com.bassitology_admin.uis.activity_exams;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.ActivityExamsBinding;
import com.bassitology_admin.model.ChapterModel;
import com.bassitology_admin.model.LessonsModel;
import com.bassitology_admin.uis.activity_add_exam_lesson_questions.AddExamLessonQuestionsActivity;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.bassitology_admin.uis.activity_chapter_exams.ChapterExamsActivity;
import com.bassitology_admin.uis.activity_chapters.ChaptersActivity;
import com.bassitology_admin.uis.activity_lesson_exams.LessonExamsActivity;
import com.bassitology_admin.uis.activity_lessons.LessonsActivity;
import com.bassitology_admin.uis.activity_subject_exams.SubjectsExamsActivity;

public class ExamsActivity extends BaseActivity {
    private ActivityExamsBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private int req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exams);
        initView();
    }

    private void initView() {
        binding.setLang(getLang());
        setUpToolbar(binding.toolbar, getString(R.string.exams), getUserModel().getSubjectModel().getName(), R.color.white, R.color.black);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (req ==1){
                if (result.getResultCode() == RESULT_OK&&result.getData()!=null) {
                    LessonsModel lessonsModel = (LessonsModel) result.getData().getSerializableExtra("lesson");
                    Intent intent = new Intent(this, LessonExamsActivity.class);
                    intent.putExtra("lesson",lessonsModel);
                    startActivity(intent);

                }
            }else if (req ==2){
                if (result.getResultCode() == RESULT_OK&&result.getData()!=null) {
                    ChapterModel chapterModel = (ChapterModel) result.getData().getSerializableExtra("chapter");
                    Intent intent = new Intent(this, ChapterExamsActivity.class);
                    intent.putExtra("chapter",chapterModel);
                    startActivity(intent);

                }
            }

        });

        binding.cardAddExamLesson.setOnClickListener(view -> {
            req = 1;
            Intent intent = new Intent(this, LessonsActivity.class);
            intent.putExtra("action",true);
            launcher.launch(intent);
        });

        binding.cardAddExamChapter.setOnClickListener(view -> {
            req = 2;
            Intent intent = new Intent(this, ChaptersActivity.class);
            intent.putExtra("action",true);
            launcher.launch(intent);
        });
        binding.cardAddExamSubject.setOnClickListener(view -> {
            Intent intent = new Intent(this, SubjectsExamsActivity.class);
            startActivity(intent);
        });
    }
}