package com.preklit.ngaji.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.google.common.collect.Lists;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nex3z.notificationbadge.NotificationBadge;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.UserManager;
import com.preklit.ngaji.activity.teacher.ListPengajuanEventActivity;
import com.preklit.ngaji.activity.teacher.ListTeacherFreeTimeActivity;
import com.preklit.ngaji.activity.teacher.ListTeachingHistoryActivity;
import com.preklit.ngaji.activity.teacher.ListUpcomingEventActivity;
import com.preklit.ngaji.entities.CreateResponse;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.entities.EventsResponse;
import com.preklit.ngaji.entities.SelfUserDetail;
import com.preklit.ngaji.entities.TeacherFreeTimeResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.network.firebase.FirebaseInstanceIDService;
import com.preklit.ngaji.utils.Tools;
import com.preklit.ngaji.utils.ViewAnimation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.teacher_card_section)
    CardView teacherCardSection;
    @BindView(R.id.reminder_lyt)
    LinearLayout reminderLayout;
    @BindView(R.id.tv_ngajipoints)
    TextView textViewNgajiPoints;
    @BindView(R.id.tv_nama_pengguna)
    TextView textViewNamaPengguna;
    @BindView(R.id.tv_phone_number)
    TextView textViewPhoneNumber;
    @BindView(R.id.photo_round)
    ImageView imageViewPhotoRound;
    private static final String TAG = MainActivity.class.getSimpleName();

    private TokenManager tokenManager;
    private UserManager userManager;
    private ApiService service;
    private ProgressDialog progressDialog;
    private int mPengajuanGuruCount = 0;
    NotificationBadge teacherPengajuanBadge;

    private List<Event> arrEvent;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        ctx = this;

        teacherPengajuanBadge = findViewById(R.id.teacher_pengajuan_badge);
        teacherPengajuanBadge.setText("...");

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
            initUserText();
            getSelfUserData();
            if(userManager.getUserDetail().getRole().equals("teacher"))
                getCountUnconfirmed();

        }
        getListEvents2Hours();
    }

    private void initUserText() {
        Tools.displayImageRoundFromUrl(ctx, imageViewPhotoRound, userManager.getUserDetail().getProfilePicUrl());
        toolbar.setTitle("Halo, " + userManager.getUserDetail().getName());
        textViewNgajiPoints.setText(userManager.getUserDetail().getLoyaltyPoints() + " NgajiPoints");
        textViewNamaPengguna.setText(userManager.getUserDetail().getName());
        textViewPhoneNumber.setText(userManager.getUserDetail().getWhatsappNumber());
        initTeacherMenu();
    }

    private void initTeacherMenu() {
        Log.w(TAG, "initTeacherMenu: " + userManager.getUserDetail().getRole());

        if(!userManager.getUserDetail().getRole().equals("teacher"))
            teacherCardSection.setVisibility(View.GONE);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        } else if (item.getItemId() == R.id.action_help) {
            Intent intent = new Intent();
        } else if (item.getItemId() == R.id.action_logout) {
            logOut();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.menu_guru_jadwal)
    void clickMenuGuruJadwal() {
        Intent intent = new Intent(MainActivity.this, ListTeacherFreeTimeActivity.class);
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

    void openTeacherSearchActivity(String ngajiType) {
        Intent intent = new Intent(MainActivity.this, TeacherSearchActivity.class);
        intent.putExtra("ngaji_type", ngajiType);
        startActivity(intent);
    }

    @OnClick(R.id.menu_santri_pengajuan)
    void openMenuSantriPengajuan() {
        Intent intent = new Intent(MainActivity.this, ListEventForStudentActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.menu_santri_jadwal)
    void openMenuSantriJadwal() {
        Intent intent = new Intent(MainActivity.this, TimelineEventStudentActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.menu_santri_riwayat)
    void openMenuSantriHistory() {
        Intent intent = new Intent(MainActivity.this, ListStudyHistoryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.menu_guru_upcoming)
    void openMenuGuruUpcoming() {
        Intent intent = new Intent(MainActivity.this, ListUpcomingEventActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.menu_guru_pengajuan)
    void openMenuGuruPengajuan() {
        Intent intent = new Intent(MainActivity.this, ListPengajuanEventActivity.class);
        startActivityForResult(intent, 123);
    }

    @OnClick(R.id.menu_guru_riwayat)
    void openMenuGuruRiwayat() {
        Intent intent = new Intent(MainActivity.this, ListTeachingHistoryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_edit_profile)
    void openEditProfile() {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }
//
//    @OnClick(R.id.cek_presensi)
//    void onclickCekPresensi() {
//        Intent intent = new Intent(MainActivity.this, PresenceActivity.class);
//        startActivity(intent);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123) {
            getCountUnconfirmed();
        }
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
                    initUserText();
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

    void getCountUnconfirmed(){
        Call<Integer> call = service.countEventUnconfirmed();
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body());
                    mPengajuanGuruCount = response.body();
                    teacherPengajuanBadge.setNumber(mPengajuanGuruCount);
                    initTeacherMenu();
                }else {
                    Toast.makeText(MainActivity.this, "Kok gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }

    void getListEvents2Hours(){
        Call<EventsResponse> call = service.listEvents2Hours();
        call.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    Log.w(TAG, "onResponse: " + response.body().getData());
                    arrEvent = response.body().getData();

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");


                    for (final Event event: arrEvent) {
                        LayoutInflater inflater = LayoutInflater.from(ctx);
                        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.lyt_card_reminder, null, false);

                        CircularImageView imageView = layout.findViewById(R.id.photo_round);
                        TextView textViewName = layout.findViewById(R.id.card_name);
                        TextView textViewJadwalDesc = layout.findViewById(R.id.jadwal_desc);
                        TextView textViewTimeInfo = layout.findViewById(R.id.time_info);
                        TextView textViewLocationDescription = layout.findViewById(R.id.location_description);
                        TextView textViewTimeAgo = layout.findViewById(R.id.time_ago);
                        Button btnMap = layout.findViewById(R.id.btn_map);
                        Button btnPresensi = layout.findViewById(R.id.btn_presensi);
                        Button btnPenilaian = layout.findViewById(R.id.btn_penilaian);

                        Date dateStart = Tools.convertDateTimeMySQLStringToJavaDate(event.getStartTime());
                        Date dateEnd = Tools.convertDateTimeMySQLStringToJavaDate(event.getEndTime());

                        if(event.getPresence().getCheckInTime() != null) {
                            btnPresensi.setEnabled(false);
                            btnPresensi.setText("Sudah Presensi");
                            btnPresensi.setTextColor(Color.GRAY);
                            if (event.getStudent().getId() == Integer.valueOf(userManager.getUserDetail().getId())) {
                                if(event.getPresence().getRatingToTeacher() != null) {
                                    continue;
                                }
                            } else {
                                if(event.getPresence().getRatingToStudent() != null) {
                                    continue;
                                }
                            }
                        } else {
                            btnPenilaian.setEnabled(false);
                            btnPenilaian.setTextColor(Color.GRAY);
                        }
                        if(event.getTeacher().getId() == Integer.valueOf(userManager.getUserDetail().getId())) {
                            Tools.displayImageRoundFromUrl(ctx, imageView, event.getStudent().getProfilePicUrl());
                            textViewName.setText(event.getStudent().getName());
                            textViewJadwalDesc.setText("Jadwal Mengajar");

                        } else {
                            Tools.displayImageRoundFromUrl(ctx, imageView, event.getTeacher().getProfilePicUrl());
                            textViewName.setText(event.getTeacher().getName());
                            textViewJadwalDesc.setText("Jadwal Belajar");
                        }

                        // TODO: Perbaiki time ago bahasanya belum bisa bahasa indonesia
                        textViewTimeInfo.setText(dateFormat.format(dateStart) + ", " + timeFormat.format(dateStart) + " - " + timeFormat.format(dateEnd));

                        Locale localeBylanguageTag = new Locale("in");
                        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(localeBylanguageTag).build();
                        String textTimeAgo = TimeAgo.using(dateStart.getTime(), messages);
                        textViewTimeAgo.setText(textTimeAgo);

                        textViewLocationDescription.setText("Di " + event.getShortPlaceName() + ((event.getLocationDetails() != null) ? "(" + event.getLocationDetails() + ")" : ""));
                        btnPresensi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(event.getTeacher().getId() == Integer.valueOf(userManager.getUserDetail().getId())) {
                                    Intent intent = new Intent(MainActivity.this, com.preklit.ngaji.activity.teacher.PresenceActivity.class);
                                    intent.putExtra("event", (new Gson()).toJson(event));
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(MainActivity.this, PresenceActivity.class);
                                    intent.putExtra("event", (new Gson()).toJson(event));
                                    startActivity(intent);
                                }
                            }
                        });
                        btnMap.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(Lokasi+Ngaji)", event.getLatitude(), event.getLongitude(), event.getLatitude(), event.getLongitude());
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                ctx.startActivity(intent);
                            }
                        });
                        btnPenilaian.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showReviewDialog(layout, event);
                            }
                        });


                        reminderLayout.addView(layout);
                        Log.d(TAG, "onResponse: " + "layout added");
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Kok gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }

    void logOut() {
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Call<Object> call = service.logout(firebaseToken);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

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

    private void showReviewDialog(final LinearLayout linearLayout, final Event event) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_review);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_post = dialog.findViewById(R.id.et_post);
        final AppCompatRatingBar rating_bar = dialog.findViewById(R.id.rating_bar);

        final TextView textViewName = dialog.findViewById(R.id.name);
        final TextView textViewDescription = dialog.findViewById(R.id.description);
        final CircularImageView circularImageViewPhoto = dialog.findViewById(R.id.photo);

        if(event.getTeacher().getId() == Integer.valueOf(userManager.getUserDetail().getId())) {
            Tools.displayImageRoundFromUrl(ctx, circularImageViewPhoto, event.getStudent().getProfilePicUrl());
            textViewName.setText(event.getStudent().getName());
            textViewDescription.setText("Siswa");

        } else {
            Tools.displayImageRoundFromUrl(ctx, circularImageViewPhoto, event.getTeacher().getProfilePicUrl());
            textViewName.setText(event.getTeacher().getName());
            textViewDescription.setText("Pengajar");
        }

        dialog.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bt_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = et_post.getText().toString().trim();
                if (review.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Kolom catatan penilaian wajib diisi!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onClick: " + rating_bar.getRating() + " " + review);
                    sendRatingToServer(linearLayout, event, review, Math.round(rating_bar.getRating()));
                }
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    void sendRatingToServer(final LinearLayout linearLayout, Event event, String note, Integer rating){
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();

        Call<CreateResponse> call = service.rateEvent(event.getId(), rating, note);
        call.enqueue(new Callback<CreateResponse>() {
            @Override
            public void onResponse(Call<CreateResponse> call, Response<CreateResponse> response) {
                Log.w(TAG, "onResponse: " + response );
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if(response.isSuccessful()){
                    linearLayout.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Penilaian telah dikirim!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Gagal mengirim penilaian!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }
}
