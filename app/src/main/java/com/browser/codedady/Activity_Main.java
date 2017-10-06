package com.browser.codedady;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.browser.codedady.EventBus.BrowserSearchEvent;
import com.browser.codedady.helper.helper_webView;
import com.browser.codedady.newsPojo.MainPojo;
import com.browser.codedady.newsRetrofit.ApiClient;
import com.browser.codedady.newsRetrofit.ApiInterface;
import com.browser.codedady.utils.Key;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import com.browser.codedady.helper.Activity_settings;
import com.browser.codedady.helper.Activity_settings_start;
import com.browser.codedady.helper.class_CustomViewPager;
import com.browser.codedady.helper.helper_browser;
import com.browser.codedady.helper.helper_editText;
import com.browser.codedady.helper.helper_main;
import com.browser.codedady.fragments.Fragment_Bookmarks;
import com.browser.codedady.fragments.Fragment_Browser;
import com.browser.codedady.fragments.Fragment_Files;
import com.browser.codedady.fragments.Fragment_History;
import com.browser.codedady.fragments.Fragment_Pass;
import com.browser.codedady.fragments.Fragment_ReadLater;
import com.browser.codedady.helper.helper_toolbar;


import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.browser.codedady.R.id.scrollTabs;
import static com.browser.codedady.R.id.viewpager;

public class Activity_Main extends AppCompatActivity {


    // Views

    private class_CustomViewPager viewPager;
    private TextView urlBar;
    private TextView listBar;
    private EditText editTextURL;
    //private Toolbar toolbar;
    private ObservableWebView mWebView;
    private EditText viewThatFuckinWorks;


    // Others

    private SharedPreferences sharedPref;


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity activity = Activity_Main.this;

        helper_main.setTheme(activity);

        setContentView(R.layout.activity_main);
        mWebView = (ObservableWebView) findViewById(R.id.webView);
        WebView.enableSlowWholeDocumentDraw();

        //makeRetrofitCall();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putBoolean("isOpened", true).apply();

        helper_main.checkPin(activity);
        helper_main.onStart(activity);
        helper_main.grantPermissionsStorage(activity);
        helper_browser.resetTabs(activity);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        // show changelog

        final String versionName = BuildConfig.VERSION_NAME;
        String oldVersionName = sharedPref.getString("oldVersionName", "0.0");

