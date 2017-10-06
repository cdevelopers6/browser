package com.browser.codedady;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.browser.codedady.fragments.RecommendedNewsFragment;
import com.browser.codedady.fragments.SportsNewsFragment;
import com.browser.codedady.fragments.TrendingNewsFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class NewsFeedActivity extends AppCompatActivity
{
    private ImageButton backButton;
    private ImageButton menuButton;
    private ImageButton bookmarkButton;
    private ImageButton tabsButton;
    private boolean isClicked;
    private CardView itemsCard;
    private RelativeLayout downloadsButton;
    private RelativeLayout exitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarNewsFeed);
        setSupportActionBar(myToolbar);

        isClicked = false;
        backButton = (ImageButton)findViewById(R.id.backNewsFeed);
        menuButton = (ImageButton)findViewById(R.id.menuNewsFeed);
        bookmarkButton = (ImageButton)findViewById(R.id.bookmarkFinalNewsFeed);
        itemsCard = (CardView)findViewById(R.id.menuContentCardNewsFeed);
        downloadsButton = (RelativeLayout)findViewById(R.id.boxLay1NewsFeed);
        exitButton = (RelativeLayout)findViewById(R.id.boxLay2NewsFeed);




        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("News Feed");
        }

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Recommend", RecommendedNewsFragment.class)
                .add("Trending", TrendingNewsFragment.class)
                .add("Sports", SportsNewsFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerNews);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isClicked)
                {
                    isClicked = false;
                    itemsCard.setVisibility(View.GONE);
                }
                else
                {
                    isClicked = true;
                    itemsCard.setVisibility(View.VISIBLE);
                }

            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(NewsFeedActivity.this,HistoryAndBookmarkActivity.class);
                startActivity(mIntent);
                finish();
            }
        });

        downloadsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(NewsFeedActivity.this,DownloadsActivity.class);
                startActivity(mIntent);
                finish();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
