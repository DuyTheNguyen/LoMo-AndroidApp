package com.example.davenguyen.lomoapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * to handle interaction events.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends DialogFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_EMAILFIELD = "emailField";
    private static final String ARG_PASSWORDFIELD = "passwordField";
    private static final String ARG_NAMEFIELD = "nameField";

    // TODO: Rename and change types of parameters
    private Activity activity;

    private String mEmailField;
    private String mPassField;
    private String mNameField;

    private String editTextValue;
    //UI
    private TextView nameFieldTv;
    private EditText changeFieldEt;
    private Button saveBtn;
    private Button cancelBtn;
    //Database
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private AuthCredential credential; //for change email or pass
    //Dialog
    private SpotsDialog spotsDialog;

    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nameField Parameter 1.
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String emailField, String passwordField, String nameField) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAILFIELD, emailField);
        args.putString(ARG_PASSWORDFIELD, passwordField);
        args.putString(ARG_NAMEFIELD, nameField);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmailField = getArguments().getString(ARG_EMAILFIELD);
            mPassField = getArguments().getString(ARG_PASSWORDFIELD);
            mNameField = getArguments().getString(ARG_NAMEFIELD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        nameFieldTv = view.findViewById(R.id.editFrag_tv);
        changeFieldEt = view.findViewById(R.id.editFrag_et);
        saveBtn = view.findViewById(R.id.edit_save);
        cancelBtn = view.findViewById(R.id.edit_cancel);

        nameFieldTv.setText("Type your new " + mNameField + ": ");
        activity = getActivity();

        spotsDialog = new SpotsDialog(activity, R.style.Loading);

        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        spotsDialog.dismiss();
        getDialog().dismiss();
    }


    @Override
    public void onClick(View view) {
        if (view == saveBtn) {
            //Show dialog for waiting
            spotsDialog.show();
            saveData();
        }
        if (view == cancelBtn) {
            onButtonPressed();
        }
    }

    private void saveData() {


        //Get reference and user ID
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        String uID = firebaseUser.getUid();

        //Get auth credentials from the user for re-authentication
        credential = EmailAuthProvider.getCredential(mEmailField, mPassField);


        final DatabaseReference userDatabase = firebaseDatabase.getInstance().getReference().child("users").child(uID);
        //Get the value of edit text
        editTextValue = changeFieldEt.getText().toString().trim();
        //Check whether edit text is empty
        if (!TextUtils.isEmpty(editTextValue)) {
            //if change name
            if (mNameField.equals("name")) {
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(editTextValue).build();
                firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("PROFILE", "User profile updated.");
                            updateDatabase(userDatabase);
                            spotsDialog.dismiss();
                        }
                    }
                });
            }//if change password
            else if (mNameField.equals("password")) {

                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.updatePassword(editTextValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.i("PASSWORD", "User password updated.");
                                                updateDatabase(userDatabase);
                                            } else {
                                                Log.i("PASSWORD", "User cannot updated.");
                                                Utility.MakeLongToastToast(activity, "Password must have more than 7 characters");
                                                spotsDialog.dismiss();
                                            }
                                        }
                                    });
                                } else {
                                    Log.i("AUTH", "Error auth failed");
                                }
                            }
                        });
            }//if change email
            else if (mNameField.equals("email")) {
                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.updateEmail(editTextValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.i("EMAIL", "User email address updated.");
                                                updateDatabase(userDatabase);
                                            } else {
                                                Utility.MakeLongToastToast(activity, "Cannot Updated. Please try another name ");
                                                spotsDialog.dismiss();
                                            }
                                        }
                                    });
                                } else {
                                    Log.i("AUTH", "Error auth failed");
                                }
                            }
                        });

            }


        } else {
            Utility.MakeLongToastToast(getActivity(), "Please enter your change.");
            spotsDialog.dismiss();
        }
    }

    private void updateDatabase(DatabaseReference userDatabase) {
        //Update in the database
        userDatabase.child(mNameField).setValue(editTextValue).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("DATABASE-PROFILE", "Updated successfully");
                    Utility.MakeLongToastToast(activity, "Updated  successfully");
                    onButtonPressed();
                    spotsDialog.dismiss();

                }

            }
        });

    }

}
