package com.example.android.mybakingapp.retrofit;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Interface used by Retrofit to load all recipes (baking.json) from server
 */

public interface BakingApiService {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<JsonArray> recipesJson();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://d17h27t6h515a5.cloudfront.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
