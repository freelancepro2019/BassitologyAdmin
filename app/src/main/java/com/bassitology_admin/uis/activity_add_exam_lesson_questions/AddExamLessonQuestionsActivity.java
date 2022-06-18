package com.bassitology_admin.uis.activity_add_exam_lesson_questions;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bassitology_admin.R;
import com.bassitology_admin.adapter.AddAnswerAdapter;
import com.bassitology_admin.databinding.ActivityAddExamLessonQuestionsBinding;
import com.bassitology_admin.databinding.DialogAddResultBinding;
import com.bassitology_admin.databinding.DialogChooseImageBinding;
import com.bassitology_admin.model.AnswerModel;
import com.bassitology_admin.model.ExamModel;
import com.bassitology_admin.model.QuestionModel;
import com.bassitology_admin.share.Common;
import com.bassitology_admin.tags.Tags;
import com.bassitology_admin.uis.activity_base.BaseActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddExamLessonQuestionsActivity extends BaseActivity {
    private ActivityAddExamLessonQuestionsBinding binding;
    private ExamModel examModel;
    private ActivityResultLauncher<String[]> permissions;
    private Uri uri;
    private int req;
    private boolean isCorrectAnswer = false;
    private AddAnswerAdapter adapter;
    private int selectedAnswerPos = -1;
    private QuestionModel questionModel, questionModelForUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_exam_lesson_questions);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        examModel = (ExamModel) intent.getSerializableExtra("data");
        questionModelForUpdate = (QuestionModel) intent.getSerializableExtra("question");
    }

    private void initView() {
        String title = getString(R.string.add_question);
        if (examModel != null) {
            title = "(" + examModel.getTitle() + ")";
        }
        setUpToolbar(binding.toolbar, title, "", R.color.white, R.color.black);
        questionModel = new QuestionModel();

        adapter = new AddAnswerAdapter(this);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setAdapter(adapter);

        if (questionModelForUpdate != null) {
            questionModel.setUser_id(questionModelForUpdate.getUser_id());
            questionModel.setQuestion_id(questionModelForUpdate.getQuestion_id());
            questionModel.setExam_id(questionModelForUpdate.getExam_id());
            questionModel.setLesson_id(questionModelForUpdate.getLesson_id());
            questionModel.setChapter_id(questionModelForUpdate.getChapter_id());
            questionModel.setTitle(questionModelForUpdate.getTitle());
            questionModel.setAnswers(questionModelForUpdate.getAnswers());
            questionModel.setLink(questionModelForUpdate.getLink());
            questionModel.setImage(questionModelForUpdate.getImage());
            questionModel.setValid(true);
            adapter.updateList(questionModelForUpdate.getAnswers());

            Glide.with(this)
                    .load(Uri.parse(questionModel.getImage()))
                    .centerCrop()
                    .into(binding.image);
            binding.btnChooseImage.setVisibility(View.GONE);


        }else {
            List<AnswerModel> answers = new ArrayList<>();
            answers.add(new AnswerModel("أ",false));
            answers.add(new AnswerModel("ب",false));
            answers.add(new AnswerModel("ج",false));
            answers.add(new AnswerModel("د",false));
            adapter.updateList(answers);
            questionModel.setAnswers(answers);

        }
        binding.setModel(questionModel);


        binding.btnChooseImage.setOnClickListener(view -> {
            chooseImage();
        });
        binding.image.setOnClickListener(view -> {
            chooseImage();
        });

        permissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (!isGranted.containsValue(false)) {
                if (req == 1) {
                    openGallery();
                } else {
                    openCamera();
                }
            } else {
                Toast.makeText(this, "Permission to access photo denied", Toast.LENGTH_SHORT).show();

            }
        });

        binding.cardAddResult.setOnClickListener(view -> {
            createResultDialog(null);
        });

        binding.btnSave.setOnClickListener(view -> {
            if (questionModelForUpdate == null) {
                saveImage();
            } else {
                if (questionModel.getUri() != null && !questionModel.getUri().isEmpty()) {
                    updateImage();
                } else {
                    updateQuestion();
                }
            }

        });

    }


    private void saveImage() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref = dRef.child(Tags.table_questions)
                .child(getUserModel().getId())
                .child(examModel.getChapter_id())
                .child(examModel.getLesson_id())
                .child(examModel.getId());
        String question_id = ref.push().getKey();

        StorageReference sRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = sRef.child(Tags.image_lessons_path)
                .child(getUserModel().getId())
                .child(examModel.getChapter_id())
                .child(examModel.getLesson_id())
                .child(examModel.getId())
                .child(question_id);

        imageRef.putFile(Uri.parse(questionModel.getUri()))
                .addOnSuccessListener(taskSnapshot -> {
                    if (taskSnapshot.getTask().isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            addQuestion(question_id, uri, dialog);
                        });
                    } else {
                        dialog.dismiss();
                    }
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void addQuestion(String question_id, Uri uri, ProgressDialog dialog) {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        questionModel.setChapter_id(examModel.getChapter_id());
        questionModel.setLesson_id(examModel.getLesson_id());
        questionModel.setExam_id(examModel.getId());
        questionModel.setQuestion_id(question_id);
        questionModel.setImage(uri.toString());
        dRef.child(Tags.table_questions)
                .child(getUserModel().getId())
                .child(examModel.getChapter_id())
                .child(examModel.getLesson_id())
                .child(examModel.getId())
                .child(question_id)
                .setValue(questionModel)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateQuestion() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(Tags.table_questions)
                .child(getUserModel().getId())
                .child(examModel.getChapter_id())
                .child(examModel.getLesson_id())
                .child(examModel.getId())
                .child(questionModel.getQuestion_id())
                .setValue(questionModel)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateImage() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.deleting));
        dialog.show();

        StorageReference sRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = sRef.child(Tags.image_lessons_path)
                .child(getUserModel().getId())
                .child(examModel.getChapter_id())
                .child(examModel.getLesson_id())
                .child(examModel.getId())
                .child(questionModel.getQuestion_id());
        imageRef.putFile(Uri.parse(questionModel.getUri()))
                .addOnSuccessListener(taskSnapshot -> {
                    if (taskSnapshot.getTask().isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            addQuestion(questionModel.getQuestion_id(), uri, dialog);
                        });
                    } else {
                        dialog.dismiss();
                    }
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void createResultDialog(AnswerModel model) {
        isCorrectAnswer = false;
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        DialogAddResultBinding dialogAddResultBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_add_result, null, false);
        dialog.setView(dialogAddResultBinding.getRoot());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        if (model == null) {
            dialogAddResultBinding.edtResult.setText(null);
            dialogAddResultBinding.rbCorrectAnswer.setChecked(false);
            dialogAddResultBinding.rbWrongAnswer.setChecked(true);
            isCorrectAnswer = false;

        } else {
            isCorrectAnswer = model.isAnswer();
            dialogAddResultBinding.edtResult.setText(model.getTitle());
            if (model.isAnswer()) {
                dialogAddResultBinding.rbCorrectAnswer.setChecked(true);

            } else {
                dialogAddResultBinding.rbWrongAnswer.setChecked(false);

            }
        }

        dialogAddResultBinding.rbCorrectAnswer.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isCorrectAnswer = true;
            }
        });

        dialogAddResultBinding.rbWrongAnswer.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isCorrectAnswer = false;
            }
        });


        dialogAddResultBinding.tvAdd.setOnClickListener(v -> {
            String q = dialogAddResultBinding.edtResult.getText().toString();
            if (!q.isEmpty()) {
                if (model == null) {
                    AnswerModel answerModel = new AnswerModel(q, isCorrectAnswer);
                    questionModel.addAnswer(answerModel);

                } else {
                    model.setAnswer(isCorrectAnswer);
                    model.setTitle(q);
                    questionModel.updateAnswer(model, selectedAnswerPos);
                }
                adapter.updateList(questionModel.getAnswers());
                dialog.dismiss();
            }

        });

        dialogAddResultBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void cropImage(Uri uri) {

        CropImage.activity(uri).setAllowFlipping(true)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAutoZoomEnabled(true)
                .setFlipHorizontally(true)
                .setFlipVertically(true)
                .start(this);
    }

    private void chooseImage() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        DialogChooseImageBinding imageBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_choose_image, null, false);
        dialog.setView(imageBinding.getRoot());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        imageBinding.tvCamera.setOnClickListener(v -> {
            req = 2;
            checkCameraPermission();
            dialog.dismiss();
        });
        imageBinding.tvGallery.setOnClickListener(v -> {
            req = 1;
            checkGalleryPermission();
            dialog.dismiss();
        });
        imageBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void checkCameraPermission() {
        String[] permissions = new String[]{BaseActivity.WRITE_PERM, BaseActivity.CAM_PERM};
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            this.permissions.launch(permissions);

        }
    }

    private void checkGalleryPermission() {
        String[] permissions = new String[]{BaseActivity.READ_PERM};
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            this.permissions.launch(permissions);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose image"), 1);

    }

    private void openCamera() {
        File fileImage = createImageFile();
        if (fileImage != null) {
            uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", fileImage);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 2);
        } else {
            Toast.makeText(this, "You don't allow to access photos", Toast.LENGTH_SHORT).show();
        }

    }

    private File createImageFile() {
        File imageFile = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm", Locale.ENGLISH).format(new Date());
        String imageName = "JPEG_" + timeStamp + "_";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "basitology_app_photos");
        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            imageFile = File.createTempFile(imageName, ".jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("err", e.getMessage());
        }

        return imageFile;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult res = CropImage.getActivityResult(data);
            if (res != null && res.getUri() != null) {
                binding.image.setImageURI(res.getUri());
                binding.btnChooseImage.setVisibility(View.GONE);
                questionModel.setUri(res.getUri().toString());
            }

        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            cropImage(data.getData());
        } else if (requestCode == 2) {
            cropImage(uri);
        }
    }

    public void updateAnswer(AnswerModel model, int adapterPosition) {
        selectedAnswerPos = adapterPosition;
        createResultDialog(model);
    }

    public void deleteAnswer(int adapterPosition) {
        questionModel.removeAnswer(adapterPosition);
        adapter.updateList(questionModel.getAnswers());

    }
}