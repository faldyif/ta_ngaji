package com.preklit.ngaji.adapter;

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

public class ListEventStudentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Event> items = new ArrayList<>();

    private Context ctx;
    private ListEventStudentAdapter.OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, Event obj, int position);
    }

    public void setOnItemClickListener(final ListEventStudentAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ListEventStudentAdapter(Context context, List<Event> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView time_info;
        public TextView date_info;
        public TextView status;
        public View lyt_parent;
        public MaterialRippleLayout parentLayout;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            time_info = (TextView) v.findViewById(R.id.time_info);
            date_info = (TextView) v.findViewById(R.id.date_info);
            status = (TextView) v.findViewById(R.id.status);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            parentLayout = (MaterialRippleLayout) v.findViewById(R.id.parent_layout);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_event, parent, false);
        vh = new ListEventStudentAdapter.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof ListEventStudentAdapter.OriginalViewHolder) {
            ListEventStudentAdapter.OriginalViewHolder view = (ListEventStudentAdapter.OriginalViewHolder) holder;

            Event p = items.get(position);

            // Set name
            view.name.setText(p.getTeacher().getName());
            view.name.setCompoundDrawables(Tools.getDrawableTeacherRank(ctx, p.getTeacherRank()), null, null, null);
            view.name.setCompoundDrawablePadding(5);
            // Set image
            Tools.displayImageRoundFromUrl(ctx, view.image, p.getTeacher().getProfilePicUrl());
            // Set status
            view.status.setText(p.getStatus().toUpperCase());
            switch (p.getStatus()) {
                case "accepted":
                    view.status.setTextColor(ctx.getResources().getColor(R.color.green_600));
                    break;
                case "rejected":
                    view.status.setTextColor(ctx.getResources().getColor(R.color.red_400));
                    break;
                case "pending":
                    view.status.setTextColor(ctx.getResources().getColor(R.color.grey_600));
                    break;
            }
            // Set time info
            Date dateStart = Tools.convertDateTimeMySQLStringToJavaDate(p.getStartTime());
            Date dateEnd = Tools.convertDateTimeMySQLStringToJavaDate(p.getEndTime());
            SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, dd MMMM yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

            view.time_info.setText(sdfTime.format(dateStart) + " - " + sdfTime.format(dateEnd));
            view.date_info.setText(sdfDate.format(dateStart));

            // Colorify teacher ranks
            if(p.getTeacherRank() == 3) {
                view.parentLayout.setBackgroundColor(ctx.getResources().getColor(R.color.medal_gold));
            } else if (p.getTeacherRank() == 2) {
                view.parentLayout.setBackgroundColor(ctx.getResources().getColor(R.color.medal_silver));
            }

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
