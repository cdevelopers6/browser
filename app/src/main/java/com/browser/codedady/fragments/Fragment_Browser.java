package com.browser.codedady.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.browser.codedady.Activity_Main;
import com.browser.codedady.DownloadsActivity;
import com.browser.codedady.EventBus.BrowserSearchEvent;
import com.browser.codedady.HistoryAndBookmarkActivity;
import com.browser.codedady.NewsFeedActivity;
import com.browser.codedady.adapter.TopNewsAdapter;
import com.browser.codedady.newsPojo.Article;
import com.browser.codedady.newsPojo.MainPojo;
import com.browser.codedady.newsRetrofit.ApiClient;
import com.browser.codedady.newsRetrofit.ApiInterface;
import com.browser.codedady.utils.Key;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.browser.codedady.Home;
import com.browser.codedady.R;
import com.browser.codedady.databases.DbAdapter_ReadLater;
import com.browser.codedady.helper.Activity_settings;
import com.browser.codedady.helper.class_CustomViewPager;
import com.browser.codedady.helper.helper_browser;
import com.browser.codedady.helper.helper_editText;
import com.browser.codedady.helper.helper_main;
import com.browser.codedady.helper.helper_toolbar;
import com.browser.codedady.helper.helper_webView;
import com.browser.codedady.utils.Utils_AdBlocker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import ru.whalemare.sheetmenu.SheetMenu;

import static android.content.Context.DOWNLOAD_SERVICE;

public class Fragment_Browser extends Fragment implements ObservableScrollViewCallbacks {


    // Views

    private ObservableWebView mWebView;
    private ProgressBar progressBar;
    private ImageButton imageButton_up;
    private ImageButton imageButton_down;
    private ImageButton imageButton_left;
    private ImageButton imageButton_right;
    private TextView urlBar;
    private FrameLayout customViewContainer;
    private View mCustomView;
    private EditText editText;
    private HorizontalScrollView scrollTabs;
    private class_CustomViewPager viewPager;
    private AppBarLayout appBarLayout;
    private RecyclerView rView;
    private CardView socialCardBox;
    private CardView newsCard;
    private EditText viewThatFuckinWorks;
    private CardView mSearchCard;
    private ImageButton addToBookmarks;



    // Strings

    private String mCameraPhotoPath;
    private final String TAG = "Browser";
    private String sharePath;
    private String tab_number;


    // Others

    private Activity activity;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private myWebChromeClient mWebChromeClient;
    private SharedPreferences sharedPref;
    private File shareFile;
    private Bitmap bitmap;
    private ValueCallback<Uri[]> mFilePathCallback;
    private static final int REQUEST_CODE_LOLLIPOP = 1;
    private HorizontalScrollView horizontalScrollView;
    private boolean isMenuClicked;
    private boolean isTabsClicked;
    private View rootView;


    // Booleans

    private boolean inCustomView() {
        return (mCustomView != null);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_browser, container, false);


        setHasOptionsMenu(true);
        activity = getActivity();

        isMenuClicked = false;
        isTabsClicked = false;
        PreferenceManager.setDefaultValues(activity, R.xml.user_settings, false);
        PreferenceManager.setDefaultValues(activity, R.xml.user_settings_search, false);
        PreferenceManager.setDefaultValues(activity, R.xml.user_settings_app, false);
        PreferenceManager.setDefaultValues(activity, R.xml.user_settings_close, false);
        PreferenceManager.setDefaultValues(activity, R.xml.user_settings_start, false);
        PreferenceManager.setDefaultValues(activity, R.xml.user_settings_search_main, false);
        PreferenceManager.setDefaultValues(activity, R.xml.user_settings_data, false);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putInt("tab_" + tab_number + "_exit", 0).apply();

        TextView newsFeedText = (TextView)rootView.findViewById(R.id.newsFeedText);
        Typeface typeface = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/roboto.ttf");
        newsFeedText.setTypeface(typeface);




        // find Views
     /*final   RelativeLayout relativeLayout= (RelativeLayout) rootView.findViewById(R.id.relative1);
        TextView settings = (TextView)rootView.findViewById(R.id.settingstext);
        TextView exit = (TextView)rootView.findViewById(R.id.exittext);*/
        ImageButton back1 =(ImageButton)getActivity().findViewById(R.id.back);
        ImageButton front1 =(ImageButton)getActivity().findViewById(R.id.front);
        ImageButton bookmarksImage =(ImageButton)getActivity().findViewById(R.id.bookmarkFinal);
        ImageButton tabs1 =(ImageButton)getActivity().findViewById(R.id.tabs);
        final ImageButton menu1 =(ImageButton)getActivity().findViewById(R.id.menu);
        socialCardBox = (CardView)rootView.findViewById(R.id.socialCard);
        newsCard = (CardView)rootView.findViewById(R.id.newsCard);
        viewThatFuckinWorks = (EditText)rootView.findViewById(R.id.editTextFuck);


        ImageButton facebookButton = (ImageButton)rootView.findViewById(R.id.facebookButton);
        ImageButton twitterButton = (ImageButton)rootView.findViewById(R.id.twitterButton);
        ImageButton googlePlusButton = (ImageButton)rootView.findViewById(R.id.googleplusButton);
        ImageButton instagramButton = (ImageButton)rootView.findViewById(R.id.instagramButton);
        ImageButton linkedInButton = (ImageButton)rootView.findViewById(R.id.linkedinButton);
        ImageButton pinterestButton = (ImageButton)rootView.findViewById(R.id.pinterestButton);
        ImageButton youtubeButton = (ImageButton)rootView.findViewById(R.id.youtubeButton);
        ImageButton spotifyButton = (ImageButton)rootView.findViewById(R.id.spotifyButton);
        ImageButton appleButton = (ImageButton)rootView.findViewById(R.id.appleButton);
        ImageButton soundcloudButton = (ImageButton)rootView.findViewById(R.id.soundcloudButton);
        ImageButton tumblrButton = (ImageButton)rootView.findViewById(R.id.tumblrButton);
        ImageButton githubButton = (ImageButton)rootView.findViewById(R.id.githubButton);

