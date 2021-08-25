package com.example.herexamengarage;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.RecyclerView;

import com.example.herexamengarage.util.FirebaseUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class GarageAdapter extends RecyclerView.Adapter<GarageAdapter.GarageViewHolder> {
   /* Fragment manager */
    FragmentManager manager;

    /* List of garages */
    ArrayList<Garage> garages;

    private ImageView garageImage;

    /* Constructor */
    public GarageAdapter(FragmentManager manager, MainActivity caller){
        this.manager = manager;
        /* Get the garages from the Util class */
        FirebaseUtil.openFbReference("Garages", caller);
        /* Reference and event listener */
        DatabaseReference mDatabaseReference = FirebaseUtil.mFirebaseReference;
        garages = FirebaseUtil.mGarages;

        /* Create an event listener */
        ChildEventListener mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Garage garage = snapshot.getValue(Garage.class);
                if (garage != null) {
                    garage.setId(snapshot.getKey());
                }
                garages.add(garage);
                notifyItemInserted(garages.size() - 1);
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
        mDatabaseReference.addChildEventListener(mChildListener);
    }

    /* Create view holder */
    @NonNull
    @Override
    public GarageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);
        return new GarageViewHolder(itemView);
    }

    /* Bind garage to position in view holder */
    @Override
    public void onBindViewHolder(@NonNull GarageViewHolder holder, int position) {
        Garage garage = garages.get(position);
        holder.bind(garage);
    }

    /* Get amount of garages */
    @Override
    public int getItemCount() {
        return garages.size();
    }

    /* Garage view holder class */
    public class GarageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        /* Variables */
        TextView gName;
        TextView gAddress;
        TextView gSpots;

        /* Constructor */
        public GarageViewHolder(@NonNull View itemView) {
            super(itemView);
            gName = itemView.findViewById(R.id.gName);
            gAddress = itemView.findViewById(R.id.gAddress);
            gSpots = itemView.findViewById(R.id.gFreeSpots);
            garageImage = itemView.findViewById(R.id.gImage);
            itemView.setOnClickListener(this);

        }

        /* Bind the values to the labels */
        public void bind(Garage garage){
            gName.setText(garage.getName());
            gAddress.setText(garage.getAddress());

            if(garage.isFull()){
                gSpots.setText(R.string.full_message);
                gSpots.setTextColor(Color.RED);
            }
            else{
                String message = garage.getFreeSpots() + " FREE SPOTS";
                gSpots.setText(message);
                gSpots.setTextColor(Color.GREEN);
            }
            showImage(garage.getImageUrl());

        }

        /* On click event */
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            GarageFragment detail = (GarageFragment) manager.findFragmentById(R.id.fragmentDetailLandscape);
            Garage selectedGarage = garages.get(clickedPosition);

            /* Landscape */
            if(detail != null && detail.isVisible()){
                GarageFragment newDetail = new GarageFragment();

                /* Create a bundle */
                Bundle bundle = createBundle(selectedGarage);
                newDetail.setArguments(bundle);

                /* Send to detail */
                FragmentTransaction transaction = detail.getFragmentManager().beginTransaction();
                transaction.replace(detail.getId(), newDetail);
                transaction.addToBackStack(null);
                transaction.commit();


            }
             /* Portrait */
            else{
                /* Open new view */
                Intent intent = new Intent(view.getContext(), GarageActivity.class);
                intent.putExtra("Garage", selectedGarage);
                view.getContext().startActivity(intent);
            }

        }

        private Bundle createBundle(Garage garage){
            Bundle bundle = new Bundle();
            bundle.putString("Name", garage.getName());
            bundle.putString("Address", garage.getAddress());
            bundle.putInt("Capacity", garage.getCapacity());
            bundle.putInt("Current", garage.getCurrentCount());
            bundle.putString("Id", garage.getId());
            bundle.putString("ImageUrl", garage.getImageUrl());
            return bundle;
        }

        private void showImage(String url) {
            if (url != null && !url.isEmpty()) {
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                Picasso.with(garageImage.getContext()).load(url).resize(300, 300).centerCrop().into(garageImage);

            }
        }
    }

}
