package com.preklit.ngaji.activity.teacher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.preklit.ngaji.activity.LoginActivity;
import com.preklit.ngaji.entities.CreateResponse;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.Tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.preklit.ngaji.MyApp.APP_NAME;

public class DetailEventActivity extends AppCompatActivity {

    private static final String TAG = DetailEventActivity.class.getSimpleName();

    @BindView(R.id.name)
    TextView textViewName;
    @BindView(R.id.whatsapp_number)
    TextView textViewWhatsappNumber;
    @BindView(R.id.status)
    TextView textViewStatus;
    @BindView(R.id.start_date)
    TextView textViewStartDate;
    @BindView(R.id.time_info)
    TextView textViewTimeInfo;
    @BindView(R.id.event_type)
    TextView textViewEventType;
    @BindView(R.id.location_description)
    TextView textViewLocationDescription;

    @BindView(R.id.btn_accept)
    Button buttonAccept;
    @BindView(R.id.btn_cancel)
    Button buttonCancel;
    @BindView(R.id.btn_change_request)
    Button buttonChangeRequest;

    SupportMapFragment mapFragment;
    Event event;
    GoogleMap mMap;
    Gson gson;
    Context ctx;
    private ProgressDialog progressDialog;

    Date dateStart;
    Date dateEnd;
    String eventType;
    Boolean hasProcessed;
    Integer eventIndex;

