package com.bassitology_admin.uis.activity_home;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.ActivityHomeBinding;
import com.bassitology_admin.language.Language;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.bassitology_admin.uis.activity_chapters.ChaptersActivity;
import com.bassitology_admin.uis.activity_exams.ExamsActivity;
import com.bassitology_admin.uis.activity_lessons.LessonsActivity;
import com.bassitology_admin.uis.activity_login.LoginActivity;
import com.bassitology_admin.uis.activity_settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();
    }

    private void initView() {
        binding.setLang(getLang());
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode()==RESULT_OK){
                if (result.getData()!=null){
                    if(result.getData().hasExtra("logout")){
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        clearUserModel();
                        navigateToLoginActivity();
                    }else if(result.getData().hasExtra("lang")){
                        String lang = result.getData().getStringExtra("lang");
                        refreshActivity(lang);
                    }
                }
            }
        });
        binding.cardMenu.setOnClickListener(view -> {
            navigateToSettingActivity();
        });

        binding.cardChapters.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChaptersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        binding.cardLessons.setOnClickListener(view -> {
            Intent intent = new Intent(this, LessonsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        binding.cardExams.setOnClickListener(view -> {
            Intent intent = new Intent(this, ExamsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        binding.cardSetting.setOnClickListener(view -> {
            navigateToSettingActivity();

        });
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void navigateToSettingActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launcher.launch(intent);
    }

    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 500);


    }
}