package com.browser.codedady;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class NewsReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);

        Bundle extras = getIntent().getExtras();
        String title = extras.getString("TITLE");
        String desc = extras.getString("DESC");
        String url = extras.getString("IMAGE");

        ImageView newsImage = (ImageView)findViewById(R.id.newsImageDetail);
        TextView titleText = (TextView)findViewById(R.id.titleTextNewsDetail);
        TextView descText = (TextView)findViewById(R.id.descTextNewsDetail);

        titleText.setText(title);
        descText.setText(desc);
        Picasso.with(this).load(url).into(newsImage);


    }
}
