package com.preklit.ngaji.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.UserManager;
import com.preklit.ngaji.entities.SelfUserDetail;
import com.preklit.ngaji.entities.TeacherFreeTimeResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.network.firebase.FirebaseInstanceIDService;
import com.preklit.ngaji.utils.Tools;
import com.preklit.ngaji.utils.ViewAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static final String TAG = MainActivity.class.getSimpleName();

    private TokenManager tokenManager;
    private UserManager userManager;
    private ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        initToolbar();

        userManager = UserManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(userManager.getUserDetail() == null) {
            getSelfUserData();
        } else {
            toolbar.setTitle("Halo, " + userManager.getUserDetail().getName());
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
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
        } else if (item.getItemId() == R.id.action_logout) {
            logOut();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.menu_guru_jadwal)
    void clickMenuGuruJadwal() {
        Intent intent = new Intent(MainActivity.this, JadwalGuruActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.menu_tahsin)
    void clickMenuTahsin() {
        openTeacherSearchActivity("tahsin");
    }

    @OnClick(R.id.menu_tahfidz)
    void clickMenuTahfidz() {
        openTeacherSearchActivity("tahfidz");
    }

    @OnClick(R.id.menu_tadabbur)
    void clickMenuTadabbur() {
        openTeacherSearchActivity("tadabbur");
    }

    void openTeacherSearchActivity(String ngajiType) {
        Intent intent = new Intent(MainActivity.this, TeacherSearchActivity.class);
        intent.putExtra("ngaji_type", ngajiType);
        startActivity(intent);
    }

    @OnClick(R.id.menu_santri_history)
    void openMenuSantriJadwal() {
        Intent intent = new Intent(MainActivity.this, ListEventForStudentActivity.class);
        startActivity(intent);
    }

    void getSelfUserData(){
        Call<SelfUserDetail> call = service.refreshSelfUserDetail();
        call.enqueue(new Callback<SelfUserDetail>() {
            @Override
            public void onResponse(Call<SelfUserDetail> call, Response<SelfUserDetail> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());
                    userManager.saveUser(response.body());
                    toolbar.setTitle("Halo, " + userManager.getUserDetail().getName());
                }else {
                    Toast.makeText(MainActivity.this, "Kok gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SelfUserDetail> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }

    void logOut() {
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Call<Object> call = service.logout(firebaseToken);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    tokenManager.deleteToken();
                    userManager.deleteUser();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "Kok gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }
}
