package com.example.davenguyen.lomoapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;

import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    private Button logoutBtn;
    private TextView emailTv;
    private TextView passwordTv;
    private TextView displayNameTv;
    private CircleImageView avatarTv;
    //Modify
    private ImageButton cEmail;
    private ImageButton cPassword;
    private ImageButton cName;

    private FirebaseUser firebaseUser;
    private DatabaseReference userDatabase;
    private DatabaseReference userImageDatabase;
    private StorageReference avatarStorage;

    private String userID="";
    private String userEmail="";
    private String userPassword="";
    private String userName="";
    private String userAvatar="";

    private SpotsDialog spotsDialog;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
    }

    public void initializeUI() {
        emailTv = findViewById(R.id.profilePg_email);
        passwordTv = findViewById(R.id.profilePg_password);
        displayNameTv = findViewById(R.id.profilePg_displayName);
        logoutBtn = findViewById(R.id.profile_logout);
        avatarTv = findViewById(R.id.profile_avatar);


        cEmail = findViewById(R.id.profile_changeEmail);
        cPassword = findViewById(R.id.profile_changePassword);
        cName = findViewById(R.id.profile_changeName);

        spotsDialog = new SpotsDialog(this, R.style.Loading);

        firebaseUser = mAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        //get ref to user data base
        userImageDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        userDatabase = lomoDatabase.getInstance().getReference()
                .child("users")
                .child(userID);

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userEmail = dataSnapshot.child("email").getValue().toString();
                userPassword = dataSnapshot.child("password").getValue().toString();
                userName = dataSnapshot.child("name").getValue().toString();
                userAvatar = dataSnapshot.child("avatar").getValue().toString();


                emailTv.setText(userEmail);
                passwordTv.setText(userPassword);
                displayNameTv.setText(userName);
                if (userAvatar.equals("default"))
                    avatarTv.setImageResource(R.drawable.ic_account_circle_blue_grey_400_48dp);
                else
                    Picasso.with(ProfileActivity.this).load(userAvatar).into(avatarTv);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        avatarTv.setOnClickListener(this);

        logoutBtn.setOnClickListener(this);
        cEmail.setOnClickListener(this);
        cPassword.setOnClickListener(this);
        cName.setOnClickListener(this);

        avatarTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeAvatar();
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        String changing = "";
        if (view == cEmail) {
            changing = "email";
        }
        if (view == cPassword) {
            changing = "password";
        }
        if (view == cName) {
            changing = "name";
        }
        if (view == logoutBtn) {
            mAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        if (!changing.equals("")) {
            EditFragment editFragment = EditFragment.newInstance(userEmail, userPassword, changing);
            /*getSupportFragmentManager().beginTransaction()
                    .add(R.id.profile_frag_area,editFragment)
                    .addToBackStack(null)
                    .commit();*/
            FragmentManager fm = getSupportFragmentManager();

            editFragment.show(fm, "fragment_edit");
        }

        if (view == avatarTv) {
            Utility.MakeLongToastToast(this, "Hold the avatar to change");
        }

    }

    //Using API to crop Image
    private void changeAvatar() {

        /*Intent avatarIntent = new Intent();
        avatarIntent.setType("image/*");
        avatarIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(avatarIntent,"Choose your avatar"),1);*/

        CropImage.activity().setAspectRatio(1, 1).setGuidelines(CropImageView.Guidelines.ON).start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                spotsDialog.show();
                avatarStorage = FirebaseStorage.getInstance().getReference().child("avatars").child(userID + ".jpg");

                avatarStorage.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        String result = "Changed successfully";
                        if (task.isSuccessful()) {
                            @SuppressWarnings("VisibleForTests") String download_url = task.getResult().getDownloadUrl().toString();
                            userImageDatabase.child("avatar").setValue(download_url);
                        } else {
                            result = "Changed failed";
                        }
                        Utility.MakeLongToastToast(getApplicationContext(), result);
                        spotsDialog.dismiss();
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
