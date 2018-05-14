package com.preklit.ngaji.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.adapter.AdapterListJadwal;
import com.preklit.ngaji.data.DataGenerator;
import com.preklit.ngaji.entities.DateSection;
import com.preklit.ngaji.entities.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListJadwalAvailableFragment extends Fragment {

    private static final String TAG = ListJadwalAvailableFragment.class.getSimpleName();
    
    private RecyclerView recyclerView;
    private AdapterListJadwal mAdapter;
    private Context context;
    private View parent_view;


    public ListJadwalAvailableFragment() {
        // Required empty public constructor
    }

    public static ListJadwalAvailableFragment newInstance(Context context) {
        ListJadwalAvailableFragment fragment = new ListJadwalAvailableFragment();
        fragment.context = context;

        // do some magic
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_jadwal_available, container, false);

        parent_view = getActivity().findViewById(android.R.id.content);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent(view);
    }

    private void initComponent(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        List<Object> items = DataGenerator.getEventGuruData(context, 10);
        
        Gson gson = new Gson();
        Log.d(TAG, "GsonDEBUG initComponent: " + gson.toJson(items));

        int sect_count = 0;
        int sect_idx = 0;
        List<String> months = DataGenerator.getStringsMonth(context);

        for (int i = 0; i < items.size() / 6; i++) {

            items.add(sect_count, new DateSection(months.get(sect_idx)));
            sect_count = sect_count + 5;
            sect_idx++;
        }

        //set data and list adapter
        mAdapter = new AdapterListJadwal(context, items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListJadwal.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                if(obj instanceof Event) {
                    Event event = (Event) obj;
                    Snackbar.make(parent_view, "Item " + event.getShortPlaceName() + " clicked", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }
}
