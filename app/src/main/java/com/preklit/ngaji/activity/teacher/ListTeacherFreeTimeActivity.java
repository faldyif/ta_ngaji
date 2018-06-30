package com.preklit.ngaji.activity.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.preklit.ngaji.activity.ListEventSearchActivity;
import com.preklit.ngaji.activity.LoginActivity;
import com.preklit.ngaji.adapter.teacher.ListTeacherFreeTimeAdapter;
import com.preklit.ngaji.entities.TeacherFreeTime;
import com.preklit.ngaji.entities.TeacherFreeTimeResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.ItemAnimation;
import com.preklit.ngaji.utils.ViewAnimation;
import com.preklit.ngaji.widget.LineItemDecoration;
import com.preklit.ngaji.widget.RecyclerViewEmptySupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTeacherFreeTimeActivity extends AppCompatActivity {
    private View parent_view;

    private static final String TAG = ListEventSearchActivity.class.getSimpleName();
    private RecyclerViewEmptySupport recyclerView;
    private ListTeacherFreeTimeAdapter mAdapter;
    private List<TeacherFreeTime> items = new ArrayList<>();
    private int animation_type = ItemAnimation.BOTTOM_UP;
    private Call<TeacherFreeTimeResponse> call;
    private TokenManager tokenManager;
    private ApiService service;
    private RelativeLayout noItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_teacher_free_time);
        parent_view = findViewById(android.R.id.content);

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daftar Waktu Luang Saya");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        noItem = findViewById(R.id.no_result);
        noItem.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
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
        final LinearLayout lyt_progress = findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
        recyclerView.setVisibility(View.GONE);

        call = service.indexTeacherFreeTime();
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

    @OnClick(R.id.fab_add)
    void fabAdd() {
        Intent intent = new Intent(ListTeacherFreeTimeActivity.this, AddNewTeacherFreeTimeActivity.class);
        startActivity(intent);
    }
}
