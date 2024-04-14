package com.example.forge;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forge.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Set up FAB and Snackbar based on the current destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_home) {
                binding.appBarMain.fab.setImageResource(R.drawable.baseline_add);
                binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Add new athlete/coach", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                binding.appBarMain.fab.setVisibility(View.VISIBLE);
            } else {
                // If not on the home destination, hide the FAB
                binding.appBarMain.fab.setVisibility(View.GONE);
            }

            ImageButton threeDotsButton = findViewById(R.id.three_dots);
            ImageButton switchAccountsButton = findViewById(R.id.switch_accounts);

            if (destination.getId() == R.id.nav_profile) {
                threeDotsButton.setVisibility(View.GONE);
            } else {
                threeDotsButton.setVisibility(View.VISIBLE);
                threeDotsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_profile);
                    }
                });
            }

            switchAccountsButton.setVisibility(destination.getId() == R.id.nav_profile ? View.VISIBLE : View.GONE);
            switchAccountsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top-level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_schedule, R.id.nav_analysis,
                R.id.nav_tournaments, R.id.nav_diet, R.id.nav_progress)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}