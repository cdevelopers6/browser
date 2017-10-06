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
import com.browser.codedady.adapter.TopNewsAdapter;
import com.browser.codedady.newsPojo.Article;
import com.browser.codedady.newsPojo.MainPojo;
import com.browser.codedady.newsRetrofit.ApiClient;
import com.browser.codedady.newsRetrofit.ApiInterface;
import com.browser.codedady.utils.Key;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecommendedNewsFragment extends Fragment
{
    private RecyclerView rViewRecommended;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recommended_news_fragment, container, false);

        rViewRecommended = (RecyclerView)rootView.findViewById(R.id.rViewRecommended);
        LinearLayoutManager llmr = new LinearLayoutManager(rootView.getContext());
        rViewRecommended.setLayoutManager(llmr);

        makeRetrofitCall();

        return rootView;
    }

    private void makeRetrofitCall()
    {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MainPojo> call = apiService.getValues("the-times-of-india","top", Key.getApiKey());
        call.enqueue(new Callback<MainPojo>()
        {
            @Override
            public void onResponse(Call<MainPojo>call, Response<MainPojo> response)
            {
                try
                {
                    Log.e("TAG",""+response.body().getArticles().get(0).getDescription());
                    List<Article> articles = response.body().getArticles();

                    RecommendNewsAdapter adapter = new RecommendNewsAdapter(articles);
                    rViewRecommended.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                }
                catch (Exception e)
                {
                    //Toast.makeText(Activity_Main.this, "Check data connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MainPojo> call, Throwable t) {
                // Log error here since request failed
                Log.e("FAILURE", t.toString());
            }
        });
    }
}
