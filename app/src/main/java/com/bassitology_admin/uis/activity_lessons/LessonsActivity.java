package com.bassitology_admin.uis.activity_lessons;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bassitology_admin.R;
import com.bassitology_admin.adapter.ChaptersAdapter;
import com.bassitology_admin.adapter.LessonsAdapter;
import com.bassitology_admin.databinding.ActivityChaptersBinding;
import com.bassitology_admin.databinding.ActivityLessonsBinding;
import com.bassitology_admin.model.ChapterModel;
import com.bassitology_admin.model.ExamModel;
import com.bassitology_admin.model.LessonsModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_add_chapter.AddChapterActivity;
import com.bassitology_admin.uis.activity_add_lessons.AddLessonsActivity;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LessonsActivity extends BaseActivity {
    private ActivityLessonsBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private LessonsAdapter adapter;
    private boolean forReturn = false;
    private LessonsModel selectedLesson;
    private int selectedPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lessons);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("action")) {
            forReturn = true;
        }
    }

    private void initView() {
        setUpToolbar(binding.toolbar, getString(R.string.lessons), getUserModel().getSubjectModel().getName(), R.color.white, R.color.black);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData()!=null){
                    adapter.updateItem(selectedPos,selectedLesson);
                }else {
                    getLessons();
                }

            }
        });
        adapter = new LessonsAdapter(this);
        binding.recViewLayout.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewLayout.recView.setAdapter(adapter);
        binding.recViewLayout.recView.setHasFixedSize(true);
        binding.recViewLayout.tvNoData.setText(R.string.no_items);

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddLessonsActivity.class);
            launcher.launch(intent);
        });
        binding.recViewLayout.swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.recViewLayout.swipeRefresh.setOnRefreshListener(this::getLessons);

        getLessons();
    }

    private void getLessons() {
        binding.recViewLayout.swipeRefresh.setRefreshing(true);
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_lessons)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<LessonsModel> list = new ArrayList<>();
                        if (snapshot.getValue() != null) {

                            for (DataSnapshot ds : snapshot.getChildren()) {
                                for (DataSnapshot d : ds.getChildren()) {
                                    if (d.getValue() != null) {
                                        for (DataSnapshot data : d.getChildren()) {
                                            if (data != null) {
                                                LessonsModel model = data.getValue(LessonsModel.class);
                                                if (model != null && model.getUser_id().equals(getUserModel().getId())) {
                                                    list.add(model);
                                                }
                                            }
                                        }

                                    }

                                }

                            }

                            if (list.size() > 0) {
                                getChapterData(list);

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

    private void getChapterData(List<LessonsModel> list) {
        List<LessonsModel> data = new ArrayList<>();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_chapters)
                .child(getUserModel().getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            for (LessonsModel lessonsModel : list) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    ChapterModel model = ds.getValue(ChapterModel.class);
                                    if (model != null && lessonsModel.getChapter_id().equals(model.getId())) {
                                        lessonsModel.setChapterModel(model);
                                        data.add(lessonsModel);

                                    }
                                }

                            }


                        } else {
                            data.clear();
                            data.addAll(list);
                        }
                        binding.recViewLayout.swipeRefresh.setRefreshing(false);
                        binding.recViewLayout.tvNoData.setVisibility(View.GONE);
                        adapter.updateList(data);
                        binding.recViewLayout.recView.scheduleLayoutAnimation();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void edit(LessonsModel lessonsModel) {
        Intent intent = new Intent(this, AddLessonsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("data", lessonsModel);
        launcher.launch(intent);
    }

    public void delete(LessonsModel lessonsModel, int adapterPosition) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.deleting));
        dialog.show();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = dRef.child(Tags.table_exams)
                .child(Tags.table_exams_lessons)
                .child(getUserModel().getId())
                .child(lessonsModel.getChapter_id())
                .child(lessonsModel.getId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ExamModel> exams = new ArrayList<>();
                if (snapshot.getValue()!=null){
                    for (DataSnapshot ds:snapshot.getChildren()){
                        if (ds.getValue()!=null){
                            ExamModel examModel = ds.getValue(ExamModel.class);
                            exams.add(examModel);
                        }
                    }
                    if (exams.size()>0){
                        dialog.dismiss();
                        Toast.makeText(LessonsActivity.this, R.string.plz_delete_exams, Toast.LENGTH_SHORT).show();
                    }else {
                        deleteLessons(lessonsModel,adapterPosition,dialog);
                    }
                }else {
                    deleteLessons(lessonsModel,adapterPosition,dialog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void deleteLessons(LessonsModel lessonsModel, int adapterPosition ,ProgressDialog dialog){
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_lessons)
                .child(getUserModel().getId())
                .child(lessonsModel.getChapter_id())
                .child(lessonsModel.getId())
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
    public void setItemData(LessonsModel lessonsModel, int adapterPosition) {
        selectedLesson = lessonsModel;
        selectedPos = adapterPosition;

        if (forReturn) {
            Intent intent = getIntent();
            intent.putExtra("lesson", lessonsModel);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}