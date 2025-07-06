package com.nava.samiyuri;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nava.samiyuri.databinding.ActivityStoryBinding;

public class StoryActivity extends AppCompatActivity {

    private ActivityStoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonNext.setOnClickListener(v -> {
            Intent intent = new Intent(StoryActivity.this, NamingCeremonyActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}