package com.browser.codedady;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.browser.codedady.fragments.FragmentBookmarkMain;
import com.browser.codedady.fragments.FragmentDownloadMain;
import com.browser.codedady.fragments.FragmentDownloadingMain;
import com.browser.codedady.fragments.FragmentHistoryMain;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class HistoryAndBookmarkActivity extends AppCompatActivity
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
        setContentView(R.layout.activity_history_and_bookmark);

        isClicked = false;
        backButton = (ImageButton)findViewById(R.id.backHistory);
        menuButton = (ImageButton)findViewById(R.id.menuHistory);
        bookmarkButton = (ImageButton)findViewById(R.id.bookmarkFinalHistory);
        itemsCard = (CardView)findViewById(R.id.menuContentCardHistory);
        downloadsButton = (RelativeLayout)findViewById(R.id.boxLay1History);
        exitButton = (RelativeLayout)findViewById(R.id.boxLay2History);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarHistoryAndBookmarks);
        setSupportActionBar(myToolbar);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("History", FragmentHistoryMain.class)
                .add("Bookmarks", FragmentBookmarkMain.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerHistoryAndBookmarks);
        viewPager.setAdapter(adapter);


        SmartTabLayout viewPagerTab = (SmartTabLayout)findViewById(R.id.smartTabHistoryAndBookmarks);
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

        downloadsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(HistoryAndBookmarkActivity.this,DownloadsActivity.class);
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
}
