package com.preklit.ngaji.network;

import com.preklit.ngaji.entities.AccessToken;
import com.preklit.ngaji.entities.CreateResponse;
import com.preklit.ngaji.entities.Event;
import com.preklit.ngaji.entities.EventsResponse;
import com.preklit.ngaji.entities.RegisterResponse;
import com.preklit.ngaji.entities.SelfUserDetail;
import com.preklit.ngaji.entities.TeacherFreeTimeResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

import static com.preklit.ngaji.network.RetrofitBuilder.apiVersion;

/**
 * Created by Faldy on 4/17/2018.
 */

public interface ApiService {
    // -- BEGIN AUTHENTICATION ROUTES -- //
    @POST("register")
    @FormUrlEncoded
    Call<RegisterResponse> register(@Field("name") String name, @Field("email") String email, @Field("password") String password, @Field("gender") Character gender, @Field("whatsapp_number") String whatsappNumber);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password, @Field("firebase_token") String firebaseToken);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("v1/logout")
    @FormUrlEncoded
    Call<Object> logout(@Field("firebase_token") String firebaseToken);
    // -- END AUTHENTICATION ROUTES -- //

    // Firebase refresh token
    @PUT("v1/firebase/token")
    @FormUrlEncoded
    Call<Object> refreshFirebaseToken(@Field("firebase_token") String firebaseToken);

    // Find teacher free time
    @GET("v1/finder/events/filter")
    Call<TeacherFreeTimeResponse> listEvents(@Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("time_start") String timeStart, @Query("time_end") String timeEnd, @Query("event_type") String eventType);

    // Get current user profile
    @GET("v1/profile")
    Call<SelfUserDetail> refreshSelfUserDetail();

    // Create event request
    @POST("v1/events")
    @FormUrlEncoded
    Call<CreateResponse> createEvent(@Field("latitude") Double latitude, @Field("longitude") Double longitude, @Field("time_start") String timeStart, @Field("time_end") String timeEnd, @Field("event_type") String eventType, @Field("teacher_free_time_id") String teacherFreeTimeId, @Field("location_details") String locationDetails, @Field("short_place_name") String shortPlaceName);

    @GET("v1/events")
    Call<EventsResponse> listStudentEvent(@Query("active") Integer active);
    @GET("v1/history/events")
    Call<EventsResponse> listHistoryStudentEvent(@Query("status") String status);

    // Teacher Only
    @GET("v1/teacher/freetime")
    Call<TeacherFreeTimeResponse> indexTeacherFreeTime();
    @POST("v1/teacher/freetime")
    @FormUrlEncoded
    Call<CreateResponse> createTeacherFreeTime(@Field("latitude") Double latitude, @Field("longitude") Double longitude, @Field("time_start") String timeStart, @Field("time_end") String timeEnd, @Field("short_place_name") String shortPlaceName);
    @GET("v1/teacher/list/event/unconfirmed")
    Call<EventsResponse> listEventUnconfirmed();
    @GET("v1/teacher/list/event/unconfirmed/count")
    Call<Integer> countEventUnconfirmed();
    @GET("v1/teacher/list/event/confirmed")
    Call<EventsResponse> listEventConfirmed();

    @POST("v1/teacher/update/event/status")
    @FormUrlEncoded
    Call<CreateResponse> updateEventStatus(@Field("event_id") Integer eventId, @Field("status") Integer status);

}