        addToBookmarks = (ImageButton)getActivity().findViewById(R.id.addToBookmarkButton);


        rView = (RecyclerView)rootView.findViewById(R.id.rViewTopNews);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rView.setLayoutManager(llm);

        RelativeLayout downloads = (RelativeLayout)getActivity().findViewById(R.id.boxLay1);
        RelativeLayout exitCard = (RelativeLayout)getActivity().findViewById(R.id.boxLay2);

        final CardView menuCardx = (CardView)getActivity().findViewById(R.id.menuContentCard);


        mWebView = (ObservableWebView) rootView.findViewById(R.id.webView);
        customViewContainer = (FrameLayout) rootView.findViewById(R.id.customViewContainer);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        editText = (EditText) activity.findViewById(R.id.editTextSearch);
        urlBar = (TextView) activity.findViewById(R.id.urlBar);
        imageButton_left = (ImageButton) rootView.findViewById(R.id.imageButton_left);
        imageButton_right = (ImageButton) rootView.findViewById(R.id.imageButton_right);
        //imageButton_up = (ImageButton) rootView.findViewById(R.id.imageButton);
        //imageButton_down = (ImageButton) rootView.findViewById(R.id.imageButton_down);
        scrollTabs  = (HorizontalScrollView) activity.findViewById(R.id.scrollTabs);
        viewPager = (class_CustomViewPager) activity.findViewById(R.id.viewpager);
        appBarLayout = (AppBarLayout) activity.findViewById(R.id.appBarLayout);
        horizontalScrollView = (HorizontalScrollView) getActivity().findViewById(R.id.scrollTabs);
        mSearchCard = (CardView)getActivity().findViewById(R.id.searchCard);

        makeRetrofitCall();

        helper_main.save_bookmark(getActivity(),"Facebook","http://www.facebook.com",mWebView);
        helper_main.save_bookmark(getActivity(),"Stackoverflow","http://www.stackoverflow.com",mWebView);
        helper_main.save_bookmark(getActivity(),"Twitter","http://www.twitter.com",mWebView);
        helper_main.save_bookmark(getActivity(),"Github","http://www.github.com",mWebView);


        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                mWebView.setVisibility(View.VISIBLE);
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.facebook.com");
            }
        });

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.twitter.com");
            }
        });

        googlePlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.plus.google.com");
            }
        });

        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.instagram.com");
            }
        });

        linkedInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.linkedin.com");
            }
        });

        pinterestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.pinterest.com");
            }
        });

        youtubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.youtube.com");
            }
        });

        spotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.spotify.com");
            }
        });

        appleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.apple.com");
            }
        });

        soundcloudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.soundcloud.com");
            }
        });

        tumblrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.tumblr.com");
            }
        });

        githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideHomeContents();
                addToBookmarks.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://www.github.com");
            }
        });

        exitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(isMenuClicked)
                {
                    isMenuClicked = false;
                    menuCardx.setVisibility(View.GONE);
                }
                else
                {
                    isMenuClicked = true;
                    menuCardx.setVisibility(View.VISIBLE);
                }

            }
        });

        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.e("Click","I got clicked!");
                //viewPager.setCurrentItem(9);
                Intent mIntent = new Intent(getActivity(), DownloadsActivity.class);
                startActivity(mIntent);
                menuCardx.setVisibility(View.GONE);
                scrollTabs.setVisibility(View.GONE);

            }
        });

        bookmarksImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getActivity(), HistoryAndBookmarkActivity.class);
                startActivity(mIntent);
                scrollTabs.setVisibility(View.GONE);
            }
        });



        newsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent mIntent = new Intent(getActivity(), NewsFeedActivity.class);
                startActivity(mIntent);
            }
        });
        //hideHomeContents();






        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    Log.e("Click","Meh,I got clicked back");
                    if (scrollTabs.getVisibility() == View.VISIBLE) {
                        scrollTabs.setVisibility(View.GONE);
                    }
                     else if ((mCustomView == null) && mWebView.canGoBack())
                     {
                        mWebView.goBack();
                         //showHomeContents();

                     }
                     else if(!mWebView.canGoBack())
                    {
                        showHomeContents();
                        mWebView.setVisibility(View.GONE);
                    }
                    else {
                        Snackbar snackbar = Snackbar
                                .make(mWebView, getString(R.string.toast_exit), Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        helper_main.closeApp(activity, mWebView);
                                    }
                                });
                        snackbar.show();
                    }

            }
        });

        CardView context_1_Layout = (CardView) getActivity().findViewById(R.id.context_1_Layout);
        CardView context_2_Layout = (CardView) getActivity().findViewById(R.id.context_2_Layout);
        CardView context_3_Layout = (CardView) getActivity().findViewById(R.id.context_3_Layout);
        CardView context_4_Layout = (CardView) getActivity().findViewById(R.id.context_4_Layout);
        CardView context_5_Layout = (CardView) getActivity().findViewById(R.id.context_5_Layout);

        ImageView close1 = (ImageView)getActivity().findViewById(R.id.close_1);
        ImageView close2 = (ImageView)getActivity().findViewById(R.id.close_2);
        ImageView close3 = (ImageView)getActivity().findViewById(R.id.close_3);
        ImageView close4 = (ImageView)getActivity().findViewById(R.id.close_4);
        ImageView close5 = (ImageView)getActivity().findViewById(R.id.close_5);



        helper_toolbar.cardViewClickMenu(getActivity(), context_1_Layout, scrollTabs, 0, close1, viewPager, "empty", "1");
        helper_toolbar.cardViewClickMenu(getActivity(), context_2_Layout, scrollTabs, 1, close2, viewPager, "empty", "2");
        helper_toolbar.cardViewClickMenu(getActivity(), context_3_Layout, scrollTabs, 2, close3, viewPager, "empty", "3");
        helper_toolbar.cardViewClickMenu(getActivity(), context_4_Layout, scrollTabs, 3, close4, viewPager, "empty", "4");
        helper_toolbar.cardViewClickMenu(getActivity(), context_5_Layout, scrollTabs, 4, close5, viewPager, "empty", "5");






     /*   front1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //mWebView.goForward();

            }
        });*/