    private Call<CreateResponse> call;
    private TokenManager tokenManager;
    private ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event_teacher);
        ButterKnife.bind(this);

        // Get passed variable from activity berfore
        hasProcessed = false;
        ctx = getApplicationContext();
        gson = new Gson();
        Intent intent = getIntent();
        event = gson.fromJson(intent.getStringExtra("event_detail"), Event.class);
        eventIndex = intent.getIntExtra("event_detail", 0);

        progressDialog = new ProgressDialog(this);
        initToolbar();
        initComponent();

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(DetailEventActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail Guru");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("event_index", eventIndex);
        setResult(Activity.RESULT_OK, intent);
        Log.w(TAG, "onBackPressed: Nih");
        finish();
    }

    @OnClick(R.id.btn_change_request)
    void changeRequest() {
        Gson gson = new Gson();

        Intent intent;
//        if(event.getEventModificationRequest() != null && event.getEventModificationRequest().getRequestByTeacher() == 0) {
        if(event.getEventModificationRequest() != null) {
            intent = new Intent(DetailEventActivity.this, ReviewEventModificationRequestActivity.class);
        } else {
            intent = new Intent(DetailEventActivity.this, RequestEventModificationActivity.class);
        }
        intent.putExtra("event", gson.toJson(event));
        startActivity(intent);

    }

    private void initComponent() {
        final CircularImageView image = findViewById(R.id.image);
        final CollapsingToolbarLayout collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int min_height = ViewCompat.getMinimumHeight(collapsing_toolbar) * 2;
                float scale = (float) (min_height + verticalOffset) / min_height;
                image.setScaleX(scale >= 0 ? scale : 0);
                image.setScaleY(scale >= 0 ? scale : 0);
            }
        });

        if(event.getEventModificationRequest() != null) {
            if(event.getEventModificationRequest().getRequestByTeacher() == 1) {
                buttonChangeRequest.setText("Menunggu Persetujuan");
                buttonChangeRequest.setEnabled(false);
                buttonChangeRequest.setBackgroundColor(Color.GRAY);
                buttonChangeRequest.setTextColor(Color.BLACK);
            } else {
                buttonChangeRequest.setText("Respon Permintaan");
            }
        }

        // Initialize date formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("in", "id-ID"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        // Converting to local variable to easy modification
        dateStart = Tools.convertDateTimeMySQLStringToJavaDate(event.getStartTime());
        dateEnd = Tools.convertDateTimeMySQLStringToJavaDate(event.getEndTime());
        eventType = event.getEventType();

        // Inserting values to components
        Tools.displayImageRoundFromUrl(this, image, event.getStudent().getProfilePicUrl());
        textViewName.setText(event.getStudent().getName());
        textViewStartDate.setText(dateFormat.format(dateStart));
        textViewWhatsappNumber.setText(event.getStudent().getWhatsappNumber());
        textViewTimeInfo.setText(timeFormat.format(dateStart) + " - " + timeFormat.format(dateEnd));
        textViewEventType.setText("Kelas " + eventType.substring(0, 1).toUpperCase() + eventType.substring(1));
        textViewLocationDescription.setText(event.getLocationDetails());
        if(event.getLocationDetails() == null) {
            float twelveDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 3, ctx.getResources().getDisplayMetrics());

            textViewLocationDescription.setText("(Tidak ada keterangan lokasi yang diberikan)");
            textViewLocationDescription.setTextSize(twelveDp);
            textViewStatus.setTextColor(getResources().getColor(R.color.grey_600));
        }

        Log.w(TAG, "initComponent: " + event.getStatus());
        textViewStatus.setText("cek");
        // Inserting status values
        Drawable img = null;
        switch (event.getStatus()) {
            case "accepted":
                img = ctx.getResources().getDrawable(R.drawable.ic_check_circle);
                img.setBounds( 0, 0, 60, 60);

                textViewStatus.setText("Jadwal telah disetujui");
                textViewStatus.setCompoundDrawables(img, null, null, null);
                textViewStatus.setTextColor(getResources().getColor(R.color.green_600));

                buttonAccept.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.GONE);
                break;
            case "rejected":
                img = ctx.getResources().getDrawable(R.drawable.ic_close_circle);
                img.setBounds( 0, 0, 60, 60);

                textViewStatus.setText("Jadwal ditolak");
                textViewStatus.setCompoundDrawables(img, null, null, null);
                textViewStatus.setTextColor(getResources().getColor(R.color.red_400));

                buttonAccept.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.GONE);
                break;
            case "pending":
                img = ctx.getResources().getDrawable(R.drawable.ic_dots_horizontal_circle);
                img.setBounds( 0, 0, 60, 60);

                textViewStatus.setText("Menunggu respon guru...");
                textViewStatus.setCompoundDrawables(img, null, null, null);
                textViewStatus.setTextColor(getResources().getColor(R.color.grey_600));
                break;
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Add a marker in Sydney and move the camera
                LatLng tempatNgaji = new LatLng(event.getLatitude(), event.getLongitude());
                Marker marker2 = mMap.addMarker(new MarkerOptions().position(tempatNgaji).title("Lokasi ngaji"));
                marker2.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(tempatNgaji));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_with_phone_number, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_sms) {
            String message = "Assalamualaikum," + "\n" + "Saya " + event.getStudent().getName() + ", murid anda dari aplikasi " + APP_NAME + "\n";


            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:"+event.getStudent().getWhatsappNumber())); // This ensures only SMS apps respond
            intent.putExtra("sms_body", message);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if (item.getItemId() == R.id.action_call) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", event.getStudent().getWhatsappNumber(), null));
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_cancel)
    void clickCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi jadwal");
        builder.setMessage("Apakah anda akan menolak jadwal ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateEventStatus(0);
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    @OnClick(R.id.btn_accept)
    void clickAccept() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi jadwal");
        builder.setMessage("Apakah anda akan menerima jadwal ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateEventStatus(1);
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    void updateEventStatus(final Integer status) {
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();

        call = service.updateEventStatusTeacher(event.getId(), status);
        call.enqueue(new Callback<CreateResponse>() {
            @Override
            public void onResponse(Call<CreateResponse> call, Response<CreateResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showSuccessDialog("Berhasil mengupdate jadwal!", status);
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(DetailEventActivity.this, LoginActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<CreateResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }

    private void showSuccessDialog(String message, final Integer status) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View dialogView = inflater.inflate(R.layout.dialog_info, null);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(dialogView);
        dialog.setCancelable(true);

        TextView txtContent = dialogView.findViewById(R.id.content);
        txtContent.setText(message);
        AppCompatButton btnClose = dialogView.findViewById(R.id.bt_close);
        btnClose.setText("Tutup");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                buttonAccept.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.GONE);

                Drawable img = null;
                switch (status) {
                    case 1:
                        img = ctx.getResources().getDrawable(R.drawable.ic_check_circle);
                        img.setBounds( 0, 0, 60, 60);

                        textViewStatus.setText("Jadwal telah disetujui");
                        textViewStatus.setCompoundDrawables(img, null, null, null);
                        textViewStatus.setTextColor(getResources().getColor(R.color.green_600));
                        hasProcessed = true;
                        break;
                    case 0:
                        img = ctx.getResources().getDrawable(R.drawable.ic_close_circle);
                        img.setBounds( 0, 0, 60, 60);

                        textViewStatus.setText("Jadwal ditolak");
                        textViewStatus.setCompoundDrawables(img, null, null, null);
                        textViewStatus.setTextColor(getResources().getColor(R.color.red_400));
                        hasProcessed = true;
                        break;
                }

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
