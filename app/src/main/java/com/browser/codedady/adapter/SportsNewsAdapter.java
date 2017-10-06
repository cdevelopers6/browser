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
import com.browser.codedady.newsPojo.SportsArticle;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SportsNewsAdapter extends RecyclerView.Adapter<SportsNewsAdapter.FeedsViewHolder>
{



    private DataHolder d1 = new DataHolder();

    public  class FeedsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView sTitle;
        private ImageView sImage;
        private Typeface sFace;
        private Context sContext;

        FeedsViewHolder(View itemView)
        {
            super(itemView);
            sContext = itemView.getContext();
            sTitle = (TextView)itemView.findViewById(R.id.sportsNewsTitleTextCard);
            sFace = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/roboto.ttf");
            sImage = (ImageView)itemView.findViewById(R.id.sportsNewsImageCard);
            sTitle.setTypeface(sFace);
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
        List<SportsArticle> feeds;

    }



    public SportsNewsAdapter(List<SportsArticle> feeds)
    {
        this.d1.feeds = feeds;
    }



    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public FeedsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sports_news_card, viewGroup, false);
        FeedsViewHolder pvh = new FeedsViewHolder(v);
        return pvh;
    }



    @Override
    public void onBindViewHolder(FeedsViewHolder feedViewHolder, int i)
    {
        if(d1.feeds.get(i).getTitle().length()>50)
        {
            feedViewHolder.sTitle.setText(d1.feeds.get(i).getTitle());
        }
        else
        {
            feedViewHolder.sTitle.setText(d1.feeds.get(i).getTitle());
        }
        //feedViewHolder.desc.setText(d1.feeds.get(i).getDescription());

        Picasso.with(feedViewHolder.sContext).load(d1.feeds.get(i).getUrlToImage()).into(feedViewHolder.sImage);
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


