package com.preklit.ngaji.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.entities.DateSection;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by faldyikhwanfadila on 30/04/18.
 */

public class AdapterListJadwal extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = AdapterListJadwal.class.getSimpleName();
    
    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    private List<Object> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Object obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListJadwal(Context context, List<Object> items) {
        this.items = items;
        ctx = context;

        Gson gson = new Gson();
        Log.d(TAG, "GsonDEBUG AdapterListJadwal: " + gson.toJson(items));
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewLocation;
        public TextView textViewStartTime;
        public TextView textViewEndTime;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            textViewTitle = (TextView) v.findViewById(R.id.text_title);
            textViewLocation = (TextView) v.findViewById(R.id.text_location);
            textViewStartTime = (TextView) v.findViewById(R.id.text_start_time);
            textViewEndTime = (TextView) v.findViewById(R.id.text_end_time);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title_section;

        public SectionViewHolder(View v) {
            super(v);
            title_section = (TextView) v.findViewById(R.id.title_section);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_chat, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Gson gson = new Gson();
        Log.d(TAG, "GsonDEBUG onBindViewHolder: " + gson.toJson(items.get(position)));
        
        if(items.get(position) instanceof Event) {
            Event p = (Event) items.get(position);

            OriginalViewHolder view = (OriginalViewHolder) holder;

            String jenis = "";
            if(p.getEventType() == 1) {
                jenis = "Tahsin";
            } else if(p.getEventType() == 2) {
                jenis = "Tahfidz";
            } else if(p.getEventType() == 3) {
                jenis = "Tahsin & Tahfidz";
            } else {
                jenis = "Tadabbur";
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            view.textViewTitle.setText(jenis);
            view.textViewLocation.setText(p.getShortPlaceName());
            view.textViewStartTime.setText(sdf.format(p.getStartTime()) + " ~");
            view.textViewEndTime.setText(sdf.format(p.getEndTime()));

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, (Event) items.get(position), position);
                    }
                }
            });

        } else if(items.get(position) instanceof DateSection) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
            DateSection p = (DateSection) items.get(position);

            SectionViewHolder view = (SectionViewHolder) holder;
//            view.title_section.setText(sdf.format(p.getDate()));
            view.title_section.setText(p.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position) instanceof Event ? VIEW_ITEM : VIEW_SECTION;
    }

    public void insertItem(int index, Event event){
        items.add(index, event);
        notifyItemInserted(index);
    }
}
