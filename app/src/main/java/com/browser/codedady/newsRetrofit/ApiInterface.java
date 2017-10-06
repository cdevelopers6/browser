package com.browser.codedady.newsRetrofit;

import com.browser.codedady.newsPojo.MainPojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface
{
    @GET("articles")
    Call<MainPojo> getValues(@Query("source") String sourceNews, @Query("sortBy") String sortHow, @Query("apiKey") String apiKey);

}