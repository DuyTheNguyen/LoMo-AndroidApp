package com.example.davenguyen.lomoapp;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 * Copyright (C) 2014 relex
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class HomeFragment extends Fragment {

    private static ViewPager slideShow;

    //Movie List
    private ArrayList<Movie> popularMoviesList = new ArrayList<Movie>();

    private SlideShowAdapter slideShowAdapter;

    private DatabaseReference mDatabase;

    private FragmentActivity activity;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        //Set title
        ((HomeActivity) getActivity()).setActionBarTitle("Home Page");

        //find the current pager
        slideShow = (ViewPager) view.findViewById(R.id.slideShow);

        //Initialize the adpater
        slideShowAdapter = new SlideShowAdapter(activity, popularMoviesList);
        slideShow.setAdapter(slideShowAdapter);
        loadImage();


        //Set the indicator
        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        indicator.setViewPager(slideShow);
        //Since used dynamic adapter
        slideShowAdapter.registerDataSetObserver(indicator.getDataSetObserver());


        // Auto start of viewpager
        /*final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == popularMoviesList.size()) {
                    currentPage = 0;
                }
                slideShow.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 5000);*/
    }

    private void loadImage() {
        // Get a reference to our popularmovies
        mDatabase = FirebaseDatabase.getInstance().getReference().child("lpopular");
        // Attach a listener to read the data at our genres reference
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Get all the genres
                Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Movie movie = postSnapshot.getValue(Movie.class);

                    if (!(popularMoviesList.size() == dataSnapshot.getChildrenCount()))
                        popularMoviesList.add(movie);
                    //Log.i("CONTAIN",b.toString());


                    Log.e("Get Data", movie.getName());
                    slideShowAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("READ_FAIELD", "The read failed: " + databaseError.getCode());
            }
        });


    }

    //Clear the list when resume
    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Custom apdaper for Pager
     */
    public static class SlideShowAdapter extends PagerAdapter {


        private ArrayList<Movie> popularMovieList;
        private LayoutInflater inflater;
        private FragmentActivity context;

        public SlideShowAdapter(FragmentActivity context, ArrayList<Movie> popularMovieList) {
            this.context = context;
            this.popularMovieList = popularMovieList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return popularMovieList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            View myImageLayout = inflater.inflate(R.layout.slide, view, false);
            ImageView myImage = (ImageView) myImageLayout
                    .findViewById(R.id.slideShowImg);
            Picasso.with(context).load(popularMovieList.get(position).getImage()).into(myImage);
            view.addView(myImageLayout, 0);
            myImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MovieFragment movieFragment = MovieFragment
                            .newInstance(popularMovieList.get(position), popularMovieList.get(position).getGenre());
                    /***These code below does not work*/
                    context.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragArea, movieFragment)
                            .addToBackStack(null)
                            .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_UNSET)
                            .commit();

                }
            });
            return myImageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }


    }
}
