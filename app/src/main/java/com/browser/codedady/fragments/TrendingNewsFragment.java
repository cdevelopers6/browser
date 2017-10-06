package com.browser.codedady.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.codedady.R;
import com.browser.codedady.adapter.RecommendNewsAdapter;
import com.browser.codedady.adapter.TrendingNewsAdapter;
import com.browser.codedady.newsPojo.Article;
import com.browser.codedady.newsPojo.ArticleHeadlines;
import com.browser.codedady.newsPojo.MainPojo;
import com.browser.codedady.newsPojo.TrendingPojo;
import com.browser.codedady.newsRetrofit.ApiClient;
import com.browser.codedady.newsRetrofit.ApiInterface;
import com.browser.codedady.newsRetrofit.ApiInterfaceTrending;
import com.browser.codedady.utils.Key;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TrendingNewsFragment extends Fragment
{
    private RecyclerView rViewTrending;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trending_news_fragment, container, false);

        rViewTrending = (RecyclerView)rootView.findViewById(R.id.rViewTrending);
        LinearLayoutManager llmt = new LinearLayoutManager(rootView.getContext());
        rViewTrending.setLayoutManager(llmt);

        makeRetrofitCall();
        return rootView;
    }


    private void makeRetrofitCall()
    {
        ApiInterfaceTrending apiService =
                ApiClient.getClient().create(ApiInterfaceTrending.class);

        Call<TrendingPojo> call = apiService.getValues("the-times-of-india","latest", Key.getApiKey());
        call.enqueue(new Callback<TrendingPojo>()
        {
            @Override
            public void onResponse(Call<TrendingPojo>call, Response<TrendingPojo> response)
            {
                try
                {
                    Log.e("TAG",""+response.body().getArticles().get(0).getDescription());
                    List<ArticleHeadlines> articles = response.body().getArticles();

                    TrendingNewsAdapter adapter = new TrendingNewsAdapter(articles);
                    rViewTrending.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                }
                catch (Exception e)
                {
                    //Toast.makeText(Activity_Main.this, "Check data connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TrendingPojo> call, Throwable t) {
                // Log error here since request failed
                Log.e("FAILURE", t.toString());
            }
        });
    }

}
