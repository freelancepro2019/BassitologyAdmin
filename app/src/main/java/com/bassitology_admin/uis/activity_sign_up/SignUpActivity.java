package com.bassitology_admin.uis.activity_sign_up;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bassitology_admin.R;
import com.bassitology_admin.adapter.SpinnerClassAdapter;
import com.bassitology_admin.adapter.SpinnerStageAdapter;
import com.bassitology_admin.adapter.SpinnerSubjectAdapter;
import com.bassitology_admin.databinding.ActivitySignUpBinding;
import com.bassitology_admin.model.ClassModel;
import com.bassitology_admin.model.SignUpModel;
import com.bassitology_admin.model.StageModel;
import com.bassitology_admin.model.SubjectModel;
import com.bassitology_admin.model.UserModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SignUpActivity extends BaseActivity {
    private ActivitySignUpBinding binding;
    private SignUpModel model;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        initView();
    }


    private void initView() {
        binding.setLang(getLang());
        model = new SignUpModel();


        binding.tvLogin.setOnClickListener(view -> {
            navigateToSignUpLogin();
        });
        binding.btnSignUp.setOnClickListener(v -> {
            if (getUserModel() == null) {
                createAccount();
            } else {
                updateAccount();
            }

        });

        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().startsWith("0")) {
                    binding.edtPhone.setText("");
                }
            }
        });

        SpinnerSubjectAdapter spinnerSubjectAdapter = new SpinnerSubjectAdapter(this);
        binding.spinnerSubject.setAdapter(spinnerSubjectAdapter);

        SpinnerClassAdapter spinnerClassAdapter = new SpinnerClassAdapter(this);
        binding.spinnerClass.setAdapter(spinnerClassAdapter);


        SpinnerStageAdapter spinnerStageAdapter = new SpinnerStageAdapter(this);
        spinnerStageAdapter.updateList(Common.getStages(this));
        binding.spinnerStage.setAdapter(spinnerStageAdapter);

        binding.spinnerStage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                StageModel stageModel = (StageModel) adapterView.getSelectedItem();
                if (i == 0) {
                    binding.spinnerClass.setSelection(0);
                    binding.spinnerSubject.setSelection(0);
                    model.setStageModel(null);
                } else {
                    model.setStageModel(stageModel);
                    spinnerClassAdapter.updateList(stageModel.getClasses());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ClassModel classModel = (ClassModel) adapterView.getSelectedItem();
                if (i == 0) {
                    binding.spinnerSubject.setSelection(0);
                    model.setClassModel(null);
                } else {
                    model.setClassModel(classModel);
                    spinnerSubjectAdapter.updateList(classModel.getSubjects());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SubjectModel subjectModel = (SubjectModel) adapterView.getSelectedItem();
                if (i == 0) {
                    model.setSubjectModel(null);
                } else {
                    model.setSubjectModel(subjectModel);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (getUserModel() != null) {
            binding.tvTitle.setText(R.string.edit_profile);
            binding.tvLogin.setVisibility(View.GONE);
            binding.btnSignUp.setText(R.string.edit_profile);
            model.setFirst_name(getUserModel().getFirst_name());
            model.setLast_name(getUserModel().getLast_name());
            model.setFull_name(getUserModel().getFull_name());
            model.setPhone(getUserModel().getPhone());
            model.setEmail(getUserModel().getEmail());
            model.setPassword(getUserModel().getPassword());
            model.setSubjectModel(getUserModel().getSubjectModel());
            model.setClassModel(getUserModel().getClassModel());
            model.setStageModel(getUserModel().getStageModel());
            model.setValid(true);
            int stagePos = getStagePos();
            if (stagePos != 0) {
                binding.spinnerStage.setSelection(stagePos);
                spinnerClassAdapter.updateList(Common.getStages(this).get(stagePos).getClasses());
                int classPos = getClassPos(Common.getStages(this).get(stagePos).getClasses());
                if (classPos != 0) {
                    binding.spinnerClass.setSelection(classPos);
                    spinnerSubjectAdapter.updateList(Common.getStages(this).get(stagePos).getClasses().get(classPos).getSubjects());
                    int subjectPos = getSubjectPos(Common.getStages(this).get(stagePos).getClasses().get(classPos).getSubjects());
                    if(subjectPos!=0){
                        binding.spinnerSubject.setSelection(subjectPos);
                    }
                }
            }


        }
        binding.setModel(model);

    }

    private int getStagePos() {
        int pos = 0;
        for (int index = 0; index < Common.getStages(this).size(); index++) {
            StageModel stageModel = Common.getStages(this).get(index);
            if (stageModel.getId().equals(getUserModel().getStageModel().getId())) {
                return index;
            }
        }
        return pos;
    }

    private int getClassPos(List<ClassModel> classes) {
        int pos = 0;
        for (int index = 0; index < classes.size(); index++) {
            ClassModel classModel = classes.get(index);
            if (classModel.getId().equals(getUserModel().getClassModel().getId())) {
                return index;
            }
        }
        return pos;
    }

    private int getSubjectPos(List<SubjectModel> subjects) {
        int pos = 0;
        for (int index = 0; index < subjects.size(); index++) {
            SubjectModel subjectModel = subjects.get(index);
            if (subjectModel.getId().equals(getUserModel().getSubjectModel().getId())) {
                return index;
            }
        }
        return pos;
    }

    private void navigateToSignUpLogin() {
        finish();
    }

    private void createAccount() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.loggingin));
        dialog.show();
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(model.getEmail(), model.getPassword())
                .addOnSuccessListener(authResult -> {
                    dialog.dismiss();
                    if (authResult.getUser() != null) {
                        signUp(dialog, authResult.getUser().getUid());

                    } else {
                        Toast.makeText(SignUpActivity.this, R.string.inc_email_pass, Toast.LENGTH_LONG).show();
                    }

                }).addOnFailureListener(e -> {
            if (e.getMessage() != null && e.getMessage().contains("There is no user record corresponding to this identifier")) {
                Toast.makeText(SignUpActivity.this, R.string.inc_email_pass, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
            dialog.dismiss();
            Log.e("error", e.getMessage());
        });
    }

    private void updateAccount() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.edit_profile));
        dialog.show();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(getUserModel().getEmail(), getUserModel().getPassword());
            mAuth.getCurrentUser().reauthenticate(credential)
                    .addOnSuccessListener(unused -> {
                        mAuth.getCurrentUser().updateEmail(model.getEmail())
                                .addOnSuccessListener(unused1 -> {
                                    mAuth.getCurrentUser().updatePassword(model.getPassword())
                                            .addOnSuccessListener(unused2 -> {
                                                signUp(dialog, getUserModel().getId());
                                            }).addOnFailureListener(e -> {
                                        dialog.dismiss();

                                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    });
                                }).addOnFailureListener(e -> {
                            dialog.dismiss();

                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        });
                    }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void signUp(ProgressDialog dialog, String uid) {
        UserModel userModel = new UserModel(uid, model.getFirst_name(), model.getLast_name(), model.getFull_name(), model.getPhone_code(), model.getPhone(), model.getEmail(), model.getPassword(), model.getStageModel(), model.getClassModel(), model.getSubjectModel());
        if (getUserModel()!=null){
            userModel.setWhatsapp(getUserModel().getWhatsapp());
            userModel.setFacebook(getUserModel().getFacebook());
            userModel.setTwitter(getUserModel().getTwitter());
            userModel.setInstagram(getUserModel().getInstagram());
            userModel.setYoutube(getUserModel().getYoutube());
        }

        dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_teachers)
                .child(uid)
                .setValue(userModel)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    setUserModel(userModel);
                    setResult(RESULT_OK);
                    finish();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        });

    }


}