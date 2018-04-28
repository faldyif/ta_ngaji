package com.preklit.ngaji.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.preklit.ngaji.R;
import com.preklit.ngaji.utils.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.til_username)
    TextInputLayout tilUsername;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind butterknife
        ButterKnife.bind(this);

        parent_view = findViewById(android.R.id.content);

        Tools.setSystemBarColor(this, R.color.cyan_800);

        ((View) findViewById(R.id.sign_up_for_account)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(parent_view, "Sign up for an account", Snackbar.LENGTH_SHORT).show();
                signUp();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchAction();
            }
        });

        showIntro();
    }

    private void searchAction() {
        progressBar.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                fab.setAlpha(1f);
//                Snackbar.make(parent_view, "Login data submitted", Snackbar.LENGTH_SHORT).show();
                login();
            }
        }, 1000);
    }

    private void login() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void signUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void showIntro() {
        Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
        startActivity(intent);
    }
}
