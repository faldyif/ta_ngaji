package com.preklit.ngaji.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.utils.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initToolbar();
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
}
