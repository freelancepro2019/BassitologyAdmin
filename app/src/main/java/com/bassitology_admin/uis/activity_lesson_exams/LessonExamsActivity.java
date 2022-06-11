package com.bassitology_admin.uis.activity_lesson_exams;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bassitology_admin.R;
import com.bassitology_admin.adapter.ExamAdapter;
import com.bassitology_admin.databinding.ActivityLessonExamsBinding;
import com.bassitology_admin.databinding.DialogAddExamNameBinding;
import com.bassitology_admin.databinding.DialogAddResultBinding;
import com.bassitology_admin.model.AnswerModel;
import com.bassitology_admin.model.ChapterModel;
import com.bassitology_admin.model.ExamModel;
import com.bassitology_admin.model.LessonsModel;
import com.bassitology_admin.model.QuestionModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_add_exam_lesson_questions.AddExamLessonQuestionsActivity;
import com.bassitology_admin.uis.activity_add_lessons.AddLessonsActivity;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.bassitology_admin.uis.activity_lesson_exam_questions.LessonExamQuestionsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class LessonExamsActivity extends BaseActivity {
    private ActivityLessonExamsBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private ExamAdapter adapter;
    private LessonsModel lessonsModel;
    private int req;
    private ExamModel selectedExam;
    private int selectedPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lesson_exams);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        lessonsModel = (LessonsModel) intent.getSerializableExtra("lesson");
    }

    private void initView() {
        setUpToolbar(binding.toolbar, getString(R.string.exams) + "(" + lessonsModel.getName() + ")", getUserModel().getSubjectModel().getName(), R.color.white, R.color.black);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (req == 1) {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        adapter.updateItem(selectedPos, selectedExam);
                    } else {
                        getExams();
                    }

                }
            }

        });
        adapter = new ExamAdapter(this);
        binding.recViewLayout.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewLayout.recView.setAdapter(adapter);
        binding.recViewLayout.recView.setHasFixedSize(true);
        binding.recViewLayout.tvNoData.setText(R.string.no_items);

        binding.fab.setOnClickListener(view -> {
            showAddExamDialog(null);
        });
        binding.recViewLayout.swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.recViewLayout.swipeRefresh.setOnRefreshListener(this::getExams);

        getExams();
    }

    private void showAddExamDialog(ExamModel examModel) {

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        DialogAddExamNameBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_add_exam_name, null, false);
        dialog.setView(dialogBinding.getRoot());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        if (examModel != null) {
            dialogBinding.edtName.setText(examModel.getTitle());
        }
        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogBinding.tvAdd.setOnClickListener(view -> {
            String title = dialogBinding.edtName.getText().toString().trim();
            if (!title.isEmpty()) {
                dialog.dismiss();
                addExam(title, examModel);
            }
        });
        dialog.show();
    }

    private void addExam(String title, ExamModel examModel) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.deleting));
        dialog.show();

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = dRef.child(Tags.table_exams)
                .child(Tags.table_exams_lessons)
                .child(getUserModel().getId())
                .child(lessonsModel.getChapter_id())
                .child(lessonsModel.getId());
        String exam_id = "";
        ExamModel model = null;
        if (examModel != null) {
            exam_id = examModel.getId();
            model = examModel;
            model.setTitle(title);
        } else {
            exam_id = ref.push().getKey();
            model = new ExamModel(exam_id, getUserModel().getId(), getUserModel().getSubjectModel().getId(), lessonsModel.getChapter_id(), lessonsModel.getId(), title);

        }

        ref.child(exam_id)
                .setValue(model)
                .addOnSuccessListener(unused -> {
                    getExams();
                    dialog.dismiss();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(LessonExamsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    private void getExams() {
        binding.recViewLayout.swipeRefresh.setRefreshing(true);
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_exams)
                .child(Tags.table_exams_lessons)
                .child(getUserModel().getId())
                .child(lessonsModel.getChapter_id())
                .child(lessonsModel.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ExamModel> list = new ArrayList<>();
                        if (snapshot.getValue() != null) {

                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (ds.getValue() != null) {
                                    ExamModel examModel = ds.getValue(ExamModel.class);
                                    if (examModel != null) {
                                        list.add(examModel);
                                    }
                                }

                            }

                            if (list.size() > 0) {
                                binding.recViewLayout.swipeRefresh.setRefreshing(false);
                                binding.recViewLayout.tvNoData.setVisibility(View.GONE);

                                if (adapter != null) {
                                    adapter.updateList(list);
                                }
                            } else {
                                binding.recViewLayout.swipeRefresh.setRefreshing(false);
                                adapter.updateList(new ArrayList<>());
                                binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);

                            }

                        } else {
                            binding.recViewLayout.swipeRefresh.setRefreshing(false);
                            adapter.updateList(new ArrayList<>());
                            binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void edit(ExamModel model) {
        showAddExamDialog(model);
    }

    public void delete(ExamModel examModel, int adapterPosition) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.deleting));
        dialog.show();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = dRef.child(Tags.table_questions)
                .child(getUserModel().getId())
                .child(lessonsModel.getChapter_id())
                .child(lessonsModel.getId())
                .child(examModel.getId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<QuestionModel> questions = new ArrayList<>();
                if (snapshot.getValue() != null) {
                    for (DataSnapshot ds :snapshot.getChildren()){
                        if (ds.getValue()!=null){
                            QuestionModel questionModel = ds.getValue(QuestionModel.class);
                            if (questionModel!=null){
                                questions.add(questionModel);
                            }
                        }
                    }
                    if (questions.size()>0){
                        dialog.dismiss();
                        Toast.makeText(LessonExamsActivity.this, R.string.ples_delete_ques_first, Toast.LENGTH_SHORT).show();
                    }else {
                        deleteExam(examModel,adapterPosition,dialog);

                    }
                }else {
                    deleteExam(examModel,adapterPosition,dialog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



    private void deleteExam(ExamModel examModel, int adapterPosition, ProgressDialog dialog) {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();

        dRef.child(Tags.table_exams)
                .child(Tags.table_exams_lessons)
                .child(getUserModel().getId())
                .child(examModel.getChapter_id())
                .child(examModel.getLesson_id())
                .child(examModel.getId())
                .removeValue()
                .addOnSuccessListener(unused -> {
                    adapter.removeItem(adapterPosition);
                    if (adapter.getItemCount() == 0) {
                        binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);
                    }
                    dialog.dismiss();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("err", e.getMessage());
        });
    }

    public void setItemData(ExamModel examModel, int adapterPosition) {
        this.selectedExam = examModel;
        this.selectedPos = adapterPosition;
        req = 1;
        Intent intent = new Intent(this, LessonExamQuestionsActivity.class);
        intent.putExtra("data", examModel);
        launcher.launch(intent);
    }
}
