package com.preklit.ngaji.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.iid.FirebaseInstanceId;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.Utils;
import com.preklit.ngaji.entities.ApiError;
import com.preklit.ngaji.entities.CreateResponse;
import com.preklit.ngaji.entities.SelfUserDetail;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.RealPathUtil;
import com.preklit.ngaji.utils.Tools;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 4323;
    private static final int REQUEST_WRITE_PERMISSION = 2341;

    @BindView(R.id.btn_choose_photo)
    FloatingActionButton buttonChoosePhoto;
    @BindView(R.id.preview_image)
    ImageView imageViewPreviewImage;
    @BindView(R.id.tv_name)
    TextInputLayout tilName;
    @BindView(R.id.tv_phone)
    TextInputLayout tilPhone;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Context ctx;
    private TokenManager tokenManager;
    private ApiService service;
    private Uri mImageCaptureUri;
    private File imgFile;
    private String name;
    private String phoneNumber;
    private AwesomeValidation validator;
    private Call<CreateResponse> callUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ButterKnife.bind(this);

        ctx = this;
        initToolbar();
        progressBar.setVisibility(View.GONE);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();
        getSelfUserData();
    }

    private void setupRules() {
        validator.addValidation(this, R.id.tv_name, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.tv_phone, RegexTemplate.TELEPHONE, R.string.err_phone);
        validator.addValidation(this, R.id.tv_phone, RegexTemplate.NOT_EMPTY, R.string.err_phone);
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_done) {
            updateProfile();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_choose_photo)
    void clickChoosePhoto() {
        requestPermission();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            openFilePicker();
        }
    }

    private void openFilePicker() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            imgFile = new File(uriToFilename(uri));
            mImageCaptureUri = Uri.fromFile(imgFile);

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageViewPreviewImage.setImageBitmap(myBitmap);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                Toast.makeText(ctx, "Izin membaca storage diperlukan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String uriToFilename(Uri uri) {
        String path = null;

        if (Build.VERSION.SDK_INT < 19) {
            path = RealPathUtil.getRealPathFromURI_API11to18(ctx, uri);
        } else {
            path = RealPathUtil.getRealPathFromURI_API19(ctx, uri);
        }
        return path;
    }

    void getSelfUserData(){
        Call<SelfUserDetail> call = service.refreshSelfUserDetail();
        call.enqueue(new Callback<SelfUserDetail>() {
            @Override
            public void onResponse(Call<SelfUserDetail> call, Response<SelfUserDetail> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());
                    refreshFields(response.body());
                }else {
                    Toast.makeText(EditProfileActivity.this, "Kok gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SelfUserDetail> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }

    void updateProfile() {

        name = tilName.getEditText().getText().toString();
        phoneNumber = "+62" + tilPhone.getEditText().getText().toString();

        tilName.setError(null);
        tilPhone.setError(null);

        validator.clear();

        if (validator.validate()) {
            progressBar.setVisibility(View.VISIBLE);

            MultipartBody.Part body = null;
            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phoneNumber);

            if(imgFile != null) {
                // Wrap into multipart
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
                body = MultipartBody.Part.createFormData("photo", imgFile.getName(), reqFile);
            } else {
                RequestBody file = RequestBody.create(MultipartBody.FORM, "");
                body = MultipartBody.Part.createFormData("photo", "", file);
            }

            callUpdate = service.updateProfile(body, nameBody, phoneBody);
            callUpdate.enqueue(new Callback<CreateResponse>() {
                @Override
                public void onResponse(Call<CreateResponse> call, Response<CreateResponse> response) {
                    Log.w(TAG, "onResponse: " + response );
                    progressBar.setVisibility(View.GONE);

                    if(response.isSuccessful()){
                        Log.w(TAG, "onResponse: " + response.body());
                        Toast.makeText(EditProfileActivity.this, "Berhasil mengupdate profil!", Toast.LENGTH_SHORT).show();
                    }else {
                        if (response.code() == 422) {
//                            Snackbar.make(parent_view, "Sign up for an account", Snackbar.LENGTH_SHORT).show();
                            handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.convertErrors(response.errorBody());
//                            Snackbar.make(parent_view, apiError.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CreateResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.w(TAG, "onFailure: " + t.getMessage() );
                }
            });
        }
    }

    private void handleErrors(ResponseBody responseBody) {
        ApiError apiError = Utils.convertErrors(responseBody);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("name")) {
                tilName.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("whatsapp_number")) {
                tilPhone.setError(error.getValue().get(0));
            }
        }
    }

    private void refreshFields(SelfUserDetail userDetail) {
        Tools.displayImageOriginal(ctx, imageViewPreviewImage, userDetail.getProfilePicUrl());
        tilName.getEditText().setText(userDetail.getName());
        tilPhone.getEditText().setText(userDetail.getWhatsappNumber().substring(3));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (callUpdate != null) {
            callUpdate.cancel();
            callUpdate = null;
        }
    }
}
