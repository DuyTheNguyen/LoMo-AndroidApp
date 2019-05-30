package com.example.davenguyen.lomoapp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenreFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GENRE = "genre";


    // TODO: Rename and change types of parameters
    private String mGenre;

    private RecyclerView movieList;

    private DatabaseReference mDatabase;

    private SpotsDialog spotsDialog;

    public GenreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param genre Parameter 1.
     * @return A new instance of fragment Genre.
     */
    // TODO: Rename and change types and number of parameters
    public static GenreFragment newInstance(String genre) {
        GenreFragment fragment = new GenreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GENRE, genre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Movie, MovieViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Movie, MovieViewHolder>(
                Movie.class,             // class of item
                R.layout.movie_list_row, // layout for displaying
                MovieViewHolder.class,   // View holder
                mDatabase               //  database

        ) {
            @Override
            protected void populateViewHolder(MovieViewHolder viewHolder, final Movie model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                //Handle click on the view
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utility.MakeLongToastToast(getActivity(), "You just choose " + model.getName());
                        MovieFragment movieFragment = MovieFragment.newInstance(model, mGenre);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragArea, movieFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
        };

        movieList.setAdapter(firebaseRecyclerAdapter);

    }

    /**
     * Create view holder for recycle view
     */
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        private View mView;

        private TextView mNameTV;
        private ImageView mImageIM;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mNameTV = (TextView) mView.findViewById(R.id.mlr_name);
            mImageIM = mView.findViewById(R.id.mlr_img);
        }

        //Set Name
        public void setName(String name) {
            mNameTV.setText(name);
        }

        public void setImage(Context ct, String image) {
            //get the image from sever
            // Picasso.with(ct).load(image).into(mImageIM);
            Picasso.with(ct).load(image).resize(300, 420).centerCrop().into(mImageIM);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGenre = getArguments().getString(ARG_GENRE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_genre, container, false);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get Database from firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("movies").child(mGenre);
        //Set title
        ((HomeActivity) getActivity()).setActionBarTitle(mGenre);
        float imageWidth = (int) getResources().getDimension(R.dimen.imageWidth) / getResources().getDisplayMetrics().density;
        //Set layout for the recycle view
        movieList = view.findViewById(R.id.fg_movie_list);
        movieList.setHasFixedSize(true);
        movieList.setNestedScrollingEnabled(false);
        int mNoOfColumns = Utility.calculateNoOfColumns(getActivity().getApplicationContext(),(int)imageWidth);
        movieList.setLayoutManager(new GridLayoutManager(this.getActivity(), mNoOfColumns));


    }


}
