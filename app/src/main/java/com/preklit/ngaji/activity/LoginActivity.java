package com.preklit.ngaji.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.iid.FirebaseInstanceId;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.Utils;
import com.preklit.ngaji.entities.AccessToken;
import com.preklit.ngaji.entities.ApiError;
import com.preklit.ngaji.entities.RegisterResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.Tools;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    @BindView(R.id.til_username)
    TextInputLayout tilUsername;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fabLogin;

    private View parent_view;

    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind butterknife
        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();

        // Check Login
        if(tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            parent_view = findViewById(android.R.id.content);

            Tools.setSystemBarColor(this, R.color.cyan_800);

            showIntro();
        }
    }

    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        fabLogin.setVisibility(View.INVISIBLE);
    }

    private void showForm(){
        progressBar.setVisibility(View.INVISIBLE);
        fabLogin.setVisibility(View.VISIBLE);
    }

    private void setupRules() {
        validator.addValidation(this, R.id.til_username, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.til_password, RegexTemplate.NOT_EMPTY, R.string.err_password);
    }

    @OnClick(R.id.sign_up_for_account)
    void signUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fab)
    void login() {

        String email = tilUsername.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        tilUsername.setError(null);
        tilPassword.setError(null);

        validator.clear();

        if (validator.validate()) {
            String firebaseToken = FirebaseInstanceId.getInstance().getToken();
            showLoading();
            call = service.login(email, password, firebaseToken);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    Log.w(TAG, "onResponse: " + response);

                    if (response.isSuccessful()) {
                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        if (response.code() == 422) {
                            Snackbar.make(parent_view, "Sign up for an account", Snackbar.LENGTH_SHORT).show();
                            handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.convertErrors(response.errorBody());
                            Snackbar.make(parent_view, apiError.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                        showForm();
                    }

                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                    showForm();
                }
            });

        }
    }

    private void handleErrors(ResponseBody responseBody) {
        ApiError apiError = Utils.convertErrors(responseBody);
        showForm();

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("username")) {
                tilUsername.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")) {
                tilPassword.setError(error.getValue().get(0));
            }
        }
    }

    private void showIntro() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( this);
        if(!sp.getBoolean("introScreen",false)) {
            Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}
