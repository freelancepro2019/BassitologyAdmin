package com.bassitology_admin.uis.activity_subject_exam_questions;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bassitology_admin.R;
import com.bassitology_admin.adapter.QuestionAdapter;
import com.bassitology_admin.databinding.ActivityChapterExamQuestionBinding;
import com.bassitology_admin.databinding.ActivitySubjectExamQuestionsBinding;
import com.bassitology_admin.model.ExamModel;
import com.bassitology_admin.model.QuestionModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_add_exam_chapter_question.AddExamChapterQuestionsActivity;
import com.bassitology_admin.uis.activity_add_exam_subject_questions.AddExamSubjectQuestionActivity;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SubjectExamQuestionsActivity extends BaseActivity {
    private ActivitySubjectExamQuestionsBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private QuestionAdapter adapter;
    private ExamModel examModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subject_exam_questions);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        examModel = (ExamModel) intent.getSerializableExtra("data");
    }

    private void initView() {
        setUpToolbar(binding.toolbar, getString(R.string.questions), getUserModel().getSubjectModel().getName() + "-" + examModel.getTitle(), R.color.white, R.color.black);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getQuestions();
            }
        });
        adapter = new QuestionAdapter(this);
        binding.recViewLayout.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewLayout.recView.setAdapter(adapter);
        binding.recViewLayout.recView.setHasFixedSize(true);
        binding.recViewLayout.tvNoData.setText(R.string.no_items);

        binding.recViewLayout.swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.recViewLayout.swipeRefresh.setOnRefreshListener(this::getQuestions);

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddExamSubjectQuestionActivity.class);
            intent.putExtra("data", examModel);
            launcher.launch(intent);
        });
        getQuestions();
    }

    private void getQuestions() {
        binding.recViewLayout.swipeRefresh.setRefreshing(true);
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_questions)
                .child(getUserModel().getId())
                .child(examModel.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<QuestionModel> list = new ArrayList<>();
                        if (snapshot.getValue() != null) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (ds.getValue() != null) {
                                    QuestionModel model = ds.getValue(QuestionModel.class);
                                    if (model != null) {
                                        list.add(model);
                                    }

                                }
                            }
                            binding.recViewLayout.swipeRefresh.setRefreshing(false);
                            if (list.size() > 0) {
                                if (adapter != null) {
                                    adapter.updateList(list);

                                }
                                binding.recViewLayout.tvNoData.setVisibility(View.GONE);

                            } else {
                                adapter.updateList(new ArrayList<>());
                                binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);

                            }
                        } else {
                            adapter.updateList(new ArrayList<>());
                            binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);
                            binding.recViewLayout.swipeRefresh.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void edit(QuestionModel questionModel) {
        Intent intent = new Intent(this, AddExamSubjectQuestionActivity.class);
        intent.putExtra("data", examModel);
        intent.putExtra("question", questionModel);
        launcher.launch(intent);
    }

    public void delete(QuestionModel questionModel, int adapterPosition) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.deleting));
        dialog.show();


        StorageReference sRef = FirebaseStorage.getInstance().getReference();
        sRef.child(Tags.image_subject_path)
                .child(getUserModel().getId())
                .child(questionModel.getExam_id())
                .child(questionModel.getQuestion_id())
                .delete()
                .addOnSuccessListener(unused -> {
                    deleteQuestion(questionModel, adapterPosition, dialog);
                }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        });


    }

    private void deleteQuestion(QuestionModel questionModel, int adapterPosition, ProgressDialog dialog) {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_questions)
                .child(getUserModel().getId())
                .child(questionModel.getExam_id())
                .child(questionModel.getQuestion_id())
                .removeValue()
                .addOnSuccessListener(unused -> {
                    adapter.removeItem(adapterPosition);
                    if (adapter.getItemCount() == 0) {
                        binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        binding.recViewLayout.tvNoData.setVisibility(View.GONE);

                    }
                    dialog.dismiss();

                }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

}