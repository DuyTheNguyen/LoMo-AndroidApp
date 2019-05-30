package com.example.davenguyen.lomoapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
   

    private EditText emailEt;
    private EditText passwordEt;
    private EditText nameEt;
    private Button reggisBtn;
    private TextView loginTv;

    private SpotsDialog spotsDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeUI();
    }

    private void initializeUI() {
        //Initialize fire base auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //Check the user log in or not. If the user has already login go to log in activity
        if (firebaseAuth.getCurrentUser() != null) {
            Intent nextAct = new Intent(SignUpActivity.this, HomeActivity.class);
            //Prevent go back
            nextAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            nextAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(nextAct);
            finish();//finish the current activity
        }

        emailEt = findViewById(R.id.signup_email);
        passwordEt = findViewById(R.id.signup_password);
        nameEt = findViewById(R.id.signup_name);
        reggisBtn = findViewById(R.id.signup_btn);
        loginTv = findViewById(R.id.signup_login);

        reggisBtn.setOnClickListener(this);
        loginTv.setOnClickListener(this);

        spotsDialog = new SpotsDialog(this, R.style.Custom);

    }

    @Override
    public void onClick(View view) {
        if (view == reggisBtn) {
            signupUser();
        }

        if (view == loginTv) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void signupUser() {
        final String email = emailEt.getText().toString().trim();
        final String password = passwordEt.getText().toString().trim();
        final String displayname = nameEt.getText().toString().trim();

        //Check input fields is empty or not
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(displayname)) {
            Utility.MakeLongToastToast(this, "Please enter fields");
        } else {
            //Create User
            spotsDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Get the current user which just created
                                firebaseUser = firebaseAuth.getCurrentUser();

                                //Update display name
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayname)
                                        .build();
                                firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            Log.i("TAG", "User profile updated.");
                                    }
                                });

                                //Get ID of the current user
                                String userID = firebaseUser.getUid();
                                //Create reference to the user ID
                                DatabaseReference mRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("users")
                                        .child(userID);
                                HashMap<String, String> userMap = new HashMap<String, String>();
                                userMap.put("email", email);
                                userMap.put("password", password);
                                userMap.put("name", displayname);
                                userMap.put("avatar", "default");

                                //set value into database and check whether it finishes
                                mRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                            Utility.MakeLongToastToast(SignUpActivity.this, "Sign up successfully");
                                            Intent nextAct = new Intent(SignUpActivity.this, HomeActivity.class);
                                            //Prevent go back
                                            nextAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            nextAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(nextAct);
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                String error = "Unknown Error";

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    error = "Password must have at least 7 characters";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    error = "Invalid Emial";
                                } catch (FirebaseAuthUserCollisionException e) {
                                    error = "Existing Account";
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                spotsDialog.dismiss();
                                Utility.MakeLongToastToast(SignUpActivity.this, error);
                            }

                        }
                    });
        }
    }
}
