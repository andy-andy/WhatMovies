package com.tarapus.whatmovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.tarapus.whatmovies.R;
import com.tarapus.whatmovies.fragments.DetailFragment;
import com.tarapus.whatmovies.fragments.MainActivityFragment;
import com.tarapus.whatmovies.network.MovieResponse;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.ListActionListener,
        DetailFragment.DetailsActionListener {

    private final static String TAG_MOVIE_LIST = "movie_list_fragment";

    private final static String TAG_DETAILS = "details_fragment";

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Optional
    @InjectView(R.id.details_placeholder)
    FrameLayout mDetailsPlaceholder;

    private MainActivityFragment mMoviesFragment;

    private boolean isDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        isDualPane = (mDetailsPlaceholder != null);
        if (savedInstanceState == null) {
            mMoviesFragment = MainActivityFragment.getInstance(isDualPane);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_list_placeholder, mMoviesFragment, TAG_MOVIE_LIST)
                    .commit();
        } else {
            mMoviesFragment = (MainActivityFragment) getSupportFragmentManager()
                    .findFragmentByTag(TAG_MOVIE_LIST);
            mMoviesFragment.setDualPane(isDualPane);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // check default or last selected option
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortParam = prefs.getString(getString(R.string.prefs_sort_order),
                getString(R.string.sort_order_popularity));
        int selectedId;
        if (getString(R.string.sort_order_rating).equals(sortParam)) {
            selectedId = R.id.sort_order_rating;
        } else if (getString(R.string.sort_order_favorites).equals(sortParam)) {
            selectedId = R.id.sort_order_favorites;
        } else {
            selectedId = R.id.sort_order_popularity;
        }
        menu.findItem(selectedId).setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String param = null;
        switch (item.getItemId()) {
            case R.id.sort_order_popularity:
                param = getString(R.string.sort_order_popularity);
                break;
            case R.id.sort_order_rating:
                param = getString(R.string.sort_order_rating);
                break;
            case R.id.sort_order_favorites:
                param = getString(R.string.sort_order_favorites);
                break;
        }
        if (param != null) {
            item.setChecked(true);
            mMoviesFragment.setSortOrder(param);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(MovieResponse.Movie movie, boolean isFavorite) {
        if (!isDualPane) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.MOVIE, movie);
            intent.putExtra(DetailFragment.FAVORITE, isFavorite);
            startActivity(intent);
        } else {
            DetailFragment fragment = DetailFragment.getInstance(movie, isFavorite);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_placeholder, fragment, TAG_DETAILS)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onEmptyMovieList() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(TAG_DETAILS);
        if (isDualPane && fragment != null) {
            fm.beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onFavoriteAction(long movieId) {
        mMoviesFragment.favoriteListChanged(movieId);
    }
}