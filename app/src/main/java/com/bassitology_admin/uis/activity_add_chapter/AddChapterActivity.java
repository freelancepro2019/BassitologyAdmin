package com.bassitology_admin.uis.activity_add_chapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.bassitology_admin.R;
import com.bassitology_admin.adapter.SpinnerChaptersAdapter;
import com.bassitology_admin.databinding.ActivityAddChaptersBinding;
import com.bassitology_admin.model.ChapterModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddChapterActivity extends BaseActivity {
    private ActivityAddChaptersBinding binding;
    private DatabaseReference dRef;
    private ChapterModel chapterModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_chapters);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        chapterModel = (ChapterModel) intent.getSerializableExtra("data");
    }

    private void initView() {
        binding.setLang(getLang());
        binding.setModel(getUserModel());

        binding.back.setOnClickListener(view -> {
            finish();
        });
        if (chapterModel != null) {
            binding.edtName.setText(chapterModel.getName());
            binding.setValid(true);
        }
        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = editable.toString().trim();
                if (!name.isEmpty()) {
                    binding.setValid(true);
                } else {
                    binding.setValid(false);
                }
            }
        });

        binding.btnSave.setOnClickListener(view -> {
            addChapter();
        });
    }

    private void addChapter() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        String chapter_name = binding.edtName.getText().toString().trim();
        dRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = dRef.child(Tags.table_chapters);
        String id = "";
        if (chapterModel == null) {
            id = ref.push().getKey();

        } else {
            id = chapterModel.getId();
        }
        ChapterModel model = new ChapterModel(id, getUserModel().getId(), chapter_name);
        ref.child(getUserModel().getId()).child(id)
                .setValue(model)
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
}