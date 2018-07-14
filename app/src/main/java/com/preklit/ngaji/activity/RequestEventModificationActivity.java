package com.preklit.ngaji.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.entities.CreateResponse;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.Tools;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestEventModificationActivity extends AppCompatActivity {

    private static final String TAG = "RequestEventModi";
    private View parent_view;

    public static Long choosenDateMillis;
    public Integer choosenHourStart;
    public Integer choosenMinuteStart;
    public Integer choosenHourEnd;
    public Integer choosenMinuteEnd;
    int[] durationIntegerArray;
    Event event;
    private String type;
    private String reason;

    @BindView(R.id.tv_date)
    TextView textViewDate;
    @BindView(R.id.tv_time_start)
    TextView textViewTimeStart;
    @BindView(R.id.tv_time_end)
    TextView textViewTimeEnd;
    @BindView(R.id.et_reason)
    EditText editTextReason;

    private Call<CreateResponse> call;
    private TokenManager tokenManager;
    private ApiService service;
    private Date dateStart;
    private Date dateEnd;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_event_modification);
        parent_view = findViewById(android.R.id.content);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        Gson gson = new Gson();
        Intent intent = getIntent();
        event = gson.fromJson(intent.getStringExtra("event"), Event.class);
        dateStart = Tools.convertDateTimeMySQLStringToJavaDate(event.getStartTime());
        dateEnd = Tools.convertDateTimeMySQLStringToJavaDate(event.getEndTime());

        Calendar tempCalStart = Calendar.getInstance();
        Calendar tempCalEnd = Calendar.getInstance();
        Calendar tempCal = Calendar.getInstance();
        tempCalStart.setTime(dateStart);
        tempCalEnd.setTime(dateEnd);
        tempCal.setTime(dateStart);

        tempCal.add(Calendar.HOUR_OF_DAY, -tempCalStart.get(Calendar.HOUR_OF_DAY));
        tempCal.add(Calendar.MINUTE, -tempCalStart.get(Calendar.MINUTE));
        choosenDateMillis = tempCal.getTimeInMillis();
        textViewDate.setText(Tools.getFormattedDateSimple(choosenDateMillis));

        choosenHourStart = tempCalStart.get(Calendar.HOUR_OF_DAY);
        choosenMinuteStart = tempCalStart.get(Calendar.MINUTE);
        NumberFormat f = new DecimalFormat("00");
        textViewTimeStart.setText(f.format(choosenHourStart) + " : " + f.format(choosenMinuteStart));

        choosenHourEnd = tempCalEnd.get(Calendar.HOUR_OF_DAY);
        choosenMinuteEnd = tempCalEnd.get(Calendar.MINUTE);
        textViewTimeEnd.setText(f.format(choosenHourEnd) + " : " + f.format(choosenMinuteEnd));

        Log.w(TAG, "onCreate: " + tempCal);
        Log.w(TAG, "onCreate: " + tempCalStart);
        Log.w(TAG, "onCreate: " + tempCalEnd);

        durationIntegerArray = getResources().getIntArray(R.array.duration_integer_array);
        initToolbar();
        initComponent();

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(RequestEventModificationActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Buat Permintaan Penggantian Jadwal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initComponent() {

    }

    /**
     * AutoComplete Activity for Google Places API location selector
     * @param request_code
     */
    private void openPlacePickerActivity(int request_code) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), request_code);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(), 0).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Snackbar.make(parent_view, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Dialog for date picker
     */
    private void dialogDatePickerLight() {
        Calendar cur_calender = Calendar.getInstance();
        Calendar today_plus_one = Calendar.getInstance();
        today_plus_one.add(Calendar.DAY_OF_MONTH, 1);

        if(choosenDateMillis != null) {
            cur_calender.setTimeInMillis(choosenDateMillis);
        }

        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        choosenDateMillis = calendar.getTimeInMillis();
                        textViewDate.setText(Tools.getFormattedDateSimple(choosenDateMillis));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(today_plus_one);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }

    /**
     * Dialog for time picker
     */
    private void dialogTimePickerLight() {
        if(choosenHourStart == null && choosenMinuteStart == null) {
            Calendar cur_calender = Calendar.getInstance();
            TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                    choosenHourStart = hourOfDay;
                    choosenMinuteStart = minute;
                    NumberFormat f = new DecimalFormat("00");
                    textViewTimeStart.setText(f.format(choosenHourStart) + " : " + f.format(choosenMinuteStart));
                }
            }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), true);
            //set dark light
            datePicker.setThemeDark(false);
            datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
            datePicker.show(getFragmentManager(), "Timepickerdialog");
        } else {
            TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                    choosenHourStart = hourOfDay;
                    choosenMinuteStart = minute;
                    NumberFormat f = new DecimalFormat("00");
                    textViewTimeStart.setText(f.format(choosenHourStart                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ) + " : " + f.format(choosenMinuteStart));
                }
            }, choosenHourStart, choosenMinuteStart, true);
            //set dark light
            datePicker.setThemeDark(false);
            datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
            datePicker.show(getFragmentManager(), "Timepickerdialog");
        }
    }
    private void dialogTimePickerLightEnd() {
        if(choosenHourEnd == null && choosenMinuteEnd == null) {
            Calendar cur_calender = Calendar.getInstance();
            TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                    choosenHourEnd = hourOfDay;
                    choosenMinuteEnd = minute;
                    NumberFormat f = new DecimalFormat("00");
                    textViewTimeEnd.setText(f.format(choosenHourEnd) + " : " + f.format(choosenMinuteEnd));
                }
            }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), true);
            //set dark light
            datePicker.setThemeDark(false);
            datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
            datePicker.show(getFragmentManager(), "Timepickerdialog");
        } else {
            TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                    choosenHourEnd = hourOfDay;
                    choosenMinuteEnd = minute;
                    NumberFormat f = new DecimalFormat("00");
                    textViewTimeEnd.setText(f.format(choosenHourEnd                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ) + " : " + f.format(choosenMinuteStart));
                }
            }, choosenHourEnd, choosenMinuteEnd, true);
            //set dark light
            datePicker.setThemeDark(false);
            datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
            datePicker.show(getFragmentManager(), "Timepickerdialog");
        }
    }

    private List<Address> doReverseGeocoding(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.w(TAG, "onActivityResult: " + addresses);
        } catch (IOException e) {
            return doReverseGeocoding(latitude, longitude);
        }
        return addresses;
    }

    @OnClick(R.id.tv_date)
    void clickTextViewDateStudy() {
        dialogDatePickerLight();
    }

    @OnClick(R.id.tv_time_start)
    void clickTextViewTimeStudy() {
        dialogTimePickerLight();
    }

    @OnClick(R.id.tv_time_end)
    void clickTextViewTimeStudyEnd() {
        dialogTimePickerLightEnd();
    }

    @OnClick(R.id.bt_request)
    void searchTeacher() {
//        Toast.makeText(this, textViewDateStudy.getText(), Toast.LENGTH_SHORT).show();
        submitSearch();
    }

    void submitSearch() {
        reason = String.valueOf(editTextReason.getText());

        textViewDate.setError(null);
        textViewTimeEnd.setError(null);
        textViewTimeStart.setError(null);
        editTextReason.setError(null);

        if(choosenDateMillis == null) textViewDate.setError("Bidang isian tanggal wajib diisi.");
        if(choosenHourStart == null) textViewTimeStart.setError("Bidang isian waktu mulai wajib diisi.");
        if(choosenHourEnd == null) textViewTimeEnd.setError("Bidang isian waktu selesai wajib diisi.");
        if(reason == null || reason.equals("")) editTextReason.setError("Bidang isian alasan harus diisi");

        Log.d(TAG, "submitSearch: " + editTextReason.getText());

        boolean validated = choosenDateMillis != null && choosenHourStart != null && choosenMinuteStart != null && choosenHourEnd != null && choosenMinuteEnd != null && reason != null && !reason.equals("");

        if(validated) {
//            Toast.makeText(this, "Validasi berhasil!", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "submitSearch2: " + choosenHourStart + ":" + choosenMinuteStart + " " + choosenHourEnd + ":" + choosenMinuteEnd);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(choosenDateMillis);
            calendar.add(Calendar.HOUR_OF_DAY, choosenHourStart);
            calendar.add(Calendar.MINUTE, choosenMinuteStart);
            dateStart = calendar.getTime();
            calendar.setTimeInMillis(choosenDateMillis);
            calendar.add(Calendar.HOUR_OF_DAY, choosenHourEnd);
            calendar.add(Calendar.MINUTE, choosenMinuteEnd);
            dateEnd = calendar.getTime();

            Log.w(TAG, "submitSearch: " + dateStart + " " + dateEnd);

            sendRequest();
        }
    }

    void sendRequest() {
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();

        call = service.updateEvent(event.getId(), Tools.convertDateToDateTimeMySQL(dateStart), Tools.convertDateToDateTimeMySQL(dateEnd), String.valueOf(editTextReason.getText()));
        call.enqueue(new Callback<CreateResponse>() {
            @Override
            public void onResponse(Call<CreateResponse> call, Response<CreateResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    if(response.code() == 204) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        showSuccessDialog("Berhasil mengirimkan permintaan penggantian jadwal! Silahkan tunggu sampai " + event.getTeacher().getName() + " meninjau perubahan yang anda berikan!");
                    }
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(RequestEventModificationActivity.this, LoginActivity.class));
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

        TextView txtContent = dialogView.findViewById(R.id.content);
        txtContent.setText(message);
        AppCompatButton btnClose = dialogView.findViewById(R.id.bt_close);
        btnClose.setText("Kembali");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(RequestEventModificationActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
