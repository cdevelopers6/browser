package com.browser.codedady;


import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.browser.codedady.fragments.FragmentDownloadMain;
import com.browser.codedady.fragments.FragmentDownloadingMain;
import com.browser.codedady.fragments.RecommendedNewsFragment;
import com.browser.codedady.fragments.SportsNewsFragment;
import com.browser.codedady.fragments.TrendingNewsFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class DownloadsActivity extends AppCompatActivity
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
        setContentView(R.layout.activity_downloads);

        isClicked = false;
        backButton = (ImageButton)findViewById(R.id.backDownloads);
        menuButton = (ImageButton)findViewById(R.id.menuDownloads);
        bookmarkButton = (ImageButton)findViewById(R.id.bookmarkFinalDownloads);
        itemsCard = (CardView)findViewById(R.id.menuContentCardDownloads);
        downloadsButton = (RelativeLayout)findViewById(R.id.boxLay1Downloads);
        exitButton = (RelativeLayout)findViewById(R.id.boxLay2Downloads);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarDownloads);
        setSupportActionBar(myToolbar);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Downloading", FragmentDownloadingMain.class)
                .add("Downloads", FragmentDownloadMain.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerDownloads);
        viewPager.setAdapter(adapter);


        SmartTabLayout viewPagerTab = (SmartTabLayout)findViewById(R.id.smartTabDownloads);
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
                Intent mIntent = new Intent(DownloadsActivity.this,HistoryAndBookmarkActivity.class);
                startActivity(mIntent);
                finish();
            }
        });

        downloadsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsCard.setVisibility(View.GONE);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(1);
            }
        });

    }


}
