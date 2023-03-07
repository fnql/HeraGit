package com.cookandroid.heragit.Service

import com.cookandroid.heragit.Model.UserEvent
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CommitService {

    @GET("users/{userName}/events")
    fun getEvent(
        @Path("userName") userName: String,
        @Header("Authorization") accessToken: String
    ): Call<List<UserEvent>>

}