package com.tarapus.whatmovies.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.tarapus.whatmovies.Adapters.MovieAdapter;
import com.tarapus.whatmovies.R;
import com.tarapus.whatmovies.activities.DetailActivity;
import com.tarapus.whatmovies.network.Movie;
import com.tarapus.whatmovies.network.MovieData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivityFragment extends Fragment {

    private MovieAdapter mMovieAdapter;
    private URL mUrl = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_most_popular:
                updateMovie();
                return true;

            case R.id.action_sort_by_vote_average:
                updateMovieSort();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Adapter
        mMovieAdapter = new MovieAdapter(getActivity(), new Movie[]{});

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_movies);
        gridView.setAdapter(mMovieAdapter);

        //On Click listener Detailed movie view
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri imageUri = Uri.parse("http://image.tmdb.org/t/p/w780/" +
                        mMovieAdapter.getItem(position).poster_path);
                String originalTitle = mMovieAdapter.getItem(position).original_title;
                String overview = mMovieAdapter.getItem(position).overview;
                String voteAverage = mMovieAdapter.getItem(position).vote_average;
                //Release Date. Substring: First 4 characters
                String releaseDate = mMovieAdapter.getItem(position).release_date;
                String releaseDateSub = releaseDate.substring(0, 4);

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("image_uri", imageUri);
                intent.putExtra("original_title", originalTitle);
                intent.putExtra("overview", overview);
                intent.putExtra("vote_average", voteAverage);
                intent.putExtra("release_date", releaseDateSub);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //URL builder method
    public void buildMovieUrl(String sort, String page) {
        //Contain API key
        String api_key = "API key here!!!";
        //Sort by popularity.desc by default Most popular
        //Sort by vote_average
        final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String API_KEY = "api_key";
        final String SORT_BY = "sort_by";
        final String PAGE = "page";

        Uri buildUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendQueryParameter(PAGE, page)
                .appendQueryParameter(SORT_BY, sort)
                .appendQueryParameter(API_KEY, api_key)
                .build();

        try {
            mUrl = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        FetchMovieTask movieTask = new FetchMovieTask(new Movie[]{});
        movieTask.execute();
    }

    public void updateMovie() {
        String page = "1";
        String sort = "popularity.desc";
        buildMovieUrl(sort, page);
    }

    public void updateMovieSort() {
        String page = "1";
        String sort = "vote_average.desc";
        buildMovieUrl(sort, page);
    }

    public class FetchMovieTask extends AsyncTask<URL, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private Movie[] movies;

        public FetchMovieTask(Movie[] movies) {
            this.movies = movies;
        }

        @Override
        protected Movie[] doInBackground(URL... urls) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr;

            try {
                Log.v(LOG_TAG, "Built URI " + mUrl.toString());

                // Create the request to Movies web, and open the connection
                urlConnection = (HttpURLConnection) mUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movie JSON String" + movieJsonStr);

                Gson gson = new Gson();
                MovieData movieData = gson.fromJson(movieJsonStr, MovieData.class);
                movies = movieData.results;

            } catch (
                    IOException e
                    ) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally

            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mMovieAdapter.addMovies(movies);
            }
        }
    }
}
