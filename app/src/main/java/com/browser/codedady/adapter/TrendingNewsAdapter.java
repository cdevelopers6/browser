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
import com.browser.codedady.newsPojo.ArticleHeadlines;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrendingNewsAdapter extends RecyclerView.Adapter<TrendingNewsAdapter.FeedsViewHolder>
{



    private DataHolder d1 = new DataHolder();

    public  class FeedsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView trendingTitle;
        private ImageView trendingNewsImage;
        private Typeface trendingFace;
        private Context trendingContext;

        FeedsViewHolder(View itemView)
        {
            super(itemView);
            trendingContext = itemView.getContext();
            trendingTitle = (TextView)itemView.findViewById(R.id.recommendNewsTitleTextCard);
            trendingFace = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/roboto.ttf");
            trendingNewsImage = (ImageView)itemView.findViewById(R.id.recommendNewsImageCard);
            trendingTitle.setTypeface(trendingFace);
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
        List<ArticleHeadlines> feeds;

    }



    public TrendingNewsAdapter(List<ArticleHeadlines> feeds)
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
            feedViewHolder.trendingTitle.setText(d1.feeds.get(i).getTitle());
        }
        else
        {
            feedViewHolder.trendingTitle.setText(d1.feeds.get(i).getTitle());
        }
        //feedViewHolder.desc.setText(d1.feeds.get(i).getDescription());

        Picasso.with(feedViewHolder.trendingContext).load(d1.feeds.get(i).getUrlToImage()).into(feedViewHolder.trendingNewsImage);
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

