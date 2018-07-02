package com.preklit.ngaji.activity.teacher;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.activity.DetailTeacherFreeTimeActivity;
import com.preklit.ngaji.activity.ListEventSearchActivity;
import com.preklit.ngaji.activity.LoginActivity;
import com.preklit.ngaji.activity.MainActivity;
import com.preklit.ngaji.entities.CreateResponse;
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

public class AddNewTeacherFreeTimeActivity extends AppCompatActivity {

    private static final String TAG = "TeacherSearchActivity";
    private View parent_view;
    private static final int PLACE_PICKER_REQUEST = 600;

    public static Place choosenPlace;
    public static Long choosenDateMillis;
    public Integer choosenHourStart;
    public Integer choosenMinuteStart;
    public Integer choosenHourEnd;
    public Integer choosenMinuteEnd;
    int[] durationIntegerArray;
    private LatLngBounds latLngBounds;
    private double longitude;
    private double latitude;
    private String type;

    @BindView(R.id.tv_date)
    TextView textViewDate;
    @BindView(R.id.tv_destination)
    TextView textViewDestination;
    @BindView(R.id.tv_time_start)
    TextView textViewTimeStart;
    @BindView(R.id.tv_time_end)
    TextView textViewTimeEnd;

    private Call<CreateResponse> call;
    private TokenManager tokenManager;
    private ApiService service;
    private Date dateStart;
    private Date dateEnd;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher_free_time);
        parent_view = findViewById(android.R.id.content);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        durationIntegerArray = getResources().getIntArray(R.array.duration_integer_array);
        initToolbar();
        initComponent();

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(AddNewTeacherFreeTimeActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_90);
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
                        calendar.set(Calendar.HOUR, 0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                choosenPlace = PlacePicker.getPlace(this, data);
                Log.w(TAG, "onActivityResult: " + choosenPlace.getPlaceTypes() + " --- " + choosenPlace.getAddress());
                latLngBounds = choosenPlace.getViewport();
                if(latLngBounds == null) {
                    Snackbar.make(parent_view, "Tempat tidak lazim", Snackbar.LENGTH_SHORT).show();
                    choosenPlace = null;
                    return;
                }
                latitude = latLngBounds.getCenter().latitude;
                longitude = latLngBounds.getCenter().longitude;

                // Test reverse geocoding
                List<Address> addresses = doReverseGeocoding(latitude, longitude);
                Log.w(TAG, "onActivityResultAddress: " + addresses);
                String featureName = "";
                if(choosenPlace.getPlaceTypes().get(0) == 0) {
                    Log.w(TAG, "onActivityResult: " + "Bukan tempat mbok");
                    featureName = addresses.get(0).getFeatureName();
                } else {
                    featureName += choosenPlace.getName();
                }
                String combinedAddress = featureName + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getSubAdminArea();

//                ((TextView) findViewById(R.id.tv_destination)).setText(choosenPlace.getName());
                textViewDestination.setText(combinedAddress);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Snackbar.make(parent_view, status.toString(), Snackbar.LENGTH_SHORT).show();
            }
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

    @OnClick(R.id.tv_destination)
    void clickTextViewDestination() {
        openPlacePickerActivity(PLACE_PICKER_REQUEST);
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

    @OnClick(R.id.bt_search)
    void searchTeacher() {
//        Toast.makeText(this, textViewDateStudy.getText(), Toast.LENGTH_SHORT).show();
        submitSearch();
    }

    void submitSearch() {
        textViewDate.setError(null);
        textViewTimeEnd.setError(null);
        textViewDestination.setError(null);
        textViewTimeStart.setError(null);

        boolean validated = choosenPlace != null && choosenDateMillis != null && choosenHourStart != null && choosenMinuteStart != null && choosenHourEnd != null && choosenMinuteEnd != null;

        if(choosenPlace == null) textViewDestination.setError("Bidang isian tempat acuan wajib diisi.");
        if(choosenDateMillis == null) textViewDate.setError("Bidang isian tanggal wajib diisi.");
        if(choosenHourStart == null) textViewTimeStart.setError("Bidang isian waktu mulai wajib diisi.");
        if(choosenHourEnd == null) textViewTimeEnd.setError("Bidang isian waktu selesai wajib diisi.");

        if(validated) {
//            Toast.makeText(this, "Validasi berhasil!", Toast.LENGTH_SHORT).show();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(choosenDateMillis);
            calendar.add(Calendar.HOUR_OF_DAY, choosenHourStart);
            calendar.add(Calendar.MINUTE, choosenMinuteStart);
            dateStart = calendar.getTime();
            calendar.setTimeInMillis(choosenDateMillis);
            calendar.add(Calendar.HOUR_OF_DAY, choosenHourEnd);
            calendar.add(Calendar.MINUTE, choosenMinuteEnd);
            dateEnd = calendar.getTime();

            sendRequest();
        }
    }

    void sendRequest() {
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();

        call = service.createTeacherFreeTime(latitude, longitude, Tools.convertDateToDateTimeMySQL(dateStart), Tools.convertDateToDateTimeMySQL(dateEnd), String.valueOf(textViewDestination.getText()));
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
                    startActivity(new Intent(AddNewTeacherFreeTimeActivity.this, LoginActivity.class));
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

                Intent intent = new Intent(AddNewTeacherFreeTimeActivity.this, MainActivity.class);
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
