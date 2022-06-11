package com.bassitology_admin.uis.activity_add_lessons;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bassitology_admin.R;
import com.bassitology_admin.adapter.SpinnerChaptersAdapter;
import com.bassitology_admin.databinding.ActivityAddLessonsBinding;
import com.bassitology_admin.model.AddLessonModel;
import com.bassitology_admin.model.ChapterModel;
import com.bassitology_admin.model.LessonsModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddLessonsActivity extends BaseActivity {
    private ActivityAddLessonsBinding binding;
    private SpinnerChaptersAdapter adapter;
    private AddLessonModel addLessonModel;
    private LessonsModel lessonsModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_lessons);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        lessonsModel = (LessonsModel) intent.getSerializableExtra("data");
    }

    private void initView() {
        binding.setLang(getLang());
        binding.setModel(getUserModel());
        addLessonModel = new AddLessonModel();

        if (lessonsModel != null) {
            addLessonModel.setChapter_id(lessonsModel.getChapter_id());
            addLessonModel.setLesson_name(lessonsModel.getName());
        }

        binding.setAddLesson(addLessonModel);
        adapter = new SpinnerChaptersAdapter(this);
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    addLessonModel.setChapter_id("");
                } else {
                    ChapterModel chapterModel = (ChapterModel) adapterView.getSelectedItem();
                    addLessonModel.setChapter_id(chapterModel.getId());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.back.setOnClickListener(view -> {
            finish();
        });

        binding.btnSave.setOnClickListener(view -> {
            Common.CloseKeyBoard(this, binding.edtName);
            addLesson();
        });

        getChapters();

    }

    private void addLesson() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        String lesson_id = "";
        if (lessonsModel == null) {
            lesson_id = dRef.child(Tags.table_lessons).child(addLessonModel.getChapter_id()).push().getKey();

        } else {
            lesson_id = lessonsModel.getId();
        }

        DatabaseReference ref = dRef.child(Tags.table_lessons).child(getUserModel().getId()).child(addLessonModel.getChapter_id()).child(lesson_id);
        LessonsModel lessonsModel = new LessonsModel(lesson_id, addLessonModel.getChapter_id(), getUserModel().getId(), addLessonModel.getLesson_name());
        ref.setValue(lessonsModel)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("err", e.getMessage());
        });
    }
    private void getChapters() {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_chapters)
                .child(getUserModel().getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChapterModel> list = new ArrayList<>();
                list.add(new ChapterModel("0", "0", getString(R.string.ch_chapter)));
                if (snapshot.getValue() != null) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ChapterModel model = ds.getValue(ChapterModel.class);
                        list.add(model);
                    }


                }

                adapter.updateList(list);

                if (lessonsModel != null) {
                    int pos = getChapterPos(list);
                    binding.spinner.setSelection(pos);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private int getChapterPos(List<ChapterModel> list) {
        for (int index = 0; index < list.size(); index++) {
            if (list.get(index).getId().equals(lessonsModel.getChapter_id())) {
                return index;
            }
        }
        return 0;
    }
}