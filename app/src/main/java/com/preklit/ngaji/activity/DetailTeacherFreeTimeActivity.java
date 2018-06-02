package com.preklit.ngaji.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.preklit.ngaji.R;
import com.preklit.ngaji.entities.TeacherFreeTime;
import com.preklit.ngaji.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailTeacherFreeTimeActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextView textViewName;
    @BindView(R.id.start_date)
    TextView textViewStartDate;
    @BindView(R.id.start_time)
    TextView textViewStartTime;
    @BindView(R.id.end_time)
    TextView textViewEndTime;
    @BindView(R.id.rank)
    TextView textViewRank;
    @BindView(R.id.medal)
    ImageView imageViewRank;
    @BindView(R.id.btn_accept)
    Button buttonAccept;

    SupportMapFragment mapFragment;
    TeacherFreeTime teacherFreeTime;
    GoogleMap mMap;
    Gson gson;
    Context ctx;

    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_teacher_free_time);
        ButterKnife.bind(this);

        ctx = getApplicationContext();
        gson = new Gson();
        Intent intent = getIntent();
        teacherFreeTime = gson.fromJson(intent.getStringExtra("free_time_details"), TeacherFreeTime.class);
        latitude = intent.getDoubleExtra("latitude_choosen", 0);
        longitude = intent.getDoubleExtra("longitude_choosen", 0);

        initToolbar();
        initComponent();
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail Guru");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        final CircularImageView image = (CircularImageView) findViewById(R.id.image);
        final CollapsingToolbarLayout collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int min_height = ViewCompat.getMinimumHeight(collapsing_toolbar) * 2;
                float scale = (float) (min_height + verticalOffset) / min_height;
                image.setScaleX(scale >= 0 ? scale : 0);
                image.setScaleY(scale >= 0 ? scale : 0);
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("in", "id-ID"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Tools.displayImageRoundFromUrl(this, image, teacherFreeTime.getTeacher().getProfilePicUrl());
        textViewName.setText(teacherFreeTime.getTeacher().getName());
        textViewStartDate.setText(dateFormat.format(teacherFreeTime.getDateStart()));
        textViewStartTime.setText(timeFormat.format(teacherFreeTime.getDateStart()));
        textViewEndTime.setText(timeFormat.format(teacherFreeTime.getDateEnd()));
        textViewRank.setText(teacherFreeTime.getTeacherRankDescription());
        imageViewRank.setColorFilter(ContextCompat.getColor(this, teacherFreeTime.getTeacherRankMedalColor()), android.graphics.PorterDuff.Mode.SRC_IN);

        if(teacherFreeTime.getTeacherRank() != 1) {
            buttonAccept.setText("Jadwalkan (" + teacherFreeTime.getPoints() + " poin)");
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Add a marker in Sydney and move the camera
                LatLng lokasiGuru = new LatLng(teacherFreeTime.getLatitude(), teacherFreeTime.getLongitude());
                LatLng tempatNgaji = new LatLng(latitude, longitude);
                Marker marker1 = mMap.addMarker(new MarkerOptions().position(lokasiGuru).title("Lokasi guru ngaji"));
                marker1.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lokasiGuru));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 1, null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
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

    @OnClick(R.id.btn_accept)
    void showSendRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi penjadwalan");
        builder.setMessage("Apakah anda yakin akan mengirim permintaan jadwal ngaji ke guru ini?");
        builder.setPositiveButton(R.string.AGREE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ctx, "Agree", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.DISAGREE, null);
        builder.show();
    }
}
