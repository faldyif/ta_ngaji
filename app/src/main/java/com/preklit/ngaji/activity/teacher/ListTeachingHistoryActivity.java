package com.preklit.ngaji.activity.teacher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.activity.LoginActivity;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.entities.EventsResponse;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;
import com.preklit.ngaji.utils.Tools;
import com.preklit.ngaji.utils.ViewAnimation;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTeachingHistoryActivity extends AppCompatActivity {

    private View parent_view;
    private static final String TAG = ListTeachingHistoryActivity.class.getSimpleName();

    @BindView(R.id.parent_layout_teaching)
    LinearLayout linearLayoutTeachingList;
    @BindView(R.id.progress_bar_teaching)
    ProgressBar progressBarTeaching;

    private NestedScrollView nested_scroll_view;
    private ImageButton bt_toggle_teaching;
    private Button bt_hide_teaching;
    private View lyt_expand_teaching;
    private ApiService service;
    private List<Event> arrEventTeaching;
    private Context ctx;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_teaching_history);
        parent_view = findViewById(android.R.id.content);
        ctx = this;
        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(ListTeachingHistoryActivity.this, LoginActivity.class));
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
        getSupportActionBar().setTitle("Riwayat Pembelajaran");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {

        // teaching section
        bt_toggle_teaching = findViewById(R.id.bt_toggle_teaching);
        bt_hide_teaching = findViewById(R.id.bt_hide_teaching);
        lyt_expand_teaching = findViewById(R.id.lyt_expand_teaching);
        lyt_expand_teaching.setVisibility(View.GONE);

        bt_toggle_teaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionTeaching(bt_toggle_teaching);
            }
        });

        bt_hide_teaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionTeaching(bt_toggle_teaching);
            }
        });

        // nested scrollview
        nested_scroll_view = findViewById(R.id.nested_scroll_view);

        getListHistoryTeaching();
    }

    private void toggleSectionTeaching(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimation.expand(lyt_expand_teaching, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, lyt_expand_teaching);
                }
            });
        } else {
            ViewAnimation.collapse(lyt_expand_teaching);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
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


    void getListHistoryTeaching(){
        Call<EventsResponse> call = service.listTeachingHistory();
        call.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    progressBarTeaching.setVisibility(View.GONE);
                    Log.w(TAG, "onResponse: " + response.body().getData());
                    arrEventTeaching = response.body().getData();

                    for (final Event event: arrEventTeaching) {
                        LayoutInflater inflater = LayoutInflater.from(ctx);
                        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.lyt_card_student_history, null, false);

                        CircularImageView imageView = layout.findViewById(R.id.photo);
                        TextView textViewName = layout.findViewById(R.id.name);
                        TextView textViewTimeInfo = layout.findViewById(R.id.time_info);
                        TextView textViewNote = layout.findViewById(R.id.note);
                        AppCompatRatingBar ratingBar = layout.findViewById(R.id.rating_bar);

                        Date dateEnd = Tools.convertDateTimeMySQLStringToJavaDate(event.getEndTime());

                        // TODO: Perbaiki time ago bahasanya belum bisa bahasa indonesia
                        Locale localeBylanguageTag = new Locale("in");
                        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(localeBylanguageTag).build();
                        String textTimeAgo = TimeAgo.using(dateEnd.getTime(), messages);
                        textViewTimeInfo.setText(textTimeAgo);

                        Tools.displayImageRoundFromUrl(ctx, imageView, event.getStudent().getProfilePicUrl());
                        textViewName.setText(event.getStudent().getName());
                        textViewNote.setText(event.getPresence().getNoteToTeacher());
                        ratingBar.setRating(event.getPresence().getRatingToTeacher());

                        linearLayoutTeachingList.addView(layout);
                        Log.d(TAG, "onResponse: " + "layout added");
                    }
                }else {
                    Toast.makeText(ListTeachingHistoryActivity.this, "Kok gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }
}