        if (!oldVersionName.equals(versionName)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.app_changelog);
            builder.setMessage(helper_main.textSpannable(activity.getString(R.string.changelog_text)));
            builder.setPositiveButton(
                    getString(R.string.toast_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sharedPref.edit().putString("oldVersionName", versionName).apply();
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


        // find Views

        urlBar = (TextView) findViewById(R.id.urlBar);
        listBar = (TextView) findViewById(R.id.listBar);
        editTextURL = (EditText) findViewById(R.id.editTextSearch);
        viewPager = (class_CustomViewPager) findViewById(viewpager);
        viewPager.setOffscreenPageLimit(10);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);

        viewThatFuckinWorks = (EditText)findViewById(R.id.editTextFuck);

        viewThatFuckinWorks.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    EventBus.getDefault().post(new BrowserSearchEvent(viewThatFuckinWorks.getText().toString()));
                    helper_editText.hideKeyboard(activity,viewThatFuckinWorks,0,"","");
                    return  true;
                }
                return false;
            }
        });

        if (sharedPref.getBoolean ("swipe", false)){
            sharedPref.edit().putString("swipe_string", activity.getString(R.string.app_yes)).apply();
            viewPager.setPagingEnabled(true);
        } else {
            sharedPref.edit().putString("swipe_string", activity.getString(R.string.app_no)).apply();
            viewPager.setPagingEnabled(false);
        }

        viewPager.addOnPageChangeListener(new class_CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                helper_editText.hideKeyboard(activity, editTextURL, 0, "", getString(R.string.app_search_hint));
                //helper_toolbar.toolbarGestures(activity, toolbar, viewPager);

                assert getSupportActionBar() != null;

                if (position < 5) {
                    //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    urlBar.setVisibility(View.VISIBLE);
                    listBar.setVisibility(View.GONE);
                } else {
                    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    urlBar.setVisibility(View.GONE);
                    listBar.setVisibility(View.VISIBLE);
                }

                if (position < 5) {
                    Fragment_Browser fragment = (Fragment_Browser) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    fragment.fragmentAction();
                } /*else if (position == 5) {
                    Fragment_Bookmarks fragment = (Fragment_Bookmarks) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    fragment.fragmentAction();
                } */else if (position == 6) {
                    Fragment_ReadLater fragment = (Fragment_ReadLater) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    fragment.fragmentAction();
                } /*else if (position == 7) {
                    Fragment_History fragment = (Fragment_History) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    fragment.fragmentAction();
                } */else if (position == 8) {
                    Fragment_Pass fragment = (Fragment_Pass) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    fragment.fragmentAction();
                } /*else {
                    Fragment_Files fragment = (Fragment_Files) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    fragment.fragmentAction();
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        //helper_toolbar.toolbarGestures(activity, toolbar, viewPager);
        onNewIntent(getIntent());
    }

    @Override
    public void onResume(){
        super.onResume();

        final int position = viewPager.getCurrentItem();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (position < 5) {
                    Fragment_Browser fragment = (Fragment_Browser) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    fragment.fragmentAction();
                }
            }
        }, 100);
    }

    protected void onNewIntent(final Intent intent) {

        String action = intent.getAction();
        Handler handler = new Handler();

        if (Intent.ACTION_SEND.equals(action)) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            String searchEngine = sharedPref.getString("searchEngine", "https://google.com/?q=");
            sharedPref.edit().putString("openURL", searchEngine + sharedText).apply();
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(0);
                }
            }, 250);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            String link = data.toString();
            sharedPref.edit().putString("openURL", link).apply();
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(0);
                }
            }, 250);
        } else if ("readLater".equals(action)) {
            sharedPref.edit().putInt("appShortcut", 1).apply();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(6);
                }
            }, 250);
        } else if ("bookmarks".equals(action)) {
            sharedPref.edit().putInt("appShortcut", 1).apply();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(5);
                }
            }, 250);
        } else if ("history".equals(action)) {
            sharedPref.edit().putInt("appShortcut", 1).apply();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(7);
                }
            }, 250);
        } else if ("pass".equals(action)) {
            sharedPref.edit().putInt("appShortcut", 1).apply();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(8);
                }
            }, 250);
        }
    }

    private void setupViewPager(final class_CustomViewPager viewPager) {

        final String startTab = sharedPref.getString("tabMain", "0");
        final int startTabInt = Integer.parseInt(startTab);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new Fragment_Browser(), String.valueOf(getString(R.string.app_name)));
        adapter.addFragment(new Fragment_Browser(), String.valueOf(getString(R.string.app_name)));
        adapter.addFragment(new Fragment_Browser(), String.valueOf(getString(R.string.app_name)));
        adapter.addFragment(new Fragment_Browser(), String.valueOf(getString(R.string.app_name)));
        adapter.addFragment(new Fragment_Browser(), String.valueOf(getString(R.string.app_name)));
        //adapter.addFragment(new Fragment_Bookmarks(), String.valueOf(getString(R.string.app_title_bookmarks)));
        adapter.addFragment(new Fragment_ReadLater(), String.valueOf(getString(R.string.app_title_readLater)));
        //adapter.addFragment(new Fragment_History(), String.valueOf(getString(R.string.app_title_history)));
        adapter.addFragment(new Fragment_Pass(), String.valueOf(getString(R.string.app_title_passStorage)));
        //adapter.addFragment(new Fragment_Files(), String.valueOf(getString(R.string.app_name)));

        viewPager.setAdapter(adapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(startTabInt,true);
            }
        }, 250);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);// add return null; to display only icons
        }
    }

    @Override
    public void onBackPressed() {

        if(viewPager.getCurrentItem() < 5) {
            Fragment_Browser fragment = (Fragment_Browser) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            fragment.doBack();
        } /*else if(viewPager.getCurrentItem() == 5) {
            Fragment_Bookmarks fragment = (Fragment_Bookmarks) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            fragment.doBack();
        }*/
        else if(viewPager.getCurrentItem() == 6) {
            Fragment_ReadLater fragment = (Fragment_ReadLater) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            fragment.doBack();
        } /*else if(viewPager.getCurrentItem() == 7) {
            Fragment_History fragment = (Fragment_History) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            fragment.doBack();
        }*/
         else if(viewPager.getCurrentItem() == 8) {
            Fragment_Pass fragment = (Fragment_Pass) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            fragment.doBack();
        } /*else if(viewPager.getCurrentItem() == 9) {
            Fragment_Files fragment = (Fragment_Files) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            fragment.doBack();
        }*/
    }
}