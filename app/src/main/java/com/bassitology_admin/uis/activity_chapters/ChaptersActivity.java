package com.bassitology_admin.uis.activity_chapters;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.bassitology_admin.databinding.ActivityChaptersBinding;
import com.bassitology_admin.model.ChapterModel;
import com.bassitology_admin.model.ExamModel;
import com.bassitology_admin.model.LessonsModel;
import com.bassitology_admin.model.UserModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_add_chapter.AddChapterActivity;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.bassitology_admin.uis.activity_chapter_lessons.ChapterLessonsActivity;
import com.bassitology_admin.uis.activity_lessons.LessonsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChaptersActivity extends BaseActivity {
    private ActivityChaptersBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private ChaptersAdapter adapter;
    private boolean forReturn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chapters);
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
        setUpToolbar(binding.toolbar, getString(R.string.chapters), getUserModel().getSubjectModel().getName(), R.color.white, R.color.black);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getChapters();
            }
        });
        adapter = new ChaptersAdapter(this);
        binding.recViewLayout.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewLayout.recView.setAdapter(adapter);
        binding.recViewLayout.recView.setHasFixedSize(true);
        binding.recViewLayout.tvNoData.setText(R.string.no_items);
        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddChapterActivity.class);
            launcher.launch(intent);
        });

        binding.recViewLayout.swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.recViewLayout.swipeRefresh.setOnRefreshListener(this::getChapters);
        getChapters();
    }

    private void getChapters() {
        binding.recViewLayout.swipeRefresh.setRefreshing(true);
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_chapters).child(getUserModel().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.recViewLayout.swipeRefresh.setRefreshing(false);
                List<ChapterModel> list = new ArrayList<>();
                if (snapshot.getValue() != null) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ChapterModel model = ds.getValue(ChapterModel.class);
                        list.add(model);
                    }

                    if (list.size() > 0) {
                        binding.recViewLayout.tvNoData.setVisibility(View.GONE);
                        adapter.updateList(list);
                        binding.recViewLayout.recView.scheduleLayoutAnimation();

                    } else {
                        binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);

                    }

                } else {
                    binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delete(ChapterModel chapterModel, int adapterPosition) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.deleting));
        dialog.show();

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = dRef.child(Tags.table_lessons)
                .child(getUserModel().getId())
                .child(chapterModel.getId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<LessonsModel> lessons = new ArrayList<>();
                if (snapshot.getValue()!=null){
                    for (DataSnapshot ds:snapshot.getChildren()){
                        if (ds.getValue()!=null){
                            LessonsModel lessonsModel = ds.getValue(LessonsModel.class);
                            lessons.add(lessonsModel);
                        }
                    }
                    if (lessons.size()>0){
                        dialog.dismiss();
                        Toast.makeText(ChaptersActivity.this, R.string.plz_delete_lessons_first, Toast.LENGTH_SHORT).show();
                    }else {
                        deleteChapter(chapterModel,adapterPosition,dialog);
                    }
                }else {
                    deleteChapter(chapterModel,adapterPosition,dialog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void deleteChapter(ChapterModel chapterModel,int adapterPosition,ProgressDialog dialog){
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_chapters)
                .child(getUserModel().getId())
                .child(chapterModel.getId())
                .removeValue()
                .addOnSuccessListener(unused -> {
                    adapter.removeItem(adapterPosition);
                    if (adapter.getItemCount()==0){
                        binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);
                    }
                    dialog.dismiss();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("err", e.getMessage());
        });
    }

    public void edit(ChapterModel chapterModel) {
        Intent intent = new Intent(this, AddChapterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("data", chapterModel);
        launcher.launch(intent);
    }

    public void navigateToChapterLessonsActivity(ChapterModel chapterModel) {
        if (forReturn) {
            Intent intent = getIntent();
            intent.putExtra("chapter", chapterModel);
            setResult(RESULT_OK, intent);
            finish();
        }else {
            Intent intent = new Intent(this, ChapterLessonsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("data", chapterModel);
            startActivity(intent);
        }

    }
}