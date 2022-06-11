package com.bassitology_admin.uis.activity_settings;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.ActivitySettingsBinding;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.bassitology_admin.uis.activity_sign_up.SignUpActivity;
import com.bassitology_admin.uis.activity_social.SocialActivity;

public class SettingsActivity extends BaseActivity {
    private ActivitySettingsBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private int req ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        initView();
    }

    private void initView() {
        setUpToolbar(binding.toolbar,getString(R.string.profile),"",R.color.white,R.color.black);
        binding.setLang(getLang());
        binding.setModel(getUserModel());

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (req==1){
                if (result.getResultCode()==RESULT_OK){
                   binding.setModel(getUserModel());
                }
            }
        });
        binding.cardLogout.setOnClickListener(view -> {
            Intent intent = getIntent();
            intent.putExtra("logout",true);
            setResult(RESULT_OK,intent);
            finish();
        });

        binding.llSocial.setOnClickListener(view -> {
            navigateToSocialActivity();
        });

        binding.tvLanguage.setOnClickListener(view -> {
            String lang;
            if (getLang().equals("ar")){
                lang = "en";
            }else {
                lang = "ar";
            }
            Intent intent = getIntent();
            intent.putExtra("lang",lang);
            setResult(RESULT_OK,intent);
            finish();
        });

        binding.llEditProfile.setOnClickListener(view -> {
            req = 1;
            Intent intent = new Intent(this, SignUpActivity.class);
            launcher.launch(intent);
        });

        binding.imageEditProfile.setOnClickListener(view -> {
            req = 1;
            Intent intent = new Intent(this, SignUpActivity.class);
            launcher.launch(intent);
        });
    }

    private void navigateToSocialActivity() {
        Intent intent  = new Intent(this, SocialActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}