package com.example.davenguyen.lomoapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MOVIELIST = "param1";
    private static final String ARG_GENRE = "param2";

    // TODO: Rename and change types of parameters
    //Get values from genre fragment
    private Movie movie;
    private String mGenre;
    private String forShare;

    //Firebase
    private DatabaseReference mDataBase;


    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userID;
    private String userAvatar;

    private String movieID;
    private String comment;
    //UI
    //Movie Details
    private TextView nameTV;
    private TextView yearTV;
    private TextView directorTV;
    private TextView ratingTV;
    private TextView desTV;
    private ImageView imageTV;

    private ImageView toolbarImage;

    private Boolean mProcessAdd = false;


    //Bottom Bar
    private EditText commentEt;
    private ImageButton sendBtn;
    private ImageButton shareBtn;
    private ImageButton playBtn;
    private ImageButton playlistBtn;

    //For comment field
    private RecyclerView recyclerView;
    private final List<Comment> listOfComment = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private CommentAdapter commentAdapter;

    public MovieFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MovieFragment newInstance(Movie movie, String mGenre) {
        MovieFragment fragment = new MovieFragment();
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        movieList.add(movie);
        Bundle args = new Bundle();
        args.putString(ARG_GENRE, mGenre);
        args.putParcelableArrayList(ARG_MOVIELIST, movieList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Get the array list of the movie
            ArrayList<Movie> moviesList = getArguments().getParcelableArrayList(ARG_MOVIELIST);
            movie = moviesList.get(0);
            mGenre = getArguments().getString(ARG_GENRE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Firebase
        mDataBase = FirebaseDatabase.getInstance().getReference();
        //Get user ID
        userID = user.getUid();
        movieID = movie.getID();


        //--------------------Set Movie Details------------------------//
        nameTV = (TextView) view.findViewById(R.id.fm_name);
        yearTV = (TextView) view.findViewById(R.id.fm_year);
        directorTV = (TextView) view.findViewById(R.id.fm_director);
        ratingTV = (TextView) view.findViewById(R.id.fm_rating);
        desTV = (TextView) view.findViewById(R.id.fm_des);
        imageTV = (ImageView) view.findViewById(R.id.main_backdrop);


        shareBtn = view.findViewById(R.id.fm_shareBtn);
        playBtn = view.findViewById(R.id.fm_playBtn);
        playlistBtn = view.findViewById(R.id.fm_addListBtn);

        shareBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        playlistBtn.setOnClickListener(this);

        //TODO: Try with progress bar
        RatingBar ratingBar = view.findViewById(R.id.fm_ratingBar);
        ratingBar.setRating(Float.valueOf(movie.getRating()));


        Picasso.with(getActivity()).load(movie.getImage()).transform(new GradientTransformation()).into(imageTV);
        nameTV.setText(movie.getName());
        yearTV.setText(movie.getYear());
        directorTV.setText(movie.getDirector());
        ratingTV.setText(movie.getRating());
        desTV.setText(movie.getDescription());


        //----------------------Set Bottom bar-------------------------//
        commentEt = view.findViewById(R.id.fm_commenttField);
        sendBtn = view.findViewById(R.id.fm_sendBtn);
        sendBtn.setOnClickListener(this);

        //----------------------Set Comment Field-------------------------//
        recyclerView = view.findViewById(R.id.fm_commentList);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        commentAdapter = new CommentAdapter(listOfComment, this.getActivity());


        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        //Set layout for the recycle view
        recyclerView.setHasFixedSize(true);


        recyclerView.setAdapter(commentAdapter);
        ((HomeActivity) getActivity()).setActionBarTitle(movie.getName());
        setAddButton();
        //Load old comment
        loadComment();


    }

    //Handling clicking
    @Override
    public void onClick(View view) {
        //user hits send comment btn
        if (view == sendBtn) {
            sendComment();
        }
        if (view == shareBtn) {
            shareMovieDetails();
        }
        if (view == playBtn) {
            playTrailer();
        }
        if (view == playlistBtn) {
            addOrRemoveMoive();
        }
    }

    private void addOrRemoveMoive() {
        final DatabaseReference databaseMovieList = FirebaseDatabase.getInstance().getReference().child("playlist");
        mProcessAdd = true;

        databaseMovieList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessAdd) {
                    if (dataSnapshot.child(movieID).hasChild(userID)) {
                        //Remove user in the movie branch
                        databaseMovieList.child(movieID).child(userID).removeValue();
                        //Remove movie in user branch
                        databaseMovieList.child(userID).child(movieID).removeValue();

                        Utility.MakeLongToastToast(getActivity(), movie.getName() + " has been removed");

                        playlistBtn.setImageResource(R.drawable.ic_playlist_add_check_white_48dp);
                    }
                    //If users have not added the movie in their list then adding to their list
                    else {
                        //Add into movie database
                        databaseMovieList.child(movieID).child(userID).setValue("yay");
                        //Add into user database
                        HashMap<String, String> movieDetails = new HashMap<String, String>();
                        movieDetails.put("description", movie.getDescription());
                        movieDetails.put("director", movie.getDirector());
                        movieDetails.put("genre", movie.getGenre());
                        movieDetails.put("id", movie.getID());
                        movieDetails.put("image", movie.getImage());
                        movieDetails.put("name", movie.getName());
                        movieDetails.put("rating", movie.getRating());
                        movieDetails.put("year", movie.getYear());

                        databaseMovieList.child(userID).child(movieID).setValue(movieDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Utility.MakeLongToastToast(getActivity(), movie.getName() + " has been added");
                                }
                            }
                        });
                        playlistBtn.setImageResource(R.drawable.ic_playlist_add_check_green_700_48dp);
                    }
                    mProcessAdd = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void playTrailer() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com")));
    }

    //This method is for sharing
    private void shareMovieDetails() {
        forShare = "Movie name: " + movie.getName() + "\n" +
                "Year:       " + movie.getYear() + "\n" +
                "Rating:     " + movie.getRating() + "\n"
        ;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/pain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forShare);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    //Send Comment
    private void sendComment() {
        comment = commentEt.getText().toString();
        if (!TextUtils.isEmpty(comment)) {
            //store the data in the database: root -> comment --> userID -> movieID
            // This movie can have comment from users

            //Get the avatar of the user.
            DatabaseReference userAvatarRef;
            userAvatarRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
            userAvatarRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userAvatar = dataSnapshot.child("avatar").getValue(String.class);
                    String movieRef = "comment/" + movieID;


                    //Get the key of the reference
                    DatabaseReference userCommentPush = mDataBase.child("comment").child(movieID).push();

                    String pushId = userCommentPush.getKey();


                    //Create map for uploading to database
                    Map commentMap = new HashMap();
                    commentMap.put("name", user.getDisplayName());
                    commentMap.put("comment", comment);
                    commentMap.put("time", ServerValue.TIMESTAMP);
                    commentMap.put("avatar", userAvatar);

                    Map commentUserAndMovieMap = new HashMap();
                    commentUserAndMovieMap.put(movieRef + "/" + pushId, commentMap);
                    //Update to the database
                    mDataBase.updateChildren(commentUserAndMovieMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.i("CHECKERROR", databaseError.getMessage().toString());
                                Utility.MakeLongToastToast(getActivity(), "Sent failed");
                            } else {
                                Utility.MakeLongToastToast(getActivity(), "Comment sent");
                                //Set comment space blank after sending the comment.
                                commentEt.setText("");
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    //Load all the Comment of specific movie
    private void loadComment() {
        //work with child's details

        //retrieve only the data on each new Comment
        mDataBase.child("comment").child(movieID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                listOfComment.add(comment);
                //Update adapter if something changed
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Set the image of the add button
    private void setAddButton() {
        DatabaseReference databaseAdd1 = FirebaseDatabase.getInstance().getReference().child("playlist");
        databaseAdd1.keepSynced(true);
        databaseAdd1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(movieID).hasChild(userID)) {
                    playlistBtn.setImageResource(R.drawable.ic_playlist_add_check_green_700_48dp);
                } else {
                    playlistBtn.setImageResource(R.drawable.ic_playlist_add_white_48dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
