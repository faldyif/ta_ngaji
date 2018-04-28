package com.preklit.ngaji.network;

import com.preklit.ngaji.entities.AccessToken;
import com.preklit.ngaji.entities.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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
}
