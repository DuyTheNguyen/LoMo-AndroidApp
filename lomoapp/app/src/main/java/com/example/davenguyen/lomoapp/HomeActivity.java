package com.example.davenguyen.lomoapp;


import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;


import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Movie resultMovie;
    private NavigationView navigationView;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set default fragment
        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragArea, homeFragment, "HomeFragment")
                    .commit();
        }
        if (getIntent().hasExtra("MOVIE")) {
            String result = "0";
            ArrayList<Movie> moviesList = getIntent().getParcelableArrayListExtra("MOVIE");
            resultMovie = moviesList.get(0);
            result = resultMovie.getName();
            Utility.MakeLongToastToast(this, result);
            MovieFragment movieFragment = MovieFragment.newInstance(resultMovie, resultMovie.getGenre());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragArea, movieFragment)
                    .commit();
        }

        initailizeUI();
    }

    private void initailizeUI() {

        //Remove back button on the left top
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        //Control the drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        //The three lines in top right
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        //If the drawer is opening then close it
        if (drawer.isDrawerOpen(navigationView)) {
            drawer.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String genre = "";
        if (id == R.id.nav_home) {
            //Create only one frag home

            Fragment homeFragment = getSupportFragmentManager().findFragmentByTag("HomeFragment");
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragArea, homeFragment, "HomeFragment")
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragArea, homeFragment, "HomeFragment")
                        .commit();
            /*HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragArea, homeFragment,"HomeFragment")
                    .commit();            */
            }
        } else if (id == R.id.nav_action) {
            genre = "Action";
        } else if (id == R.id.nav_comedy) {
            genre = "Comedy";
        } else if (id == R.id.nav_romance) {
            genre = "Romance";
        } else if (id == R.id.nav_scifi) {
            genre = "Sci-fi";
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.nav_movieList) {
            //TODO start the list
            startActivity(new Intent(this, MovieListActivity.class));
            return true;
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            return true;
        }
        //Pass the Genre to fragment
        if (!genre.equals("")) {
            GenreFragment genreFragment = GenreFragment.newInstance(genre);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragArea, genreFragment)
                    .commit();
        }
        item.setCheckable(true);
        navigationView.setCheckedItem(item.getItemId());
        //Close drawer when any item is selected
        drawer.closeDrawer(navigationView);
        return true;
    }

    //set title for action bar
    public void setActionBarTitle(String title) {
        toolbar.setTitle(title);
    }

    /**
     * override actionbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Three dots on the top right
        getMenuInflater().inflate(R.menu.home, menu);
        //Configure search
        menu.findItem(R.id.searchFake).setVisible(true);
        menu.findItem(R.id.search).setVisible(false);


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
        if (id == R.id.searchFake) {
            startActivity(new Intent(this, SearchResultActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
