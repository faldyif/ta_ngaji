package com.preklit.ngaji.data;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.entities.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by faldyikhwanfadila on 02/05/18.
 */

public class DataGenerator {

    private static final String TAG = DataGenerator.class.getSimpleName();

    /**
     * Generate dummy data people
     *
     * @param ctx android context
     * @return list of object
     */
    public static List<Object> getEventGuruData(Context ctx, int dataLength) {
        List<Object> items = new ArrayList<>();

        String masjid_names[] = ctx.getResources().getStringArray(R.array.sample_masjid_array);
        String type_names[] = ctx.getResources().getStringArray(R.array.sample_event_type);

        for (int i = 0; i < dataLength; i++) {
            Random rand = new Random();
            Event obj = new Event();

            Date startDate = new Date(System.currentTimeMillis() + 3600 * 1000 + (i * 3600 * 1000));
            Date endDate = new Date(System.currentTimeMillis() + 3600 * 1000 + 3600 * 1000 + (i * 3600 * 1000));

            obj.setEventType("tahsin");
            obj.setShortPlaceName(getRandomArray(masjid_names));
            obj.setStartTime("fdsa");
            obj.setEndTime("asdf");
//            obj.setStartTime(startDate);
//            obj.setEndTime(endDate);

            items.add(obj);
        }
        Collections.shuffle(items);

        Gson gson = new Gson();
        Log.d(TAG, "GsonDEBUG getEventGuruData: " + gson.toJson(items));

        return items;
    }

    public static List<String> getStringsMonth(Context ctx) {
        List<String> items = new ArrayList<>();
        String arr[] = ctx.getResources().getStringArray(R.array.month);
        for (String s : arr) items.add(s);
        Collections.shuffle(items);
        return items;
    }

    public static int getRandomArray(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static String getRandomArray(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
