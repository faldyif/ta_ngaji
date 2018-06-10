package com.preklit.ngaji.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.preklit.ngaji.R;
import com.preklit.ngaji.entities.TeacherFreeTime;
import com.preklit.ngaji.utils.ItemAnimation;
import com.preklit.ngaji.utils.Tools;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by faldyikhwanfadila on 29/05/18.
 */

public class ListTeacherFreeTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TeacherFreeTime> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, TeacherFreeTime obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ListTeacherFreeTimeAdapter(Context context, List<TeacherFreeTime> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView distance;
        public TextView points;
        public View lyt_parent;
        public AppCompatRatingBar ratingBar;
        public MaterialRippleLayout parentLayout;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            distance = (TextView) v.findViewById(R.id.distance);
            points = (TextView) v.findViewById(R.id.points);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            ratingBar = (AppCompatRatingBar) v.findViewById(R.id.rating);
            parentLayout = (MaterialRippleLayout) v.findViewById(R.id.parent_layout);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_free_time, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            TeacherFreeTime p = items.get(position);
            String jarak = "";

            Locale currentLocale = new Locale("in", "ID");
            NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
            String pattern = "###,###.##";
            DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter; // The one from above
            decimalFormatter.applyPattern(pattern);

            if(p.getDistance() < 1) {
                String formattedValue = decimalFormatter.format(p.getDistance() * 1000);
                jarak = formattedValue + " m";
            } else {
                String formattedValue = decimalFormatter.format(p.getDistance());
                jarak = formattedValue + " km";
            }
            jarak += " dari lokasi anda";
            view.name.setText(p.getTeacher().getName());

            view.name.setCompoundDrawables(Tools.getDrawableTeacherRank(ctx, p.getTeacherRank()), null, null, null);
            view.name.setCompoundDrawablePadding(5);
            view.distance.setText(jarak);

            view.points.setText(p.getPoints() + " point");
            if(p.getPoints() < 0) {
                view.points.setTextColor(ctx.getResources().getColor(R.color.red_400));
            } else {
                view.points.setTextColor(ctx.getResources().getColor(R.color.green_400));
            }
            Tools.displayImageRoundFromUrl(ctx, view.image, p.getTeacher().getProfilePicUrl());

            // goldify gold teacher
            if(p.getTeacherRank() == 3) {
                view.parentLayout.setBackgroundColor(ctx.getResources().getColor(R.color.medal_gold));
                view.distance.setTextColor(ctx.getResources().getColor(android.R.color.white));
            } else if (p.getTeacherRank() == 2) {
                view.parentLayout.setBackgroundColor(ctx.getResources().getColor(R.color.medal_silver));
                view.distance.setTextColor(ctx.getResources().getColor(android.R.color.black));
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
