package com.preklit.ngaji.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.activity.DetailEventStudentActivity;
import com.preklit.ngaji.activity.LoginActivity;
import com.preklit.ngaji.adapter.ListEventStudentAdapter;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.entities.EventsResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.ItemAnimation;
import com.preklit.ngaji.utils.ViewAnimation;
import com.preklit.ngaji.widget.LineItemDecoration;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListEventStudentFragment extends Fragment {

    private static final String TAG = ListEventStudentFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private LinearLayout lyt_progress;
    private ListEventStudentAdapter mAdapter;
    private Context context;
    private View parent_view;
    private int animation_type;
    private Call<EventsResponse> call;
    private ApiService service;
    List<Event> items;
    private TokenManager tokenManager;
    private RelativeLayout noItem;
    private String status;
    Gson gson;

    public ListEventStudentFragment() {
        // Required empty public constructor
    }

    public static ListEventStudentFragment newInstance(Context context, String status) {
        ListEventStudentFragment fragment = new ListEventStudentFragment();
        fragment.context = context;
        fragment.status = status;
        fragment.gson = new Gson();

        fragment.tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        fragment.service = RetrofitBuilder.createServiceWithAuth(ApiService.class, fragment.tokenManager);

        // do some magic
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(tokenManager.getToken() == null){
            startActivity(new Intent(context, LoginActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_event_for_student, container, false);

        parent_view = getActivity().findViewById(android.R.id.content);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent(view);
    }

    private void initComponent(View view) {
        noItem = (RelativeLayout) view.findViewById(R.id.no_result);
        noItem.setVisibility(View.GONE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LineItemDecoration(context, LinearLayout.VERTICAL));

        lyt_progress = view.findViewById(R.id.lyt_progress);

        animation_type = ItemAnimation.FADE_IN;

        items = Collections.emptyList();
        getEventStudentData();

    }


    void getEventStudentData() {
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
        recyclerView.setVisibility(View.GONE);

        call = service.listHistoryStudentEvent(status);
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
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

    }


    private void setAdapter() {
        //set data and list adapter
        mAdapter = new ListEventStudentAdapter(context, items, animation_type);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
//        mAdapter.setOnItemClickListener(new ListTeacherFreeTimeAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, TeacherFreeTime obj, int position) {
//                Snackbar.make(parent_view, "Item " + obj.getTeacher().getName() + " clicked", Snackbar.LENGTH_SHORT).show();
//                Intent intent = new Intent(ListEventSearchActivity.this, DetailTeacherFreeTimeActivity.class);
//                intent.putExtra("free_time_details", gson.toJson(obj));
//                intent.putExtra("latitude_choosen", latitude);
//                intent.putExtra("longitude_choosen", longitude);
//                intent.putExtra("event_type", eventType);
//                intent.putExtra("time_start", myIntent.getStringExtra("start_time"));
//                intent.putExtra("time_end", myIntent.getStringExtra("end_time"));
//                intent.putExtra("location_details", myIntent.getStringExtra("location_details"));
//                startActivity(intent);
//            }
//        });

        // on item clicked
        mAdapter.setOnItemClickListener(new ListEventStudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Event obj, int position) {
                Intent intent = new Intent(context, DetailEventStudentActivity.class);
                intent.putExtra("event_detail", gson.toJson(obj));
                startActivity(intent);
            }
        });
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