/*
        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);


            }
        });
        */






        tabs1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isTabsClicked)
                {
                    isTabsClicked = false;
                    scrollTabs.setVisibility(View.GONE);
                }
                else
                {
                    isTabsClicked = true;
                    scrollTabs.setVisibility(View.VISIBLE);
                }




            }
        });






        // setupViews

        helper_browser.setupViews(activity, viewPager, mWebView, editText, imageButton_up, imageButton_down, imageButton_left,
                imageButton_right, mSearchCard, horizontalScrollView);
        helper_webView.webView_Settings(activity, mWebView);
        helper_webView.webView_WebViewClient(activity, mWebView, urlBar);

        mWebChromeClient = new myWebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setScrollViewCallbacks(this);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebView.setDownloadListener(new DownloadListener() {

            public void onDownloadStart(final String url, String userAgent,
                                        final String contentDisposition, final String mimetype,
                                        long contentLength) {

                activity.registerReceiver(onComplete_download, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                final String filename= URLUtil.guessFileName(url, contentDisposition, mimetype);
                Snackbar snackbar = Snackbar
                        .make(mWebView, getString(R.string.toast_download_1) + " " + filename, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                                DownloadManager dm = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
                                dm.enqueue(request);

                                Snackbar.make(mWebView, getString(R.string.toast_download) + " " + filename , Snackbar.LENGTH_LONG).show();
                            }
                        });
                snackbar.show();
            }
        });

        addToBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.e("Book","clicked"+mWebView.getOriginalUrl());
                helper_main.save_bookmark(getActivity(),"Bookmark",mWebView.getUrl(),mWebView);

            }
        });


        // other stuff

        Utils_AdBlocker.init(activity);
        registerForContextMenu(mWebView);

        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BrowserSearchEvent event)
    {
        Log.e("Bus","Bus works");
        launchNuke(event.getMessage());
    }

     private void launchNuke(String text)
     {
         hideHomeContents();
         addToBookmarks.setVisibility(View.VISIBLE);
         if(mWebView.getVisibility()!=View.VISIBLE)
             mWebView.setVisibility(View.VISIBLE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        String searchEngine = sharedPref.getString("searchEngine", "https://www.google.de/search?&q=");
        String wikiLang = sharedPref.getString("wikiLang", "en");

        if (text.startsWith("http")) {
            mWebView.loadUrl(text);
        } else if (text.startsWith("www.")) {
            mWebView.loadUrl("https://" + text);
        } else if (Patterns.WEB_URL.matcher(text).matches()) {
            mWebView.loadUrl("https://" + text);
        } else {

            String subStr = null;

            if (text.length() > 3) {
                subStr=text.substring(3);
            }

            if (text.contains(".w ")) {
                mWebView.loadUrl("https://" + wikiLang + ".wikipedia.org/wiki/Spezial:Suche?search=" + subStr);
            } else if (text.startsWith(".f ")) {
                mWebView.loadUrl("https://www.flickr.com/search/?advanced=1&license=2%2C3%2C4%2C5%2C6%2C9&text=" + subStr);
            } else  if (text.startsWith(".m ")) {
                mWebView.loadUrl("https://metager.de/meta/meta.ger3?focus=web&eingabe=" + subStr);
            } else if (text.startsWith(".g ")) {
                mWebView.loadUrl("https://github.com/search?utf8=✓&q=" + subStr);
            } else if (text.startsWith(".e ")) {
                mWebView.loadUrl("https://encrypted.google.com/search?q=" + subStr);
            } else  if (text.startsWith(".s ")) {
                if (Locale.getDefault().getLanguage().contentEquals("de")){
                    mWebView.loadUrl("https://startpage.com/do/search?query=" + subStr + "&lui=deutsch&l=deutsch");
                } else {
                    mWebView.loadUrl("https://startpage.com/do/search?query=" + subStr);
                }
            } else if (text.startsWith(".G ")) {
                if (Locale.getDefault().getLanguage().contentEquals("de")){
                    mWebView.loadUrl("https://www.google.de/search?&q=" + subStr);
                } else {
                    mWebView.loadUrl("https://www.google.com/search?&q=" + subStr);
                }
            } else  if (text.startsWith(".y ")) {
                if (Locale.getDefault().getLanguage().contentEquals("de")){
                    mWebView.loadUrl("https://www.youtube.com/results?hl=de&gl=DE&search_query=" + subStr);
                } else {
                    mWebView.loadUrl("https://www.youtube.com/results?search_query=" + subStr);
                }
            } else  if (text.startsWith(".d ")) {
                if (Locale.getDefault().getLanguage().contentEquals("de")){
                    mWebView.loadUrl("https://duckduckgo.com/?q=" + subStr + "&kl=de-de&kad=de_DE&k1=-1&kaj=m&kam=osm&kp=-1&kak=-1&kd=1&t=h_&ia=web");
                } else {
                    mWebView.loadUrl("https://duckduckgo.com/?q=" + subStr);
                }
            } else {
                if (searchEngine.contains("https://duckduckgo.com/?q=")) {
                    if (Locale.getDefault().getLanguage().contentEquals("de")){
                        mWebView.loadUrl("https://duckduckgo.com/?q=" + text + "&kl=de-de&kad=de_DE&k1=-1&kaj=m&kam=osm&kp=-1&kak=-1&kd=1&t=h_&ia=web");
                    } else {
                        mWebView.loadUrl("https://duckduckgo.com/?q=" + text);
                    }
                } else if (searchEngine.contains("https://metager.de/meta/meta.ger3?focus=web&eingabe=")) {
                    if (Locale.getDefault().getLanguage().contentEquals("de")){
                        mWebView.loadUrl("https://metager.de/meta/meta.ger3?focus=web&eingabe=" + text);
                    } else {
                        mWebView.loadUrl("https://metager.de/meta/meta.ger3?focus=web&eingabe=" + text +"&focus=web&encoding=utf8&lang=eng");
                    }
                } else if (searchEngine.contains("https://startpage.com/do/search?query=")) {
                    if (Locale.getDefault().getLanguage().contentEquals("de")){
                        mWebView.loadUrl("https://startpage.com/do/search?query=" + text + "&lui=deutsch&l=deutsch");
                    } else {
                        mWebView.loadUrl("https://startpage.com/do/search?query=" + text);
                    }
                }else {
                    mWebView.loadUrl(searchEngine + text);
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri[] results = null;
        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        WebView.HitTestResult result = mWebView.getHitTestResult();
        final String url = result.getExtra();
        final AlertDialog dialog;
        final View dialogView = View.inflate(activity, R.layout.dialog_context, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if(url != null) {

            if(result.getType() == WebView.HitTestResult.IMAGE_TYPE){

                builder.setView(dialogView);
                builder.setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                dialog = builder.create();
                dialog.show();

                try {
                    sharePath = URLUtil.guessFileName(url, null, null);
                } catch (Exception e) {
                    sharePath = helper_webView.getDomain(activity, url) + url.substring(url.lastIndexOf('/') + 1);
                }

                shareFile = helper_main.newFile(sharePath);

                TextView menu_share_link_copy = (TextView) dialogView.findViewById(R.id.menu_share_link_copy);
                menu_share_link_copy.setText(R.string.context_saveImage);
                LinearLayout menu_share_link_copy_Layout = (LinearLayout) dialogView.findViewById(R.id.menu_share_link_copy_Layout);
                menu_share_link_copy_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Uri source = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(source);
                            request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, sharePath);
                            DownloadManager dm = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
                            dm.enqueue(request);
                            Snackbar.make(mWebView, activity.getString(R.string.context_saveImage_toast) + " " + sharePath , Snackbar.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(mWebView, R.string.toast_perm , Snackbar.LENGTH_SHORT).show();
                        }
                        activity.registerReceiver(onComplete_download, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        dialog.cancel();
                    }
                });

                TextView menu_share_link = (TextView) dialogView.findViewById(R.id.menu_share_link);
                menu_share_link.setText(R.string.context_shareImage);
                LinearLayout menu_share_link_Layout = (LinearLayout) dialogView.findViewById(R.id.menu_share_link_Layout);
                menu_share_link_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Uri source = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(source);
                            request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, sharePath);
                            DownloadManager dm = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
                            dm.enqueue(request);

                            Snackbar.make(mWebView, activity.getString(R.string.context_saveImage_toast) + " " + sharePath , Snackbar.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(mWebView, R.string.toast_perm , Snackbar.LENGTH_SHORT).show();
                        }
                        activity.registerReceiver(onComplete_share, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        dialog.cancel();
                    }
                });

                TextView menu_save_readLater = (TextView) dialogView.findViewById(R.id.menu_save_readLater);
                menu_save_readLater.setText(R.string.menu_save_readLater);
                LinearLayout menu_save_readLater_Layout = (LinearLayout) dialogView.findViewById(R.id.menu_save_readLater_Layout);
                menu_save_readLater_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DbAdapter_ReadLater db = new DbAdapter_ReadLater(activity);
                        db.open();
                        if(db.isExist(helper_main.secString(mWebView.getUrl()))){
                            Snackbar.make(editText, activity.getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                        }else{
                            db.insert(helper_main.secString(helper_webView.getDomain(activity, url)), helper_main.secString(url), "", "", helper_main.createDate_norm());
                            Snackbar.make(mWebView, R.string.bookmark_added, Snackbar.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                });

                contextMenu(dialogView, url, dialog);

            } else if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {

                builder.setView(dialogView);
                builder.setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        dialog.cancel();
                    }
                });

                dialog = builder.create();
                dialog.show();

                LinearLayout context_save_Layout = (LinearLayout) dialogView.findViewById(R.id.context_save_Layout);
                context_save_Layout.setVisibility(View.VISIBLE);
                context_save_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String filename = url.substring(url.lastIndexOf("/")+1);
                        dialog.cancel();
                        Snackbar snackbar = Snackbar
                                .make(mWebView, getString(R.string.toast_download_1) + " " + filename, Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            Uri source = Uri.parse(url);
                                            DownloadManager.Request request = new DownloadManager.Request(source);
                                            request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                                            request.allowScanningByMediaScanner();
                                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                                            DownloadManager dm = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
                                            dm.enqueue(request);
                                            Snackbar.make(mWebView, getString(R.string.toast_download) + " " + filename , Snackbar.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Snackbar.make(mWebView, R.string.toast_perm , Snackbar.LENGTH_SHORT).show();
                                        }
                                        activity.registerReceiver(onComplete_download, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                    }
                                });
                        snackbar.show();
                    }
                });

                LinearLayout menu_share_link_copy_Layout = (LinearLayout) dialogView.findViewById(R.id.menu_share_link_copy_Layout);
                menu_share_link_copy_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText("text", url));
                        Snackbar.make(mWebView, R.string.context_linkCopy_toast, Snackbar.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                LinearLayout menu_share_link_Layout = (LinearLayout) dialogView.findViewById(R.id.menu_share_link_Layout);
                menu_share_link_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                        sendIntent.setType("text/plain");
                        activity.startActivity(Intent.createChooser(sendIntent, activity.getResources()
                                .getString(R.string.app_share_link)));
                        dialog.cancel();
                    }
                });

                LinearLayout menu_save_readLater_Layout = (LinearLayout) dialogView.findViewById(R.id.menu_save_readLater_Layout);
                menu_save_readLater_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DbAdapter_ReadLater db = new DbAdapter_ReadLater(activity);
                        db.open();
                        if(db.isExist(helper_main.secString(mWebView.getUrl()))){
                            Snackbar.make(editText, activity.getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                        }else{
                            db.insert(helper_main.secString(helper_webView.getDomain(activity, url)), helper_main.secString(url), "", "", helper_main.createDate_norm());
                            Snackbar.make(mWebView, R.string.bookmark_added, Snackbar.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                });

                contextMenu(dialogView, url, dialog);
            }
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        if (scrollState == ScrollState.UP) {

            //mageButton_up.setVisibility(View.VISIBLE);
            //imageButton_down.setVisibility(View.VISIBLE);
            imageButton_left.setVisibility(View.GONE);
            imageButton_right.setVisibility(View.GONE);

            if (sharedPref.getString ("fullscreen", "2").equals("2") || sharedPref.getString ("fullscreen", "2").equals("3")){
                mSearchCard.setVisibility(View.GONE);
            }

        } else if (scrollState == ScrollState.DOWN) {

            urlBar.setText(mWebView.getTitle());
            helper_browser.setNavArrows(mWebView, imageButton_left, imageButton_right);
            //imageButton_up.setVisibility(View.GONE);
            //imageButton_down.setVisibility(View.GONE);
            mSearchCard.setVisibility(View.VISIBLE);

        } /*else {
            imageButton_up.setVisibility(View.GONE);
            imageButton_down.setVisibility(View.GONE);
        }*/
    }

    public void hideHomeContents()
    {
        mSearchCard.setVisibility(View.GONE);
        socialCardBox.setVisibility(View.GONE);
        newsCard.setVisibility(View.GONE);
        rView.setVisibility(View.GONE);

    }

    public void showHomeContents()
    {
        socialCardBox.setVisibility(View.VISIBLE);
        newsCard.setVisibility(View.VISIBLE);
        rView.setVisibility(View.VISIBLE);

    }

    //Retrofit API call to fetch news
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

                    /*List<Article> articleX = new ArrayList<Article>();
                    articleX.add(articles.get(0));
                    articleX.add(articles.get(1));*/

                    TopNewsAdapter adapter = new TopNewsAdapter(articles);
                    rView.setAdapter(adapter);
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private class myWebChromeClient extends WebChromeClient {

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            Log.i(TAG, "onGeolocationPermissionsShowPrompt()");

            final boolean remember = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.app_location_title);
            builder.setMessage(R.string.app_location_message)
                    .setCancelable(true).setPositiveButton(R.string.app_location_allow, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // origin, allow, remember
                    callback.invoke(origin, true, remember);
                }
            }).setNegativeButton(R.string.app_location_allow_not, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // origin, allow, remember
                    callback.invoke(origin, false, remember);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @SuppressLint("SetJavaScriptEnabled")
        public void onProgressChanged(WebView view, int progress) {

            sharedPref.edit().putString("tab_" + tab_number, helper_webView.getTitle(activity, mWebView)).apply();
            progressBar.setProgress(progress);
            progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);

            try {
                String whiteList = sharedPref.getString("whiteList", "");

                if (whiteList.contains(helper_webView.getDomain(activity, mWebView.getUrl()))) {
                    mWebView.getSettings().setJavaScriptEnabled(true);
                } else {
                    if (sharedPref.getString("started", "file:///android_asset/home.html").equals("yes")) {
                        if (sharedPref.getString("java_string", "True").equals(activity.getString(R.string.app_yes))){
                            mWebView.getSettings().setJavaScriptEnabled(true);
                        } else {
                            mWebView.getSettings().setJavaScriptEnabled(true);
                        }
                    } else {
                        if (sharedPref.getBoolean ("java", false)){
                            mWebView.getSettings().setJavaScriptEnabled(true);
                        } else {
                            mWebView.getSettings().setJavaScriptEnabled(true);
                        }
                    }
                }
            } catch (Exception e) {
                // Error occurred while creating the File
                Log.e(TAG, "Browser Error", e);
            }

            if (progress < 10) {
                if (scrollTabs.getVisibility() == View.VISIBLE) {
                    scrollTabs.setVisibility(View.GONE);
                }
            }

            if (progress == 100) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            int width = mWebView.getWidth();
                            int high = (width/175) * 100;
                            Bitmap bitmap = Bitmap.createBitmap(width, high , Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            mWebView.draw(canvas);

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                            File file = new File(activity.getFilesDir() + "/tab_" + tab_number + ".jpg");
                            file.createNewFile();
                            FileOutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(bytes.toByteArray());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 100);

                /*if (imageButton_up.getVisibility() != View.VISIBLE) {
                    helper_browser.setNavArrows(mWebView, imageButton_left, imageButton_right);
                }*/
            }
        }

        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = helper_browser.createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException e) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", e);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_share_file));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, REQUEST_CODE_LOLLIPOP);

            return true;
        }

        @Override
        public void onShowCustomView(View view,CustomViewCallback callback) {

            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            if (sharedPref.getString ("fullscreen", "2").equals("2") || sharedPref.getString ("fullscreen", "2").equals("4")){
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            mCustomView = view;
            mWebView.setVisibility(View.GONE);
            mSearchCard.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
            if (mCustomView == null)
                return;

            mWebView.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);
            mSearchCard.setVisibility(View.VISIBLE);

            if (sharedPref.getString ("fullscreen", "2").equals("2") || sharedPref.getString ("fullscreen", "2").equals("4")){
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }
    }


    // Methods

    private void contextMenu (View dialogView, String url, Dialog dialog) {
        HorizontalScrollView scrollTabs = (HorizontalScrollView) dialogView.findViewById(R.id.scrollTabs);

        TextView context_1 = (TextView) dialogView.findViewById(R.id.context_1);
        ImageView context_1_preView = (ImageView) dialogView.findViewById(R.id.context_1_preView);
        CardView context_1_Layout = (CardView) dialogView.findViewById(R.id.context_1_Layout);
        ImageView close_1 = (ImageView) dialogView.findViewById(R.id.close_1);
        helper_toolbar.cardViewClickMenu(getActivity(), context_1_Layout, scrollTabs, 0, close_1, viewPager, url, dialog, "0");
        helper_toolbar.toolBarPreview(getActivity(), context_1,context_1_preView, 0, helper_browser.tab_1(getActivity()), "/tab_0.jpg", close_1);

        TextView context_2 = (TextView) dialogView.findViewById(R.id.context_2);
        ImageView context_2_preView = (ImageView) dialogView.findViewById(R.id.context_2_preView);
        CardView context_2_Layout = (CardView) dialogView.findViewById(R.id.context_2_Layout);
        ImageView close_2 = (ImageView) dialogView.findViewById(R.id.close_2);
        helper_toolbar.cardViewClickMenu(getActivity(), context_2_Layout, scrollTabs, 1, close_2, viewPager, url, dialog, "1");
        helper_toolbar.toolBarPreview(getActivity(), context_2,context_2_preView, 1, helper_browser.tab_2(getActivity()), "/tab_1.jpg", close_2);

        TextView context_3 = (TextView) dialogView.findViewById(R.id.context_3);
        ImageView context_3_preView = (ImageView) dialogView.findViewById(R.id.context_3_preView);
        CardView context_3_Layout = (CardView) dialogView.findViewById(R.id.context_3_Layout);
        ImageView close_3 = (ImageView) dialogView.findViewById(R.id.close_3);
        helper_toolbar.cardViewClickMenu(getActivity(), context_3_Layout, scrollTabs, 2, close_3, viewPager, url, dialog, "2");
        helper_toolbar.toolBarPreview(getActivity(), context_3,context_3_preView, 2, helper_browser.tab_3(getActivity()), "/tab_2.jpg", close_3);

        TextView context_4 = (TextView) dialogView.findViewById(R.id.context_4);
        ImageView context_4_preView = (ImageView) dialogView.findViewById(R.id.context_4_preView);
        CardView context_4_Layout = (CardView) dialogView.findViewById(R.id.context_4_Layout);
        ImageView close_4 = (ImageView) dialogView.findViewById(R.id.close_4);
        helper_toolbar.cardViewClickMenu(getActivity(), context_4_Layout, scrollTabs, 3, close_4, viewPager, url, dialog, "3");
        helper_toolbar.toolBarPreview(getActivity(), context_4,context_4_preView, 3, helper_browser.tab_4(getActivity()), "/tab_3.jpg", close_4);

        TextView context_5 = (TextView) dialogView.findViewById(R.id.context_5);
        ImageView context_5_preView = (ImageView) dialogView.findViewById(R.id.context_5_preView);
        CardView context_5_Layout = (CardView) dialogView.findViewById(R.id.context_5_Layout);
        ImageView close_5 = (ImageView) dialogView.findViewById(R.id.close_5);
        helper_toolbar.cardViewClickMenu(getActivity(), context_5_Layout, scrollTabs, 4, close_5, viewPager, url, dialog, "4");
        helper_toolbar.toolBarPreview(getActivity(), context_5,context_5_preView, 4, helper_browser.tab_5(getActivity()), "/tab_4.jpg", close_5);

    }

    public void doBack()
    {
        if(!mWebView.canGoBack())
        {
            showHomeContents();
            mWebView.setVisibility(View.GONE);
        }
        //BackPressed in activity will call this;
        if (scrollTabs.getVisibility() == View.VISIBLE) {
            scrollTabs.setVisibility(View.GONE);
        } else if (inCustomView()) {
            hideCustomView();
        } else if ((mCustomView == null) && mWebView.canGoBack())
        {
            //showHomeContents();

            mWebView.goBack();

        }
        else
        {

            Snackbar snackbar = Snackbar
                    .make(mWebView, getString(R.string.toast_exit), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            helper_main.closeApp(activity, mWebView);
                        }
                    });
            snackbar.show();
        }
    }

    public void fragmentAction () {

        /*if (sharedPref.getInt("closeApp", 1) == 1) {
            helper_main.closeApp(activity, mWebView);
        }*/

        //editText.setVisibility(View.GONE);

        setTitle();
        mWebView.findAllAsync("");
        tab_number = String.valueOf(viewPager.getCurrentItem());
        sharedPref.edit().putInt("tab", viewPager.getCurrentItem()).apply();
        sharedPref.edit().putInt("keyboard", 0).apply();

        final String URL = sharedPref.getString("openURL","");

        if (URL.equals(mWebView.getUrl()) || URL.equals("") && sharedPref.getString("tab_" + tab_number, "").length() > 0) {
            Log.i(TAG, "Tab switched");
        } else if (URL.equals("settings")) {
            mWebView.reload();
        } else if (URL.equals("settings_recreate")) {
            getActivity().recreate();
        } else if (URL.equals("copyLogin")) {
            Snackbar snackbar = Snackbar
                    .make(mWebView, R.string.pass_copy_userName, Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setPrimaryClip(ClipData.newPlainText("userName", sharedPref.getString("copyUN", "")));

                            Snackbar snackbar = Snackbar
                                    .make(mWebView, R.string.pass_copy_userPW, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                            clipboard.setPrimaryClip(ClipData.newPlainText("userName", sharedPref.getString("copyPW", "")));
                                        }
                                    });
                            snackbar.show();
                        }
                    });
            snackbar.show();
        } else if (URL.contains("openLogin")) {
            Snackbar snackbar = Snackbar
                    .make(mWebView, R.string.pass_copy_userName, Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setPrimaryClip(ClipData.newPlainText("userName", sharedPref.getString("copyUN", "")));

                            Snackbar snackbar = Snackbar
                                    .make(mWebView, R.string.pass_copy_userPW, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                            clipboard.setPrimaryClip(ClipData.newPlainText("userName", sharedPref.getString("copyPW", "")));
                                        }
                                    });
                            snackbar.show();
                        }
                    });
            snackbar.show();
            mWebView.loadUrl(URL.replace("openLogin", "file:///android_asset/home.html"));
        } else if (sharedPref.getString("tab_" + tab_number, "").isEmpty() && URL.length() == 0) {
            mWebView.loadUrl(URL.replace("openLogin", "file:///android_asset/home.html"));



        } else {
            mWebView.loadUrl(URL);
        }
    }

    private void setTitle () {

        String title = helper_webView.getTitle (activity, mWebView);
        sharedPref.edit().putString("webView_url", mWebView.getUrl()).apply();

        if (title.isEmpty()) {
            urlBar.setText(getString(R.string.app_name));
        } else{
            urlBar.setText(title);
        }
    }

    private void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    private void screenshot() {

        sharePath = helper_webView.getDomain(activity, mWebView.getUrl()) + "_" + helper_main.createDate_sec() + ".jpg";
        shareFile = helper_main.newFile(sharePath);

        try{
            mWebView.measure(View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mWebView.layout(0, 0, mWebView.getMeasuredWidth(), mWebView.getMeasuredHeight());
            mWebView.setDrawingCacheEnabled(true);
            mWebView.buildDrawingCache();
            bitmap = Bitmap.createBitmap(mWebView.getMeasuredWidth(),
                    mWebView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            int iHeight = bitmap.getHeight();
            canvas.drawBitmap(bitmap, 0, iHeight, paint);
            mWebView.draw(canvas);

        }catch (OutOfMemoryError e) {
            e.printStackTrace();
            Snackbar.make(mWebView, R.string.toast_screenshot_failed, Snackbar.LENGTH_SHORT).show();
        }

        if (bitmap != null) {

            OutputStream os;
            try {
                os = new FileOutputStream(shareFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }


            try {
                OutputStream fOut;
                fOut = new FileOutputStream(shareFile);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
                fOut.flush();
                fOut.close();
                bitmap.recycle();

                Snackbar snackbar = Snackbar
                        .make(mWebView, getString(R.string.context_saveImage_toast) + " " + shareFile.getName() +
                                ". " + getString(R.string.app_open), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.toast_yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewPager.setCurrentItem(9);
                            }
                        });
                snackbar.show();

                Uri uri = Uri.fromFile(shareFile);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                activity.sendBroadcast(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(mWebView, R.string.toast_perm, Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    // Receivers

    private final BroadcastReceiver onComplete_download = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Snackbar snackbar = Snackbar
                    .make(mWebView, activity.getString(R.string.app_open), Snackbar.LENGTH_LONG)
                    .setAction(activity.getString(R.string.toast_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewPager.setCurrentItem(9);
                        }
                    });
            snackbar.show();
            activity.unregisterReceiver(onComplete_download);
        }
    };

    private final BroadcastReceiver onComplete_share = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            Uri myUri= Uri.fromFile(shareFile);
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, myUri);
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, helper_webView.getTitle (activity, mWebView));
            sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
            activity.startActivity(Intent.createChooser(sharingIntent, (activity.getString(R.string.app_share_image))));
            activity.unregisterReceiver(onComplete_share);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = getActivity().findViewById(R.id.action_history);
                if (v != null) {
                    v.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            mWebView.reload();
                            return true;
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        helper_browser.prepareMenu(activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        horizontalScrollView.setVisibility(View.GONE);

        int id = item.getItemId();

        if (id == R.id.action_history) {
            viewPager.setCurrentItem(7);
            scrollTabs.setVisibility(View.GONE);
        }

        if (id == R.id.action_search_chooseWebsite) {
            helper_editText.editText_searchWeb(editText, activity);
        }



        if (id == R.id.action_toggle) {
            helper_browser.switcher(activity, mWebView, urlBar, viewPager);
        }

        if (id == R.id.menu_save_screenshot) {
            screenshot();
        }

        if (id == R.id.menu_save_bookmark) {
            urlBar.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            helper_editText.showKeyboard(activity, editText, 2, helper_webView.getTitle(activity, mWebView), getString(R.string.app_search_hint_bookmark));
        }

        if (id == R.id.menu_save_readLater) {
            helper_main.save_readLater(getActivity(), helper_webView.getTitle(activity, mWebView), mWebView.getUrl(), mWebView);
        }



        if (id == R.id.menu_createShortcut) {
            helper_main.installShortcut(getActivity(), helper_webView.getTitle(activity, mWebView), mWebView.getUrl(), mWebView);
        }

        if (id == R.id.menu_share_screenshot) {
            screenshot();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/png");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, helper_webView.getTitle (activity, mWebView));
            sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
            Uri bmpUri = Uri.fromFile(shareFile);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share_screenshot))));
        }

        if (id == R.id.menu_share_link) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, helper_webView.getTitle (activity, mWebView));
            sharingIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
            startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share_link))));
        }

        if (id == R.id.menu_share_link_copy) {
            String  url = mWebView.getUrl();
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("text", url));
            Snackbar.make(mWebView, R.string.context_linkCopy_toast, Snackbar.LENGTH_SHORT).show();
        }

        if (id == R.id.action_downloads) {
            viewPager.setCurrentItem(9);
            scrollTabs.setVisibility(View.GONE);
        }

        if (id == R.id.action_search_go) {

            String text = editText.getText().toString();
            helper_webView.openURL(activity, mWebView, editText.getText().toString());
            helper_editText.hideKeyboard(activity, editText, 0, text, getString(R.string.app_search_hint));
            urlBar.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
        }




        if (id == R.id.action_search_onSite) {
            urlBar.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            helper_editText.showKeyboard(activity, editText, 1, "", getString(R.string.app_search_hint_site));
        }

        if (id == R.id.action_search_onSite_go) {
            String text = editText.getText().toString();
            helper_editText.hideKeyboard(activity, editText, 4, getString(R.string.app_search) + " " + text, getString(R.string.app_search_hint_site));
            mWebView.findAllAsync(text);
            editText.setVisibility(View.GONE);
            urlBar.setVisibility(View.VISIBLE);
            urlBar.setText(getString(R.string.app_search) + " " + text);
        }

        if (id == R.id.action_prev) {
            mWebView.findNext(false);
        }

        if (id == R.id.action_next) {
            mWebView.findNext(true);
        }

        if (id == R.id.action_cancel) {
            urlBar.setVisibility(View.VISIBLE);
            urlBar.setText(helper_webView.getTitle (activity, mWebView));
            editText.setVisibility(View.GONE);
            mWebView.findAllAsync("");
            helper_editText.hideKeyboard(activity, editText, 0, helper_webView.getTitle (activity, mWebView), getString(R.string.app_search_hint));
        }

        if (id == R.id.action_save_bookmark) {
            String inputTag = editText.getText().toString().trim();
            helper_main.save_bookmark(activity, inputTag, mWebView.getUrl(), mWebView);
            helper_editText.hideKeyboard(activity, editText, 0, mWebView.getTitle(), getString(R.string.app_search_hint));
            urlBar.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
        }

        if (id == R.id.action_reload) {
            mWebView.reload();
        }

        if (id == R.id.action_help) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View dialogView = View.inflate(getActivity(), R.layout.dialog_help, null);

            TextView help_lists_title = (TextView) dialogView.findViewById(R.id.help_lists_title);
            TextView help_lists = (TextView) dialogView.findViewById(R.id.help_lists);

            TextView help_toolbar_title = (TextView) dialogView.findViewById(R.id.help_toolbar_title);
            TextView help_toolbar = (TextView) dialogView.findViewById(R.id.help_toolbar);

            TextView help_tabs_title = (TextView) dialogView.findViewById(R.id.help_tabs_title);
            TextView help_tabs = (TextView) dialogView.findViewById(R.id.help_tabs);

            TextView help_menu_title = (TextView) dialogView.findViewById(R.id.help_menu_title);
            TextView help_menu = (TextView) dialogView.findViewById(R.id.help_menu);

            TextView help_search_title = (TextView) dialogView.findViewById(R.id.help_search_title);
            TextView help_search = (TextView) dialogView.findViewById(R.id.help_search);

            help_lists_title.setText(helper_main.textSpannable(getString(R.string.help_lists_title)));
            help_lists.setText(helper_main.textSpannable(getString(R.string.help_lists)));

            help_toolbar_title.setText(helper_main.textSpannable(getString(R.string.help_toolbar_title)));
            help_toolbar.setText(helper_main.textSpannable(getString(R.string.help_toolbar)));

            help_tabs_title.setText(helper_main.textSpannable(getString(R.string.help_tabs_title)));
            help_tabs.setText(helper_main.textSpannable(getString(R.string.help_tabs)));

            help_menu_title.setText(helper_main.textSpannable(getString(R.string.help_menu_title)));
            help_menu.setText(helper_main.textSpannable(getString(R.string.help_menu)));

            help_search_title.setText(helper_main.textSpannable(getString(R.string.help_search_title)));
            help_search.setText(helper_main.textSpannable(getString(R.string.help_search)));

            builder.setView(dialogView);
            builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}