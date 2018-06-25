package com.preklit.ngaji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.preklit.ngaji.R;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.utils.ItemAnimation;
import com.preklit.ngaji.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by faldyikhwanfadila on 05/06/18.
 */

public class ListTimelineEventStudentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Event> items = new ArrayList<>();

    private Context ctx;
    private ListTimelineEventStudentAdapter.OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, Event obj, int position);
    }

    public void setOnItemClickListener(final ListTimelineEventStudentAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ListTimelineEventStudentAdapter(Context context, List<Event> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView eventType;
        public TextView teacherName;
        public TextView timestamp;
        public TextView description;
        public CircularImageView circularImageView;
        public View lyt_parent;
        public LinearLayout parentLayout;

        public OriginalViewHolder(View v) {
            super(v);

            circularImageView = v.findViewById(R.id.photo);
            description = v.findViewById(R.id.description);
            eventType = v.findViewById(R.id.event_type);
            teacherName = v.findViewById(R.id.teacher_name);
            timestamp = v.findViewById(R.id.timestamp);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            parentLayout = v.findViewById(R.id.parent_layout);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_timeline, parent, false);
        vh = new ListTimelineEventStudentAdapter.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof ListTimelineEventStudentAdapter.OriginalViewHolder) {
            ListTimelineEventStudentAdapter.OriginalViewHolder view = (ListTimelineEventStudentAdapter.OriginalViewHolder) holder;

            Event p = items.get(position);

            // Set name
            view.teacherName.setText(p.getTeacher().getName());
            // Set image
            Tools.displayImageRoundFromUrl(ctx, view.circularImageView, p.getTeacher().getProfilePicUrl());
            // Set time info
            Date dateStart = Tools.convertDateTimeMySQLStringToJavaDate(p.getStartTime());
            Date dateEnd = Tools.convertDateTimeMySQLStringToJavaDate(p.getEndTime());

            // TODO: Perbaiki time ago bahasanya belum bisa bahasa indonesia
            Locale localeBylanguageTag = new Locale("in");
            TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(localeBylanguageTag).build();

            String textTimeAgo = TimeAgo.using(dateStart.getTime(), messages);
            view.timestamp.setText(textTimeAgo);
            // Set lokasi
            String textLokasi = "Lokasi: " + p.getShortPlaceName() + "\n" + "Keterangan Lokasi: " + p.getLocationDetails();
            view.description.setText(textLokasi);

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
            setAnimation(view.itemView, position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}
