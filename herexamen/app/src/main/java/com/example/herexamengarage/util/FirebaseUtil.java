package com.example.herexamengarage.util;

import android.app.Activity;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.herexamengarage.Garage;
import com.example.herexamengarage.MainActivity;
import com.firebase.ui.auth.AuthUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    /* Reference and database */
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mFirebaseReference;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;

    private static MainActivity caller;

    /* Object of the class */
    private static FirebaseUtil firebaseUtil;

    /* List of garages */
    public static ArrayList<Garage> mGarages;

    /* Private constructor */
    private FirebaseUtil(){}

    public static boolean isAdmin;

    /* Open a reference to an object in the database */
    public static void openFbReference(String ref, final MainActivity callerActivity){
        caller = callerActivity;
        openReference(ref);
    }

    private static void openReference(String ref){
        firebaseUtil = new FirebaseUtil();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser() == null){
                FirebaseUtil.signIn();
                Toast.makeText(caller.getBaseContext(), "Welcome back", Toast.LENGTH_LONG).show();
            }
            else {
                String userId = firebaseAuth.getUid();
                checkAdmin(userId);
            }

        };
        connectStorage();
        mGarages = new ArrayList<>();
        mFirebaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static void openFbReference(String ref){
        openReference(ref);
    }

    private static void checkAdmin(String userId) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("Administrators").child(userId);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addChildEventListener(listener);
    }

    private static void signIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        caller.startActivity(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build());
    }

    public static void attachListener(){
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance("gs://herexamengarages.appspot.com/");
        mStorageRef = mStorage.getReference().child("garages_pictures");
    }
}
