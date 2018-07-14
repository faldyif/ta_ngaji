package com.preklit.ngaji.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.Utils;
import com.preklit.ngaji.entities.ApiError;
import com.preklit.ngaji.entities.RegisterResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    @BindView(R.id.til_name)
    TextInputLayout tilName;
    @BindView(R.id.til_email)
    TextInputLayout tilEmail;
    @BindView(R.id.til_wa)
    TextInputLayout tilWA;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.radio_male)
    AppCompatRadioButton radioMale;
    @BindView(R.id.radio_female)
    AppCompatRadioButton radioFemale;

    ApiService service;
    Call<RegisterResponse> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Bind butterknife
        ButterKnife.bind(this);
        initToolbar();

        // Start retrofit service
        service = RetrofitBuilder.createService(ApiService.class);

        // Init token manager
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        // Init validation
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daftar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.email_sign_up_button)
    void register() {
        // Grab values from view
        String name = tilName.getEditText().getText().toString();
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();
        String waNumber = tilWA.getEditText().getText().toString();
        Character gender = null;

        Boolean isMaleChecked = radioMale.isChecked();
        Boolean isFemaleChecked = radioFemale.isChecked();

        // Add prefix +62 in front of whatsapp number
        if(waNumber.equals(null) || waNumber.equals("")) {
            waNumber = "";
        } else {
            waNumber = "+62" + waNumber;
        }

        if(isFemaleChecked) {
            gender = 'F';
        } else if(isMaleChecked) {
            gender = 'M';
        }

        Log.w(TAG, "register: " + name + ", " + email + ", " + password);

        // Reset all error values
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilWA.setError(null);
        radioFemale.setError(null);

        // Check validation
        validator.clear();
        Boolean validated = validator.validate();

        if(gender == null) {
            radioFemale.setError(getString(R.string.err_gender));
        }

        if(validated && gender != null) {

            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Memuat..");
            progress.setMessage("Memuat...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            call = service.register(name, email, password, gender, waNumber);
            call.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                    Log.w(TAG, "onResponse: " + response);

                    if (response.isSuccessful()) {
                        showCustomDialog(response.body().getMessage());
                    } else {
                        handleErrors(response.errorBody());
                    }

                    // dismiss the dialog
                    progress.dismiss();

                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());

                    // dismiss the dialog
                    progress.dismiss();
                }
            });
        }
    }

    private void handleErrors(ResponseBody response) {

        ApiError apiError = Utils.convertErrors(response);

        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if(error.getKey().equals("name")) {
                tilName.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("email")) {
                tilEmail.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("password")) {
                tilPassword.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("gender")) {
                radioFemale.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("whatsapp_number")) {
                radioFemale.setError(error.getValue().get(0));
            }
        }
    }

    private void showCustomDialog(String message) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View dialogView = inflater.inflate(R.layout.dialog_info, null);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(dialogView);
        dialog.setCancelable(true);

        TextView txtContent = dialogView.findViewById(R.id.content);
        txtContent.setText(message);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void setupRules() {

        validator.addValidation(this, R.id.til_name, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.til_password, "[^$]{6,}", R.string.err_password); // regex to match all string 6 chars min

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null) {
            call.cancel();
            call = null;
        }
    }
}
