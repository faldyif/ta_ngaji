package com.preklit.ngaji.network;

import com.preklit.ngaji.entities.AccessToken;
import com.preklit.ngaji.entities.RegisterResponse;
import com.preklit.ngaji.entities.SelfUserDetail;
import com.preklit.ngaji.entities.TeacherFreeTimeResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.preklit.ngaji.network.RetrofitBuilder.apiVersion;

/**
 * Created by Faldy on 4/17/2018.
 */

public interface ApiService {
    @POST("register")
    @FormUrlEncoded
    Call<RegisterResponse> register(@Field("name") String name, @Field("email") String email, @Field("password") String password, @Field("gender") Character gender, @Field("whatsapp_number") String whatsappNumber);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @GET("v1/finder/events/filter")
    Call<TeacherFreeTimeResponse> listEvents(@Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("time_start") String timeStart, @Query("time_end") String timeEnd, @Query("event_type") String eventType);

    @GET("v1/profile")
    Call<SelfUserDetail> refreshSelfUserDetail();

}
