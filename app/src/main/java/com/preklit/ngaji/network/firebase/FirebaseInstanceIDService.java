package com.preklit.ngaji.network.firebase;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.activity.LoginActivity;
import com.preklit.ngaji.activity.MainActivity;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by faldyikhwanfadila on 12/06/18.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = FirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        TokenManager tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() != null) {
            ApiService service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

            Call<Object> call = service.refreshFirebaseToken(refreshedToken);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.w(TAG, "onResponse: " + response );

                    if(response.isSuccessful()){
                        Log.w(TAG, "onResponse: Success updating fcm token to the server");
                    } else {
                        Log.w(TAG, "onResponse: Failed updating fcm token to the server");
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                }
            });
        }
    }
}
