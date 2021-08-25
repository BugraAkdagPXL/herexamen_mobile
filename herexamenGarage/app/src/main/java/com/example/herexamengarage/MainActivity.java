package com.example.herexamengarage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.herexamengarage.util.FirebaseUtil;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_garages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        insertMenu.setVisible(FirebaseUtil.isAdmin);
        GarageFragment detail = (GarageFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentDetailLandscape);
        // Check if detail is visible (landscape)
        if(detail != null && detail.isVisible() && FirebaseUtil.isAdmin){
            inflater.inflate(R.menu.save_menu, menu);
        }
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.insert_menu:
                intent = new Intent(this, GarageActivity.class);
                startActivity(intent);
                return true;
            case R.id.timer_menu:
                intent = new Intent(this, TimerActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {FirebaseUtil.attachListener();
                        });
                FirebaseUtil.detachListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}