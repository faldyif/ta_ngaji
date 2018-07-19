package com.preklit.ngaji.activity.teacher;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.activity.LoginActivity;
import com.preklit.ngaji.adapter.teacher.ListEventTeacherAdapter;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.entities.EventsResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.ItemAnimation;
import com.preklit.ngaji.utils.ViewAnimation;
import com.preklit.ngaji.widget.LineItemDecoration;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListPengajuanEventActivity extends AppCompatActivity {

    private static final String TAG = ListPengajuanEventActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private LinearLayout lyt_progress;
    private ListEventTeacherAdapter mAdapter;
    private Context context;
    private int animation_type;
    private Call<EventsResponse> call;
    private ApiService service;
    List<Event> items;
    private TokenManager tokenManager;
    private RelativeLayout noItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_upcoming_event);

        ButterKnife.bind(this);
        context = this;

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(context, LoginActivity.class));
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
        getSupportActionBar().setTitle("Daftar Pengajuan Jadwal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        noItem = findViewById(R.id.no_result);
        noItem.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LineItemDecoration(context, LinearLayout.VERTICAL));

        animation_type = ItemAnimation.FADE_IN;
        lyt_progress = findViewById(R.id.lyt_progress);

        items = Collections.emptyList();
        getUpcomingEventData();
    }


    void getUpcomingEventData() {
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
        recyclerView.setVisibility(View.GONE);

        call = service.listEventUnconfirmed();
        call.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                Log.w(TAG, "onResponse: " + response );
                ViewAnimation.fadeOut(lyt_progress);
                recyclerView.setVisibility(View.VISIBLE);

                if(response.isSuccessful()) {
                    items = response.body().getData();
                    Log.w(TAG, "onResponse: " + new Gson().toJson(items));
                    setAdapter();
                    if(items.size() == 0) {
                        ViewAnimation.fadeIn(noItem);
                    }
                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(context, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdapter() {
        //set data and list adapter
        mAdapter = new ListEventTeacherAdapter(context, items, animation_type);
        recyclerView.setAdapter(mAdapter);

        // on item clicked
        mAdapter.setOnItemClickListener(new ListEventTeacherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Event obj, int position) {
                Intent intent = new Intent(context, DetailEventActivity.class);
                Gson gson = new Gson();
                intent.putExtra("event_detail", gson.toJson(obj));
                intent.putExtra("event_index", position);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            items = Collections.emptyList();
            getUpcomingEventData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
