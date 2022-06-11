package com.bassitology_admin.uis.activity_social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.bassitology_admin.R;
import com.bassitology_admin.databinding.ActivitySocialBinding;
import com.bassitology_admin.model.AddSocialModel;
import com.bassitology_admin.model.UserModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SocialActivity extends BaseActivity {
    private ActivitySocialBinding binding;
    private AddSocialModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_social);
        initView();
    }

    private void initView() {
        setUpToolbar(binding.toolbar, getString(R.string.social_media), "", R.color.white, R.color.black);
        model = new AddSocialModel();
        model.setWhatsAppNumber(getUserModel().getWhatsapp());
        model.setFacebook(getUserModel().getFacebook());
        model.setInstagram(getUserModel().getInstagram());
        model.setTwitter(getUserModel().getTwitter());
        model.setYoutube(getUserModel().getYoutube());
        binding.setModel(model);
        binding.btnSave.setOnClickListener(view -> {
            UserModel userModel = getUserModel();
            userModel.setWhatsapp(model.getPhone_code() + model.getWhatsAppNumber());
            userModel.setFacebook(model.getFacebook());
            userModel.setTwitter(model.getTwitter());
            userModel.setInstagram(model.getInstagram());
            userModel.setYoutube(model.getYoutube());
            updateUserData(userModel);
        });
    }

    private void updateUserData(UserModel userModel) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_teachers).child(userModel.getId())
                .setValue(userModel)
                .addOnSuccessListener(unused -> {
                    setUserModel(userModel);
                    Toast.makeText(this, R.string.suc, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}