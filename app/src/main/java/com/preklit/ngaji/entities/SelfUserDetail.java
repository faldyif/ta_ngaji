package com.preklit.ngaji.entities;

import com.squareup.moshi.Json;

import javax.annotation.Nullable;

/**
 * Created by faldyikhwanfadila on 02/06/18.
 */

public class SelfUserDetail {
    @Json(name = "id")
    String id;
    @Json(name = "name")
    String name;
    @Json(name = "email")
    String email;
    @Json(name = "whatsapp_number")
    String whatsappNumber;
    @Json(name = "profile_pic_url")
    String profilePicUrl;
    @Json(name = "gender")
    String gender;
    @Json(name = "role")
    String role;
    @Json(name = "credits_amount")
    Double creditsAmount;
    @Json(name = "loyalty_point")
    Integer loyaltyPoint;

    @Nullable
    @Json(name = "teacher_data")
    TeacherData teacherData;


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public Double getCreditsAmount() {
        return creditsAmount;
    }

    public Integer getLoyaltyPoint() {
        return loyaltyPoint;
    }

    public String getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public String getRole() {
        return role;
    }

    @Nullable
    public TeacherData getTeacherData() {
        return teacherData;
    }
}
