package com.browser.codedady.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.browser.codedady.NewsReaderActivity;
import com.browser.codedady.R;
import com.browser.codedady.newsPojo.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecommendNewsAdapter extends RecyclerView.Adapter<RecommendNewsAdapter.FeedsViewHolder>
{



    private DataHolder d1 = new DataHolder();

    public  class FeedsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView recommendTitle;
        private ImageView recommendNewsImage;
        private Typeface recommendFace;
        private Context mcontext;

        FeedsViewHolder(View itemView)
        {
            super(itemView);
            mcontext = itemView.getContext();
            recommendTitle = (TextView)itemView.findViewById(R.id.recommendNewsTitleTextCard);
            recommendFace = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/roboto.ttf");
            recommendNewsImage = (ImageView)itemView.findViewById(R.id.recommendNewsImageCard);
            recommendTitle.setTypeface(recommendFace);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view)
        {
            Intent mIntent = new Intent(itemView.getContext(), NewsReaderActivity.class);
            Bundle extras = new Bundle();
            extras.putString("TITLE",d1.feeds.get(getLayoutPosition()).getTitle());
            extras.putString("DESC",d1.feeds.get(getLayoutPosition()).getDescription());
            extras.putString("IMAGE",d1.feeds.get(getLayoutPosition()).getUrlToImage());
            mIntent.putExtras(extras);
            itemView.getContext().startActivity(mIntent);
        }



    }

    private static class DataHolder
    {
        List<Article> feeds;

    }



    public RecommendNewsAdapter(List<Article> feeds)
    {
        this.d1.feeds = feeds;
    }



    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public FeedsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recommend_news_card, viewGroup, false);
        FeedsViewHolder pvh = new FeedsViewHolder(v);
        return pvh;
    }



    @Override
    public void onBindViewHolder(FeedsViewHolder feedViewHolder, int i)
    {
        if(d1.feeds.get(i).getTitle().length()>50)
        {
            feedViewHolder.recommendTitle.setText(d1.feeds.get(i).getTitle());
        }
        else
        {
            feedViewHolder.recommendTitle.setText(d1.feeds.get(i).getTitle());
        }
        //feedViewHolder.desc.setText(d1.feeds.get(i).getDescription());

        Picasso.with(feedViewHolder.mcontext).load(d1.feeds.get(i).getUrlToImage()).into(feedViewHolder.recommendNewsImage);
    }

    @Override
    public int getItemCount()
    {

        if(d1.feeds!=null)
        {
            return d1.feeds.size();
        }
        else
        {
            return 0;
        }
    }



}

