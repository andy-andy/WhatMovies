package com.tarapus.whatmovies.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tarapus.whatmovies.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get Intent
        Uri myImage = getIntent().getParcelableExtra("image_uri");
        String originalTitle = getIntent().getStringExtra("original_title");
        String overview = getIntent().getStringExtra("overview");
        String releaseDate = getIntent().getStringExtra("release_date");
        String voteAverage = getIntent().getStringExtra("vote_average");

        //Detail Image view
        ImageView image = (ImageView) findViewById(R.id.detail_view);
        Picasso.with(this).load(myImage).into(image);

        //Original Movie Title
        TextView originalTitleView = (TextView) findViewById(R.id.original_title);
        originalTitleView.setText(originalTitle + " (" + releaseDate + ")");
        //Movie Overview
        TextView overviewView = (TextView) findViewById(R.id.overview_view);
        overviewView.setText(overview);
        //Movie Vote Average
        TextView voteAverageView = (TextView) findViewById(R.id.vote_average);
        voteAverageView.setText("Vote Average: " + voteAverage + "/10");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
