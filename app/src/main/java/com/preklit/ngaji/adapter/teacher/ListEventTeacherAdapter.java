package com.preklit.ngaji.adapter.teacher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.preklit.ngaji.R;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.utils.ItemAnimation;
import com.preklit.ngaji.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by faldyikhwanfadila on 05/06/18.
 */

public class ListEventTeacherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Event> items = new ArrayList<>();

    private Context ctx;
    private ListEventTeacherAdapter.OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, Event obj, int position);
    }

    public void setOnItemClickListener(final ListEventTeacherAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ListEventTeacherAdapter(Context context, List<Event> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView event_type;
        public TextView place_info;
        public TextView date_info;
        public View lyt_parent;
        public MaterialRippleLayout parentLayout;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            event_type = v.findViewById(R.id.event_type);
            place_info = v.findViewById(R.id.place_info);
            date_info = v.findViewById(R.id.date_info);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            parentLayout = v.findViewById(R.id.parent_layout);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_event, parent, false);
        vh = new ListEventTeacherAdapter.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof ListEventTeacherAdapter.OriginalViewHolder) {
            ListEventTeacherAdapter.OriginalViewHolder view = (ListEventTeacherAdapter.OriginalViewHolder) holder;

            Event p = items.get(position);

            // Set name
            view.name.setText(p.getStudent().getName());
            // Set image
            Tools.displayImageRoundFromUrl(ctx, view.image, p.getStudent().getProfilePicUrl());
            // Set time info
            Date dateStart = Tools.convertDateTimeMySQLStringToJavaDate(p.getStartTime());
            Date dateEnd = Tools.convertDateTimeMySQLStringToJavaDate(p.getEndTime());
            SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, dd MMMM yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

            view.place_info.setText(p.getShortPlaceName());
            view.event_type.setText(p.getEventType().toUpperCase());
            switch (p.getEventType().toUpperCase()) {
                case "TAHSIN":
                    view.event_type.setTextColor(ctx.getResources().getColor(R.color.indigo_300));
                    break;
                case "TAHFIDZ":
                    view.event_type.setTextColor(ctx.getResources().getColor(R.color.green_500));
                    break;
                case "TADABBUR":
                    view.event_type.setTextColor(ctx.getResources().getColor(R.color.orange_300));
                    break;
            }
            view.date_info.setText(sdfDate.format(dateStart) + " (" + sdfTime.format(dateStart) + " - " + sdfTime.format(dateEnd) + ")");

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
