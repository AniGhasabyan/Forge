package com.example.forge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forge.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private boolean isChosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();
        String userDisplayName = currentUser.getDisplayName();

        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "");
        String addNewAthleteOrCoach = "";

        if(userRole.equals("Athlete")){
            addNewAthleteOrCoach = "add new coach";
        } else if(userRole.equals("Coach")){
            addNewAthleteOrCoach = "add new athlete";
        }

        View headerView = binding.navView.getHeaderView(0);
        TextView textViewEmail = headerView.findViewById(R.id.textView);
        TextView textViewDisplayName = headerView.findViewById(R.id.textWho_un);
        TextView textWho = headerView.findViewById(R.id.textWho_ac);
        ImageView navHeaderImageView = headerView.findViewById(R.id.imageView);

        textViewEmail.setText(userEmail);
        textViewDisplayName.setText(userDisplayName);
        textWho.setText(userRole);

        loadProfilePicture(navHeaderImageView);

        String finalAddNewAthleteOrCoach = addNewAthleteOrCoach;
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_home) {
                binding.appBarMain.fab.setImageResource(R.drawable.baseline_add);
                binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, finalAddNewAthleteOrCoach, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        navController.navigate(R.id.nav_search);
                    }
                });
                binding.appBarMain.fab.setVisibility(View.VISIBLE);
            } else {
                binding.appBarMain.fab.setVisibility(View.GONE);
            }

            ImageButton threeDotsButton = findViewById(R.id.three_dots);
            ImageButton switchAccountsButton = findViewById(R.id.switch_accounts);

            if (destination.getId() == R.id.nav_profile) {
                threeDotsButton.setVisibility(View.GONE);
            } else {
                threeDotsButton.setVisibility(View.VISIBLE);
                threeDotsButton.setOnClickListener(view -> {
                    if (destination.getId() == R.id.nav_chosen_analysis || destination.getId() == R.id.nav_chosen_diet
                            || destination.getId() == R.id.nav_chosen_progress || destination.getId() == R.id.nav_chosen_schedule
                            || destination.getId() == R.id.nav_chosen_tournaments || destination.getId() == R.id.nav_choose) {
                        isChosen = true;
                    } else {
                        isChosen = false;
                    }
                    navController.navigate(R.id.nav_profile);
                });
            }

            switchAccountsButton.setVisibility(destination.getId() == R.id.nav_profile && !isChosen ? View.VISIBLE : View.GONE);
            switchAccountsButton.setOnClickListener(view -> {
                SharedPreferences preferences1 = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String currentRole = preferences1.getString("UserRole", "");

                String newRole = currentRole.equals("Athlete") ? "Coach" : "Athlete";
                preferences1.edit().putString("UserRole", newRole).apply();

                textWho.setText(newRole);

                navController.popBackStack(R.id.nav_profile, true);
                navController.navigate(R.id.nav_profile);
            });
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_schedule, R.id.nav_analysis,
                R.id.nav_tournaments, R.id.nav_diet, R.id.nav_progress, R.id.nav_porch)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    private void loadProfilePicture(ImageView imageView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Glide.with(this).load(imageUrl)
                    .fitCenter()
                    .circleCrop()
                    .into(imageView);
        }).addOnFailureListener(e -> {
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}