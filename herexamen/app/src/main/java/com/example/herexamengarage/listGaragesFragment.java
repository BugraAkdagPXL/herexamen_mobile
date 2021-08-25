package com.example.herexamengarage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.herexamengarage.util.FirebaseUtil;


public class listGaragesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garages, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        /* Get the garages and put them in a recycle view */
        RecyclerView rvGarages = getView().findViewById(R.id.rvGarages);

        final GarageAdapter adapter = new GarageAdapter(getParentFragmentManager(), (MainActivity) getActivity());
        rvGarages.setAdapter(adapter);
        LinearLayoutManager garageLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvGarages.setLayoutManager(garageLayoutManager);
        FirebaseUtil.attachListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

}
