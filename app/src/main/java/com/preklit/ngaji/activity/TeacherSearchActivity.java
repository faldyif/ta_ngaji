package com.preklit.ngaji.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.preklit.ngaji.R;
import com.preklit.ngaji.utils.Tools;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeacherSearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TeacherSearchActivity";
    private View parent_view;
    private static final int PLACE_PICKER_REQUEST = 600;

    public static Place choosenPlace;
    public static Long choosenDateMillis;
    public Integer choosenHour;
    public Integer choosenMinute;
    public static String durationString;
    public static Integer studyDuration;
    int[] durationIntegerArray;

    @BindView(R.id.tv_date_study)
    TextView textViewDateStudy;
    @BindView(R.id.tv_destination)
    TextView textViewDestination;
    @BindView(R.id.tv_time_study)
    TextView textViewTimeStudy;
    @BindView(R.id.spinner_duration_study)
    Spinner spinnerDurationStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_search);
        parent_view = findViewById(android.R.id.content);
        ButterKnife.bind(this);

        durationIntegerArray = getResources().getIntArray(R.array.duration_integer_array);
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_90);
    }

    private void initComponent() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.duration_string_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerDurationStudy.setAdapter(adapter);
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
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        choosenDateMillis = calendar.getTimeInMillis();
                        textViewDateStudy.setText(Tools.getFormattedDateSimple(choosenDateMillis));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }

    /**
     * Dialog for time picker
     */
    private void dialogTimePickerLight() {
        Calendar cur_calender = Calendar.getInstance();
        TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                choosenHour = hourOfDay;
                choosenMinute = minute;
                NumberFormat f = new DecimalFormat("00");
                textViewTimeStudy.setText(f.format(choosenHour) + " : " + f.format(choosenMinute));
            }
        }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), true);
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                choosenPlace = PlacePicker.getPlace(this, data);
                ((TextView) findViewById(R.id.tv_destination)).setText(choosenPlace.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Snackbar.make(parent_view, status.toString(), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.tv_destination)
    void clickTextViewDestination() {
        openPlacePickerActivity(PLACE_PICKER_REQUEST);
    }

    @OnClick(R.id.tv_date_study)
    void clickTextViewDateStudy() {
        dialogDatePickerLight();
    }

    @OnClick(R.id.tv_time_study)
    void clickTextViewTimeStudy() {
        dialogTimePickerLight();
    }

    @OnClick(R.id.bt_search)
    void searchTeacher() {
        Toast.makeText(this, textViewDateStudy.getText(), Toast.LENGTH_SHORT).show();
        submitSearch();
    }

    void submitSearch() {
        textViewTimeStudy.setError(null);
        textViewDestination.setError(null);
        textViewDateStudy.setError(null);

        boolean validated = choosenPlace != null && choosenDateMillis != null && choosenHour != null && choosenMinute != null;

        if(choosenPlace == null) textViewDestination.setError("Bidang isian tempat belajar wajib diisi.");
        if(choosenDateMillis == null) textViewDateStudy.setError("Bidang isian tanggal belajar wajib diisi.");
        if(choosenHour == null) textViewTimeStudy.setError("Bidang isian waktu belajar wajib diisi.");

        if(validated) {
            Toast.makeText(this, "Validasi berhasil!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        studyDuration = durationIntegerArray[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
