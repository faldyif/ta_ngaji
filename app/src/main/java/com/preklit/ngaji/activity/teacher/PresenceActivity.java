package com.preklit.ngaji.activity.teacher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.preklit.ngaji.R;
import com.preklit.ngaji.TokenManager;
import com.preklit.ngaji.activity.LoginActivity;
import com.preklit.ngaji.activity.MainActivity;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.network.ApiService;
import com.preklit.ngaji.network.RetrofitBuilder;

import net.glxn.qrgen.android.QRCode;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresenceActivity extends AppCompatActivity {

    @BindView(R.id.qr_code_image)
    ImageView imageViewQrCode;

    private TokenManager tokenManager;
    private ApiService service;
    Event event;
    private String TAG = "PresenceActivity";
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence_teacher);
        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(PresenceActivity.this, LoginActivity.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        Intent intent = getIntent();
        event = (new Gson()).fromJson(intent.getStringExtra("event"), Event.class);
        String code = "";
        try {
            code = event.getPresence().getUniqueCode();
        } catch (NullPointerException e) {
            e.printStackTrace();
            code = "error";
        }

        Bitmap myBitmap = QRCode.from("NY!:"+code).bitmap();
        imageViewQrCode.setImageBitmap(myBitmap);

        timer();
    }

    private void timer() {
        timer = new CountDownTimer(5000, 20) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try{
                    checkPresence();
                }catch(Exception e){
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        }.start();
    }

    void checkPresence(){
        Call<Integer> call = service.checkPresenceTeacher(event.getId());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    if(response.body() == 1) {
                        Toast.makeText(PresenceActivity.this, "Berhasil Absen!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(PresenceActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        timer.start();
                    }
                }else {
                    Toast.makeText(PresenceActivity.this, "Kok gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }
}
