package com.nava.samiyuri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nava.samiyuri.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Plant> plantBuddies;
    private int currentPlantIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupPlantBuddies();
        updateUI();
        setupClickListeners();
    }

    private void setupPlantBuddies() {
        plantBuddies = new ArrayList<>();
        Intent intent = getIntent();
        String radishName = intent.getStringExtra("RADISH_NAME");
        String lettuceName = intent.getStringExtra("LETTUCE_NAME");

        plantBuddies.add(new Plant(radishName, "radish", R.drawable.plant_radish_happy));
        plantBuddies.add(new Plant(lettuceName, "lettuce", R.drawable.iceberg_lettuce_happy));
        plantBuddies.add(new Plant("Add New Buddy", "add", R.drawable.ic_add));
    }

    private void updateUI() {
        if (plantBuddies.isEmpty()) return;

        Plant currentPlant = plantBuddies.get(currentPlantIndex);
        binding.buddyName.setText(currentPlant.getName());
        binding.buddyAvatar.setImageResource(currentPlant.getImageResource());

        if (currentPlant.getType().equals("add")) {
            binding.buddyStatus.setVisibility(View.GONE);
            binding.buddyGrowthStage.setVisibility(View.GONE);
            binding.labelIWant.setVisibility(View.GONE);
            binding.buttonWaterBuddy.setVisibility(View.GONE);
            binding.buttonAnalyzePlant.setVisibility(View.GONE);
            binding.labelPlantAnalysis.setVisibility(View.GONE);
            binding.buttonSunlight.setVisibility(View.GONE);
            binding.buttonInfo.setVisibility(View.GONE);
        } else {
            binding.buddyStatus.setVisibility(View.VISIBLE);
            binding.buddyGrowthStage.setVisibility(View.VISIBLE);
            binding.labelIWant.setVisibility(View.VISIBLE);
            binding.buttonWaterBuddy.setVisibility(View.VISIBLE);
            binding.buttonAnalyzePlant.setVisibility(View.VISIBLE);
            binding.labelPlantAnalysis.setVisibility(View.VISIBLE);
            binding.buttonSunlight.setVisibility(View.VISIBLE);
            binding.buttonInfo.setVisibility(View.VISIBLE);

            binding.buddyStatus.setText(getString(R.string.status_happy));
            binding.buddyGrowthStage.setText(getString(R.string.stage_seed, currentPlant.getName()));
        }
    }

    private void animateAndSwitch(final int newIndex) {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                currentPlantIndex = newIndex;
                updateUI();
                Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                binding.centralCard.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        binding.centralCard.startAnimation(fadeOut);
    }

    private void setupClickListeners() {
        binding.menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        binding.arrowLeft.setOnClickListener(v -> {
            int newIndex = (currentPlantIndex - 1 + plantBuddies.size()) % plantBuddies.size();
            animateAndSwitch(newIndex);
        });

        binding.arrowRight.setOnClickListener(v -> {
            int newIndex = (currentPlantIndex + 1) % plantBuddies.size();
            animateAndSwitch(newIndex);
        });

        binding.buddyAvatarContainer.setOnClickListener(v -> {
            if (plantBuddies.get(currentPlantIndex).getType().equals("add")) {
                Toast.makeText(MainActivity.this, "Feature to add a new plant is coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener actionListener = v -> {
            Plant currentPlant = plantBuddies.get(currentPlantIndex);
            String message = "";
            if (v.getId() == R.id.button_water_buddy) {
                message = getString(R.string.buddy_watered_message, currentPlant.getName());
            } else if (v.getId() == R.id.button_analyze_plant) {
                message = "Analyzing " + currentPlant.getName() + ". " + getString(R.string.analysis_default_question, currentPlant.getName());
            } else if (v.getId() == R.id.button_sunlight) {
                message = getString(R.string.buddy_sunlight_message, currentPlant.getName());
            }
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        };

        binding.buttonWaterBuddy.setOnClickListener(actionListener);
        binding.buttonAnalyzePlant.setOnClickListener(actionListener);
        binding.buttonSunlight.setOnClickListener(actionListener);

        binding.buttonInfo.setOnClickListener(v -> {
            Plant currentPlant = plantBuddies.get(currentPlantIndex);
            Toast.makeText(MainActivity.this, "Info for " + currentPlant.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Prevent memory leaks
    }

    private static class Plant {
        private final String name;
        private final String type;
        private final int imageResource;

        public Plant(String name, String type, int imageResource) {
            this.name = name;
            this.type = type;
            this.imageResource = imageResource;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public int getImageResource() {
            return imageResource;
        }
    }
}