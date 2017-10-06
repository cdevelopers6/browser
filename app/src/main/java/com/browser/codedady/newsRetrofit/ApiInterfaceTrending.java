package com.browser.codedady.newsRetrofit;


import com.browser.codedady.newsPojo.MainPojo;
import com.browser.codedady.newsPojo.TrendingPojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterfaceTrending
{
    @GET("articles")
    Call<TrendingPojo> getValues(@Query("source") String sourceNews, @Query("sortBy") String sortHow, @Query("apiKey") String apiKey);

}