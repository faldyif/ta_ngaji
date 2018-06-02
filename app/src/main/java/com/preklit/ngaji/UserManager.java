package com.preklit.ngaji;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.preklit.ngaji.entities.AccessToken;
import com.preklit.ngaji.entities.SelfUserDetail;

/**
 * Created by faldyikhwanfadila on 02/06/18.
 */

public class UserManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;

    private static UserManager INSTANCE = null;

    private UserManager(SharedPreferences prefs) {
        this.prefs = prefs;
        this.editor = prefs.edit();
        gson = new Gson();
    }

    public static synchronized UserManager getInstance(SharedPreferences prefs) {
        if(INSTANCE == null) {
            INSTANCE = new UserManager(prefs);
        }

        return INSTANCE;
    }

    public void saveUser(SelfUserDetail userDetail) {
        editor.putString("USER_DETAIL", gson.toJson(userDetail)).commit();
    }

    public void deleteUser() {
        editor.remove("USER_DETAIL").commit();
    }

    public SelfUserDetail getUserDetail() {
        String stringUserDetail = prefs.getString("USER_DETAIL", null);
        SelfUserDetail selfUserDetail = gson.fromJson(stringUserDetail, SelfUserDetail.class);
        return selfUserDetail;
    }
}
