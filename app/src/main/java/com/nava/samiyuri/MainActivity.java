package com.nava.samiyuri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.nava.samiyuri.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- Edge-to-edge layout handling ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Create a single listener for all buttons
        View.OnClickListener comingSoonListener = v ->
                Toast.makeText(MainActivity.this, R.string.feature_coming_soon, Toast.LENGTH_SHORT).show();

        // Assign the listener to all buttons
        binding.buttonWaterBuddy.setOnClickListener(comingSoonListener);
        binding.buttonAnalyzePlant.setOnClickListener(comingSoonListener);
        binding.buttonSunlight.setOnClickListener(comingSoonListener);
        binding.buttonInfo.setOnClickListener(comingSoonListener);
        binding.arrowLeft.setOnClickListener(comingSoonListener);
        binding.arrowRight.setOnClickListener(comingSoonListener);
        binding.menuIcon.setOnClickListener(comingSoonListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Prevent memory leaks
    }
}