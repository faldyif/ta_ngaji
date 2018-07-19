package com.preklit.ngaji.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
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

public class ReviewEventModificationRequestActivity extends AppCompatActivity {

    private static final String TAG = ReviewEventModificationRequestActivity.class.getSimpleName();

    @BindView(R.id.start_date)
    TextView textViewStartDate;
    @BindView(R.id.time_info)
    TextView textViewTimeInfo;

    @BindView(R.id.start_date_after)
    TextView textViewStartDateAfter;
    @BindView(R.id.time_info_after)
    TextView textViewTimeInfoAfter;

    @BindView(R.id.btn_accept)
    Button buttonAccept;
    @BindView(R.id.btn_cancel)
    Button buttonCancel;

    SupportMapFragment mapFragmentBefore;
    SupportMapFragment mapFragmentAfter;
    Event event;
    GoogleMap mMapBefore;
    GoogleMap mMapAfter;
    Gson gson;
    Context ctx;
    private ProgressDialog progressDialog;

    Date dateStart;
    Date dateEnd;
    Date dateStartAfter;
    Date dateEndAfter;
    String eventType;
    Boolean hasProcessed;

    private Call<CreateResponse> call;
    private TokenManager tokenManager;
    private ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_event);
        ButterKnife.bind(this);

        // Get passed variable from activity berfore
        hasProcessed = false;
        ctx = getApplicationContext();
        gson = new Gson();
        Intent intent = getIntent();
        event = gson.fromJson(intent.getStringExtra("event"), Event.class);

        progressDialog = new ProgressDialog(this);
        initToolbar();
        initComponent();

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(ReviewEventModificationRequestActivity.this, LoginActivity.class));
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
        setResult(Activity.RESULT_OK, intent);
        Log.w(TAG, "onBackPressed: Nih");
        finish();
    }

    private void initComponent() {
        // Initialize date formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("in", "id-ID"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        // Converting to local variable to easy modification
        dateStart = Tools.convertDateTimeMySQLStringToJavaDate(event.getStartTime());
        dateEnd = Tools.convertDateTimeMySQLStringToJavaDate(event.getEndTime());
        dateStartAfter = Tools.convertDateTimeMySQLStringToJavaDate(event.getEventModificationRequest().getStartTime());
        dateEndAfter = Tools.convertDateTimeMySQLStringToJavaDate(event.getEventModificationRequest().getEndTime());

        // Inserting values to components
        textViewStartDate.setText(dateFormat.format(dateStart));
        textViewTimeInfo.setText(timeFormat.format(dateStart) + " - " + timeFormat.format(dateEnd));

        textViewStartDateAfter.setText(dateFormat.format(dateStartAfter));
        textViewTimeInfoAfter.setText(timeFormat.format(dateStartAfter) + " - " + timeFormat.format(dateEndAfter));

        Log.w(TAG, "initComponent: " + event.getStatus());

        mapFragmentBefore = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_before);
        mapFragmentAfter = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_after);
        mapFragmentBefore.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMapBefore = googleMap;

                // Add a marker in Sydney and move the camera
                LatLng tempatNgaji = new LatLng(event.getLatitude(), event.getLongitude());
                Marker marker2 = mMapBefore.addMarker(new MarkerOptions().position(tempatNgaji).title("Lokasi ngaji lama"));
                marker2.showInfoWindow();
                mMapBefore.moveCamera(CameraUpdateFactory.newLatLng(tempatNgaji));
                mMapBefore.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);
            }
        });

        if(event.getEventModificationRequest() != null && event.getEventModificationRequest().getLatitude() != null) {

            mapFragmentAfter.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMapAfter = googleMap;

                    // Add a marker in Sydney and move the camera
                    LatLng tempatNgaji = new LatLng(event.getEventModificationRequest().getLatitude(), event.getEventModificationRequest().getLongitude());
                    Marker marker2 = mMapAfter.addMarker(new MarkerOptions().position(tempatNgaji).title("Lokasi ngaji baru"));
                    marker2.showInfoWindow();
                    mMapAfter.moveCamera(CameraUpdateFactory.newLatLng(tempatNgaji));
                    mMapAfter.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);
                }
            });

        } else {
            mapFragmentAfter.getView().setVisibility(View.GONE);
        }
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

    @OnClick(R.id.btn_cancel)
    void clickCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi jadwal");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setMessage("Apakah anda akan menolak jadwal ini? Berikan alasannya!");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String reason = input.getText().toString();
                respondUpdateEventStatus(0, reason);
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
                respondUpdateEventStatus(1, null);
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    void respondUpdateEventStatus(final Integer status, String reason) {
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();

        call = service.respondUpdateEvent(event.getId(), status, reason);
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
                    startActivity(new Intent(ReviewEventModificationRequestActivity.this, LoginActivity.class));
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
