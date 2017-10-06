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
import com.browser.codedady.adapter.SportsNewsAdapter;
import com.browser.codedady.newsPojo.Article;
import com.browser.codedady.newsPojo.MainPojo;
import com.browser.codedady.newsPojo.SportsArticle;
import com.browser.codedady.newsPojo.SportsPojo;
import com.browser.codedady.newsRetrofit.ApiClient;
import com.browser.codedady.newsRetrofit.ApiInterface;
import com.browser.codedady.newsRetrofit.ApiInterfaceSports;
import com.browser.codedady.utils.Key;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SportsNewsFragment extends Fragment
{
    private RecyclerView rViewSports;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sports_news_fragment, container, false);

        rViewSports = (RecyclerView)rootView.findViewById(R.id.rViewSports);

        LinearLayoutManager llmr = new LinearLayoutManager(rootView.getContext());
        rViewSports.setLayoutManager(llmr);

        makeRetrofitCall();

        return rootView;
    }


    private void makeRetrofitCall()
    {
        ApiInterfaceSports apiService =
                ApiClient.getClient().create(ApiInterfaceSports.class);

        Call<SportsPojo> call = apiService.getValues("bbc-sport","top", Key.getApiKey());
        call.enqueue(new Callback<SportsPojo>()
        {
            @Override
            public void onResponse(Call<SportsPojo>call, Response<SportsPojo> response)
            {
                try
                {
                    Log.e("TAG",""+response.body().getArticles().get(0).getDescription());
                    List<SportsArticle> articles = response.body().getArticles();

                    SportsNewsAdapter adapter = new SportsNewsAdapter(articles);
                    rViewSports.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                }
                catch (Exception e)
                {
                    //Toast.makeText(Activity_Main.this, "Check data connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SportsPojo> call, Throwable t) {
                // Log error here since request failed
                Log.e("FAILURE", t.toString());
            }
        });
    }
}
