package com.tarapus.whatmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.tarapus.whatmovies.R;
import com.tarapus.whatmovies.fragments.DetailFragment;
import com.tarapus.whatmovies.network.MovieResponse;


public class DetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MovieResponse.Movie movie;
        boolean isFavorite;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            movie = extras.getParcelable(DetailFragment.MOVIE);
            isFavorite = extras.getBoolean(DetailFragment.FAVORITE);
        } else {
            throw new NullPointerException("No movie found in intent extras");
        }

        if (savedInstanceState == null) {
            DetailFragment fragment = DetailFragment.getInstance(movie, isFavorite);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
