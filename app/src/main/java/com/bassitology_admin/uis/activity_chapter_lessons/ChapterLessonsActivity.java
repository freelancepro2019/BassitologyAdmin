package com.bassitology_admin.uis.activity_chapter_lessons;

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
import com.bassitology_admin.adapter.LessonsAdapter;
import com.bassitology_admin.databinding.ActivityChapterLessonsBinding;
import com.bassitology_admin.databinding.ActivityLessonsBinding;
import com.bassitology_admin.model.ChapterModel;
import com.bassitology_admin.model.LessonsModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
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

public class ChapterLessonsActivity extends BaseActivity {
    private ActivityChapterLessonsBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private LessonsAdapter adapter;
    private ChapterModel chapterModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chapter_lessons);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        chapterModel = (ChapterModel) intent.getSerializableExtra("data");
    }

    private void initView() {
        setUpToolbar(binding.toolbar, getString(R.string.lessons), getUserModel().getSubjectModel().getName()+"-"+chapterModel.getName(), R.color.white, R.color.black);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getLessons();
            }
        });
        adapter = new LessonsAdapter(this);
        binding.recViewLayout.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewLayout.recView.setAdapter(adapter);
        binding.recViewLayout.recView.setHasFixedSize(true);
        binding.recViewLayout.tvNoData.setText(R.string.no_items);

        binding.recViewLayout.swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.recViewLayout.swipeRefresh.setOnRefreshListener(this::getLessons);

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddLessonsActivity.class);
            launcher.launch(intent);
        });
        getLessons();
    }

    private void getLessons() {
        binding.recViewLayout.swipeRefresh.setRefreshing(true);
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_lessons)
                .child(getUserModel().getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.recViewLayout.swipeRefresh.setRefreshing(false);
                        List<LessonsModel> list = new ArrayList<>();
                        if (snapshot.getValue() != null) {

                            for (DataSnapshot ds : snapshot.getChildren()) {
                                for (DataSnapshot d : ds.getChildren()) {
                                    if (d.getValue() != null) {
                                        LessonsModel model = d.getValue(LessonsModel.class);
                                        if (model != null && model.getChapter_id().equals(chapterModel.getId())) {
                                            list.add(model);
                                        }
                                    }

                                }

                            }

                            if (list.size() > 0) {
                                binding.recViewLayout.tvNoData.setVisibility(View.GONE);
                                adapter.updateList(list);
                                binding.recViewLayout.recView.scheduleLayoutAnimation();

                            } else {
                                adapter.updateList(new ArrayList<>());
                                binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);

                            }

                        } else {
                            adapter.updateList(new ArrayList<>());
                            binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void edit(LessonsModel lessonsModel) {
        Intent intent = new Intent(this, AddLessonsActivity.class);
        intent.putExtra("data", lessonsModel);
        launcher.launch(intent);
    }

    public void delete(LessonsModel lessonsModel, int adapterPosition) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.deleting));
        dialog.show();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_lessons)
                .child(lessonsModel.getChapter_id())
                .child(lessonsModel.getId())
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



}