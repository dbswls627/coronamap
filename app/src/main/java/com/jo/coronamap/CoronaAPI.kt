package com.jo.coronamap


import com.jo.coronamap.dataModel.DataList
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CoronaAPI {
    @GET("/api/15077586/v1/centers")
    @Headers(
        "accept: application/json",
        "Authorization: ${Util.API_KEY}"
    )
    suspend fun getList(
        @Query("page") page: String,
        @Query("perPage") perPage: String,
        @Query("serviceKey") serviceKey: String
    ): DataList
}