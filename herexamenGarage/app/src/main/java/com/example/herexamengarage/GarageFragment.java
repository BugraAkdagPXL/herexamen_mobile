package com.example.herexamengarage;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.herexamengarage.util.FirebaseUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class GarageFragment extends Fragment {
    /* Variables */
    EditText txtName;
    EditText txtAddress;
    EditText txtCapacity;
    EditText txtCurrentCount;
    Button btnRandomNumber;
    Button btnImage;
    private DatabaseReference mDatabaseReference;
    Garage garage;
    ImageView imageView;

    /* On creation of the view */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garage, container, false);
        setHasOptionsMenu(true);

        /* Get bundles (from master) */
        Bundle bundle = getArguments();

        imageView = view.findViewById(R.id.garageImage);

        /* Get the text fields */
        txtName = view.findViewById(R.id.txtName);
        txtAddress = view.findViewById(R.id.txtAddress);
        txtCapacity = view.findViewById(R.id.txtCapacity);
        txtCurrentCount = view.findViewById(R.id.txtCurrentCount);

        /* Button event listener */
        btnRandomNumber = view.findViewById(R.id.btnGenerateRandom);
        btnImage = view.findViewById(R.id.btnImage);

        btnRandomNumber.setOnClickListener(v -> {
            RequestQueue queue = Volley.newRequestQueue(v.getContext());
            String url = "https://csrng.net/csrng/csrng.php?min=1&max=10000";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null, response -> {
                        try {
                            txtCapacity.setText(response.getJSONObject(0).get("random").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.e("JSON" ,error.toString()));
            queue.add(jsonArrayRequest);
        });

        btnImage.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent.createChooser(intent, "Insert picture"), 111);

        });

        /* Use util class */
        FirebaseUtil.openFbReference("Garages");

        mDatabaseReference = FirebaseUtil.mFirebaseReference;

        /* Get the intent and see if there is a garage (detail view) */
        Intent intent = getActivity().getIntent();
        Garage garage = (Garage) intent.getSerializableExtra("Garage");

        /* If we are creating a new garage create a new object */
        if(garage == null){
            garage = new Garage();
        }

        /* Check if bundle is empty, if not empty save the properties */
        if(bundle != null && getArguments() != null){
            garage.setName(getArguments().getString("Name"));
            garage.setAddress(getArguments().getString("Address"));
            garage.setCapacity(getArguments().getInt("Capacity"));
            garage.setCurrentCount(getArguments().getInt("Current"));
            garage.setId(getArguments().getString("Id"));
            garage.setImageUrl(getArguments().getString("ImageUrl"));

        }
        this.garage = garage;

        /* Set the values */
        txtAddress.setText(garage.getAddress());
        txtName.setText(garage.getName());
        txtCapacity.setText(Integer.toString(garage.getCapacity()));
        txtCurrentCount.setText(Integer.toString(garage.getCurrentCount()));
        showImage(garage.getImageUrl());

        enableEditTexts(FirebaseUtil.isAdmin);
        return view;
    }


    public class RandomNumberQuery extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(getContext());
            final String[] randomNumberAsString = new String[1];
            String url = "https://csrng.net/csrng/csrng.php?min=1&max=10000";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null, response -> {
                        try {
                            randomNumberAsString[0] = response.getJSONObject(0).get("random").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.e("JSON" ,error.toString()));
            queue.add(jsonArrayRequest);
            return randomNumberAsString[0];
        }

        @Override
        protected void onPostExecute(String s) {
            txtCapacity.setText(s);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /* Check what menu item was clicked */
        switch (item.getItemId()){
            case R.id.save_menu:
                // saveGarage() returns true if it succeeded
                if (saveGarage()){
                    Toast.makeText(getActivity(), "Garage saved", Toast.LENGTH_LONG).show();
                    clean();
                    backToList();
                }
                return true;
            case R.id.delete_menu:
                if(deleteGarage()){
                    Toast.makeText(getActivity(), "Garage removed", Toast.LENGTH_LONG).show();
                    backToList();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Clean the fields */
    private void clean() {
        txtCapacity.setText("");
        txtName.setText("");
        txtAddress.setText("");
        txtName.requestFocus();
    }

    /* Save the garage, returns true if it was valid */
    private boolean saveGarage() {
        /* Set the values to the garage object */
        garage.setName(txtName.getText().toString());
        garage.setAddress(txtAddress.getText().toString());
        garage.setCapacity(Integer.parseInt(txtCapacity.getText().toString()));
        garage.setCurrentCount(Integer.parseInt(txtCurrentCount.getText().toString()));

        if(garage.getName().isEmpty()){
            Toast.makeText(getActivity(), "Please fill in a name.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(garage.getAddress().isEmpty()){
            Toast.makeText(getActivity(), "Please fill in an address.", Toast.LENGTH_LONG).show();
            return false;
        }

        /* Check if the current count was larger than the capacity */
        if (garage.getCurrentCount() > garage.getCapacity()){
            Toast.makeText(getActivity(), "The capacity cannot be lower than the current count.", Toast.LENGTH_LONG).show();
            return false;
        }

        /* Is this a brand new garage? If so use push*/
        if(garage.getId() == null){
            mDatabaseReference.push().setValue(garage);
        }
        /* Existing garage -> use child */
        else{
            mDatabaseReference.child(garage.getId()).setValue(garage);
        }
        return true;
    }

    /* Delete garage if it exists */
    private boolean deleteGarage(){
        if(garage.getId() == null){
            Toast.makeText(getActivity(), "Please save the deal before deleting", Toast.LENGTH_LONG).show();
            return false;
        }
        mDatabaseReference.child(garage.getId()).removeValue();
        return true;
    }

    /* Return back to the main view */
    private void backToList(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    /* disable or enable text fields and buttons */
    private void enableEditTexts(boolean isEnabled){
        txtName.setEnabled(isEnabled);
        txtCurrentCount.setEnabled(isEnabled);
        txtCapacity.setEnabled(isEnabled);
        btnRandomNumber.setEnabled(isEnabled);
        btnImage.setEnabled(isEnabled);
        txtAddress.setEnabled(isEnabled);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        StorageReference ref = FirebaseUtil.mStorageRef.child(uri.getLastPathSegment());
        /* on listener */
        ref.putFile(uri).addOnSuccessListener(getActivity(), taskSnapshot -> {
            // multi threading
            Task<Uri> imageTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
            imageTask.addOnSuccessListener(uri1 -> {
                garage.setImageUrl(uri1.toString());
                showImage(uri1.toString());
            });
        });
    }

    private void showImage(String url){
        if(url != null && !url.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(getActivity()).load(url).resize(width, width*2/3).centerCrop().into(imageView);

        }
    }
}
