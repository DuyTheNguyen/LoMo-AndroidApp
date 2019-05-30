package com.example.davenguyen.lomoapp;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;

import android.text.TextUtils;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import dmax.dialog.SpotsDialog;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEt;
    private EditText passwordEt;
    private Button logginBtn;
    private TextView signupBtn;


    private SpotsDialog alertDialog;

    //Create firebase Auth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        initailizeUI();
    }

    public void initailizeUI() {
        //Initialise the object
        firebaseAuth = FirebaseAuth.getInstance();

        //Check the user log in or not. If the user has already login go to profile activity
        if (firebaseAuth.getCurrentUser() != null) {
            Intent nextAct = new Intent(LoginActivity.this, HomeActivity.class);
            //Prevent go back
            nextAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            nextAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(nextAct);
            finish();//finish the current activity
        }

        emailEt = (EditText) findViewById(R.id.login_email);
        passwordEt = (EditText) findViewById(R.id.login_password);
        logginBtn = (Button) findViewById(R.id.login_btn);
        signupBtn = (TextView) findViewById(R.id.login_signup);


        alertDialog = new SpotsDialog(this, R.style.Custom);

        logginBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
    }

    private void userLogin() {
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        //Check email and password are empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Utility.MakeLongToastToast(this, "Please enter email and password");
            return;
        }

        //Diplay alert dialog if the email and password are not empty

        alertDialog.show();

        //this method will be called when the login in finished
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();
                            Utility.MakeLongToastToast(LoginActivity.this, "Sign in successfully");
                            Intent nextAct = new Intent(LoginActivity.this, HomeActivity.class);
                            //Prevent go back
                            nextAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            nextAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(nextAct);
                            finish();//finish the current activity
                        } else {
                            alertDialog.dismiss();
                            Utility.MakeLongToastToast(LoginActivity.this, "Could not sign in. Please try again");
                        }

                    }
                });
    }

    /**
     * Handle loggin and sign up click
     */
    @Override
    public void onClick(View view) {
        if (view == logginBtn) {
            userLogin();
        }

        //Redirect to Sign Up Page
        if (view == signupBtn) {
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }
}

