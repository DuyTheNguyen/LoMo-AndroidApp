package com.example.davenguyen.lomoapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultActivity extends BaseActivity implements SearchView.OnQueryTextListener, Filter.FilterListener {
    //Declare variable for result list.
    private TextView resultTv;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    private ListView resultmoviesList;
    private ResultMovieListViewAdapter resultMovieListViewAdapter;
    private ArrayList<Movie> movieList = new ArrayList<Movie>();


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
    }

    private void initializeUI() {
        /**For Searching**/
        resultTv = findViewById(R.id.resultActivity_result);

        //Load movies from server
        loadMovies();
        // Locate the ListView
        resultmoviesList = (ListView) findViewById(R.id.listview);

        // Pass results to ResultMovieListViewAdapter Class
        resultMovieListViewAdapter = new ResultMovieListViewAdapter(this, movieList);

        // Binds the Adapter to the ListView
        resultMovieListViewAdapter.setFilterListener(this);
        resultmoviesList.setAdapter(resultMovieListViewAdapter);
        resultmoviesList.setTextFilterEnabled(true);

        // set up click listener
        resultmoviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = (Movie) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(SearchResultActivity.this, HomeActivity.class);

                ArrayList<Movie> movieList = new ArrayList<Movie>();
                movieList.add(movie);
                intent.putParcelableArrayListExtra("MOVIE", movieList);
                startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        resultMovieListViewAdapter.getFilter().filter(newText);


        // use to enable search view popup text
        if (TextUtils.isEmpty(newText)) {
            resultmoviesList.clearTextFilter();
        } else {
            resultmoviesList.setFilterText(newText.toString());
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Three dots on the top right
        getMenuInflater().inflate(R.menu.home, menu);
        //Configure search
        menu.findItem(R.id.search).setVisible(true);
        menu.findItem(R.id.searchFake).setVisible(false);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchMenuItem = menu.findItem(R.id.search);

        searchView = (SearchView) searchMenuItem.getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(this);

        return true;
    }

    //handles three dots
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            return true;
        }
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        if (id == R.id.search) {
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMovies() {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("allmovies_mobile");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Get all the genres
                Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                Log.d("DEBUG SEARCH", dataSnapshot.getValue().toString());
                Log.d("DEBUG SEARCH", dataSnapshot.getRef().toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Movie movie = postSnapshot.getValue(Movie.class);
                    movieList.add(movie);
                    Log.e("Get Data", movie.getName());
                    resultMovieListViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("READ_FAIELD", "The read failed: " + databaseError.getCode());
            }
        });
    }

    //Get the total number of search results
    @Override
    public void onFilterComplete(int i) {
        resultTv.setText("Result " + "(" + Integer.toString(i) + ")");
    }
}
