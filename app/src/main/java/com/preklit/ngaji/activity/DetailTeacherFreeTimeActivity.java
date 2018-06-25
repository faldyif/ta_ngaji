package com.preklit.ngaji.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.entities.CreateResponse;
import com.preklit.ngaji.entities.TeacherFreeTime;
import com.preklit.ngaji.entities.TeacherFreeTimeResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.Tools;
import com.preklit.ngaji.utils.ViewAnimation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTeacherFreeTimeActivity extends AppCompatActivity {

    private static final String TAG = DetailTeacherFreeTimeActivity.class.getSimpleName();

    @BindView(R.id.name)
    TextView textViewName;
    @BindView(R.id.start_date)
    TextView textViewStartDate;
    @BindView(R.id.time_info)
    TextView textViewTimeInfo;
    @BindView(R.id.rank)
    TextView textViewRank;
    @BindView(R.id.event_type)
    TextView textViewEventType;
    @BindView(R.id.location_description)
    TextView textViewLocationDescription;
    @BindView(R.id.medal)
    ImageView imageViewRank;
    @BindView(R.id.btn_accept)
    Button buttonAccept;

    SupportMapFragment mapFragment;
    TeacherFreeTime teacherFreeTime;
    GoogleMap mMap;
    Gson gson;
    Context ctx;
    private ProgressDialog progressDialog;

    Double latitude;
    Double longitude;
    Date dateStart;
    Date dateEnd;
    String eventType;
    String locationDetails;
    String shortPlaceName;

    private Call<CreateResponse> call;
    private TokenManager tokenManager;
    private ApiService service;

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
        dateStart = gson.fromJson(intent.getStringExtra("time_start"), Date.class);
        dateEnd = gson.fromJson(intent.getStringExtra("time_end"), Date.class);
        eventType = intent.getStringExtra("event_type");
        locationDetails = intent.getStringExtra("location_details");
        shortPlaceName = intent.getStringExtra("short_place_name");
        Log.w(TAG, "onCreate: " + locationDetails);

        progressDialog = new ProgressDialog(this);
        initToolbar();
        initComponent();

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(DetailTeacherFreeTimeActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
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
        textViewTimeInfo.setText(timeFormat.format(dateStart) + " - " + timeFormat.format(dateEnd));
        textViewRank.setText(teacherFreeTime.getTeacherRankDescription());
        textViewEventType.setText("Kelas " + eventType.substring(0, 1).toUpperCase() + eventType.substring(1));
        imageViewRank.setColorFilter(ContextCompat.getColor(this, teacherFreeTime.getTeacherRankMedalColor()), android.graphics.PorterDuff.Mode.SRC_IN);
        textViewLocationDescription.setText(locationDetails);

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
//                Marker marker1 = mMap.addMarker(new MarkerOptions().position(lokasiGuru).title("Lokasi guru ngaji"));
//                marker1.showInfoWindow();
                Marker marker2 = mMap.addMarker(new MarkerOptions().position(tempatNgaji).title("Lokasi ngaji"));
                marker2.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(tempatNgaji));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);
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
        String alertMessage = "Apakah anda yakin akan mengirim permintaan jadwal ngaji ke guru ini?";
        if(teacherFreeTime.getTeacherRank() != 1) {
            if(teacherFreeTime.getTeacherRank() == 2) {
                alertMessage += " Guru yang akan anda panggil adalah guru dengan level perak.";
            } else if(teacherFreeTime.getTeacherRank() == 3) {
                alertMessage += " Guru yang akan anda panggil adalah guru dengan level emas.";
            }
            alertMessage += " Poin anda akan berkurang sejumlah " + teacherFreeTime.getPoints() + " poin jika anda setuju.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi penjadwalan");
        builder.setMessage(alertMessage);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(ctx, "Agree", Toast.LENGTH_SHORT).show();
                sendRequest();
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    void sendRequest() {
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();

        Log.w(TAG, "sendRequest: " + locationDetails);

        call = service.createEvent(latitude, longitude, Tools.convertDateToDateTimeMySQL(dateStart), Tools.convertDateToDateTimeMySQL(dateEnd), eventType, String.valueOf(teacherFreeTime.getId()), locationDetails, shortPlaceName);
        call.enqueue(new Callback<CreateResponse>() {
            @Override
            public void onResponse(Call<CreateResponse> call, Response<CreateResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    if(response.code() == 204) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        showSuccessDialog("Berhasil mengirim permintaan jadwal ke calon guru! Anda akan dikirimkan notifikasi ketika guru anda sudah menerima permintaan jadwal anda");
                    }
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(DetailTeacherFreeTimeActivity.this, LoginActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<CreateResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }

    private void showSuccessDialog(String message) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View dialogView = inflater.inflate(R.layout.dialog_info, null);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(dialogView);
        dialog.setCancelable(true);

        TextView txtContent = (TextView) dialogView.findViewById(R.id.content);
        txtContent.setText(message);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(DetailTeacherFreeTimeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
