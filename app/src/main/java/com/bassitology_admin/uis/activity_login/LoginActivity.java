package com.bassitology_admin.uis.activity_login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.bassitology_admin.databinding.ActivityLoginBinding;
import com.bassitology_admin.model.LoginModel;
import com.bassitology_admin.model.UserModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.bassitology_admin.uis.activity_home.HomeActivity;
import com.bassitology_admin.uis.activity_sign_up.SignUpActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private LoginModel model;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private DatabaseReference dRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
    }


    private void initView() {
        binding.setLang(getLang());
        model = new LoginModel();
        binding.setModel(model);

        binding.tvSignUp.setOnClickListener(view -> {
            navigateToSignUpActivity();
        });
        binding.btnLogin.setOnClickListener(v -> {
            login();
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                navigateToHomeActivity();
            }
        });
    }

    private void navigateToSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launcher.launch(intent);
    }

    private void login() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.loggingin));
        dialog.show();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(model.getEmail(), model.getPassword())
                .addOnSuccessListener(authResult -> {
                    dialog.dismiss();
                    if (authResult.getUser() != null) {
                        getUserById(dialog, authResult.getUser().getUid());

                    } else {
                        Toast.makeText(LoginActivity.this, R.string.inc_email_pass, Toast.LENGTH_LONG).show();
                    }

                }).addOnFailureListener(e -> {
            if (e.getMessage() != null && e.getMessage().contains("There is no user record corresponding to this identifier")) {
                Toast.makeText(LoginActivity.this, R.string.inc_email_pass, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
            dialog.dismiss();
            Log.e("error", e.getMessage());
        });
    }

    private void getUserById(ProgressDialog dialog, String uid) {
        dRef = FirebaseDatabase.getInstance().getReference();
        Query query = dRef.child(Tags.table_teachers)
                .orderByChild("id")
                .equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.dismiss();
                if (snapshot.getValue() != null) {
                    try {
                        UserModel userModel = snapshot.child(uid).getValue(UserModel.class);
                        if (userModel != null) {
                            setUserModel(userModel);
                            navigateToHomeActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.failed, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }

                } else {
                    Toast.makeText(LoginActivity.this, R.string.user_not_exist, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }


}