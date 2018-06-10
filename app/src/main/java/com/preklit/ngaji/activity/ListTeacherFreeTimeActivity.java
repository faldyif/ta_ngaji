package com.preklit.ngaji.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.adapter.ListTeacherFreeTimeAdapter;
import com.preklit.ngaji.entities.TeacherFreeTime;
import com.preklit.ngaji.entities.TeacherFreeTimeResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.ItemAnimation;
import com.preklit.ngaji.utils.Tools;
import com.preklit.ngaji.utils.ViewAnimation;
import com.preklit.ngaji.widget.LineItemDecoration;
import com.preklit.ngaji.widget.RecyclerViewEmptySupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTeacherFreeTimeActivity extends AppCompatActivity {
    private View parent_view;

    private static final String TAG = ListTeacherFreeTimeActivity.class.getSimpleName();
    private RecyclerViewEmptySupport recyclerView;
    private ListTeacherFreeTimeAdapter mAdapter;
    private List<TeacherFreeTime> items = new ArrayList<>();
    private int animation_type = ItemAnimation.BOTTOM_UP;
    private Call<TeacherFreeTimeResponse> call;
    private TokenManager tokenManager;
    private ApiService service;
    private RelativeLayout noItem;

    private double latitude;
    private double longitude;
    private Date dateStart;
    private Date dateEnd;
    private String eventType;
    private Gson gson;
    private Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_free_time_teacher);
        parent_view = findViewById(android.R.id.content);

        gson = new Gson();

        // Get values from previous intent
        myIntent = getIntent();
        Log.w(TAG, "onCreate: " + myIntent.getStringExtra("location_details"));
        latitude = myIntent.getDoubleExtra("latitude", 0);
        longitude = myIntent.getDoubleExtra("longitude", 0);
        dateStart = gson.fromJson(myIntent.getStringExtra("start_time"), Date.class);
        dateEnd = gson.fromJson(myIntent.getStringExtra("end_time"), Date.class);
        eventType = myIntent.getStringExtra("event_type");

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(ListTeacherFreeTimeActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daftar Guru");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        noItem = (RelativeLayout) findViewById(R.id.no_result);
        noItem.setVisibility(View.GONE);

        recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));

//        items = DataGenerator.getPeopleData(this);
//        items.addAll(DataGenerator.getPeopleData(this));
//        items.addAll(DataGenerator.getPeopleData(this));
//        items.addAll(DataGenerator.getPeopleData(this));
//        items.addAll(DataGenerator.getPeopleData(this));

        animation_type = ItemAnimation.FADE_IN;
        getTeacherFreeTimeData();

//        showSingleChoiceDialog();
    }

    private void setAdapter() {
        //set data and list adapter
        mAdapter = new ListTeacherFreeTimeAdapter(this, items, animation_type);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new ListTeacherFreeTimeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, TeacherFreeTime obj, int position) {
                Snackbar.make(parent_view, "Item " + obj.getTeacher().getName() + " clicked", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(ListTeacherFreeTimeActivity.this, DetailTeacherFreeTimeActivity.class);
                intent.putExtra("free_time_details", gson.toJson(obj));
                intent.putExtra("latitude_choosen", latitude);
                intent.putExtra("longitude_choosen", longitude);
                intent.putExtra("event_type", eventType);
                intent.putExtra("time_start", myIntent.getStringExtra("start_time"));
                intent.putExtra("time_end", myIntent.getStringExtra("end_time"));
                intent.putExtra("location_details", myIntent.getStringExtra("location_details"));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_teacher_free_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_refresh:
                items = new ArrayList<>();
                getTeacherFreeTimeData();
                break;
//            case R.id.action_mode:
//                showSingleChoiceDialog();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final String[] ANIMATION_TYPE = new String[]{
            "Bottom Up", "Fade In", "Left to Right", "Right to Left"
    };

//    private void showSingleChoiceDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Animation Type");
//        builder.setCancelable(false);
//        builder.setSingleChoiceItems(ANIMATION_TYPE, -1, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                String selected = ANIMATION_TYPE[i];
//                if (selected.equalsIgnoreCase("Bottom Up")) {
//                    animation_type = ItemAnimation.BOTTOM_UP;
//                } else if (selected.equalsIgnoreCase("Fade In")) {
//                    animation_type = ItemAnimation.FADE_IN;
//                } else if (selected.equalsIgnoreCase("Left to Right")) {
//                    animation_type = ItemAnimation.LEFT_RIGHT;
//                } else if (selected.equalsIgnoreCase("Right to Left")) {
//                    animation_type = ItemAnimation.RIGHT_LEFT;
//                }
//                getSupportActionBar().setTitle(selected);
//                setAdapter();
//                dialogInterface.dismiss();
//            }
//        });
//        builder.show();
//    }

    void getTeacherFreeTimeData(){
        final LinearLayout lyt_progress = (LinearLayout) findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
        recyclerView.setVisibility(View.GONE);

        call = service.listEvents(latitude, longitude, Tools.convertDateToDateTimeMySQL(dateStart), Tools.convertDateToDateTimeMySQL(dateEnd), eventType);
        call.enqueue(new Callback<TeacherFreeTimeResponse>() {
            @Override
            public void onResponse(Call<TeacherFreeTimeResponse> call, Response<TeacherFreeTimeResponse> response) {
                Log.w(TAG, "onResponse: " + response );
                ViewAnimation.fadeOut(lyt_progress);
                recyclerView.setVisibility(View.VISIBLE);

                if(response.isSuccessful()){
                    items = response.body().getData();
                    Log.w(TAG, "onResponse: " + new Gson().toJson(items));
                    setAdapter();

                    if(items.size() == 0) {
                        ViewAnimation.fadeIn(noItem);
                    }
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(ListTeacherFreeTimeActivity.this, LoginActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<TeacherFreeTimeResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }
}
