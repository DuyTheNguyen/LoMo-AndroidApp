package com.example.davenguyen.lomoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieListActivity extends BaseActivity {

    private DatabaseReference movieDatabase;
    private RecyclerView movieList;
    private FirebaseUser user;
    private TextView resultText;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_movie_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
    }

    private void initializeUI() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        resultText = findViewById(R.id.mlA_resultText);
        movieDatabase = FirebaseDatabase.getInstance().getReference().child("playlist");
        movieDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user.getUid())) {
                    resultText.setText("Your list is empty.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setUpRecycleView();


    }

    /**
     * Create view holder for Recycle View
     */
    public static class ListMovieViewHolder extends RecyclerView.ViewHolder {
        private View mView;

        private TextView nameTv;
        private TextView yearTv;
        private ImageView imageTv;

        public ListMovieViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            nameTv = mView.findViewById(R.id.rmlr_name);
            yearTv = mView.findViewById(R.id.rmlr_year);
            imageTv = mView.findViewById(R.id.rmlr_img);
        }

        public void setName(String name) {
            nameTv.setText(name);
        }

        public void setYear(String year) {
            yearTv.setText(year);
        }

        public void seImage(Context ct, String image) {
            Picasso.with(ct).load(image).into(imageTv);
        }
    }

    private void setUpRecycleView() {
        DatabaseReference movieDatabase1 = FirebaseDatabase.getInstance().getReference().child("playlist").child(user.getUid());
        Log.i("MOVIETEST", user.getUid());

        movieList = findViewById(R.id.mlA_movieList);
        movieList.setHasFixedSize(true);
        movieList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<Movie, ListMovieViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Movie, ListMovieViewHolder>(
                Movie.class,                    // class of item
                R.layout.result_movie_list_row,        // layout for displaying
                ListMovieViewHolder.class,      // View holder
                movieDatabase1                   //  database
        ) {
            @Override
            protected void populateViewHolder(ListMovieViewHolder viewHolder, final Movie model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setYear(model.getYear());
                viewHolder.seImage(getApplicationContext(), model.getImage());
                Log.i("TESTMOVIE", model.getName());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MovieListActivity.this, HomeActivity.class);

                        ArrayList<Movie> movieList = new ArrayList<Movie>();
                        movieList.add(model);
                        intent.putParcelableArrayListExtra("MOVIE", movieList);
                        startActivityForResult(intent, 1);
                    }
                });
            }
        };
        movieList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

}
