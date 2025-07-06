package com.nava.samiyuri;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nava.samiyuri.databinding.ActivityNamingCeremonyBinding;

public class NamingCeremonyActivity extends AppCompatActivity {

    private ActivityNamingCeremonyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNamingCeremonyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonStartAdventure.setOnClickListener(v -> {
            String radishName = binding.editTextRadishName.getText().toString().trim();
            String lettuceName = binding.editTextLettuceName.getText().toString().trim();

            if (radishName.isEmpty() || lettuceName.isEmpty()) {
                Toast.makeText(this, "Please name both of your new buddies!", Toast.LENGTH_SHORT).show();
            } else {
                // In a real app, you would save these names
                Intent intent = new Intent(NamingCeremonyActivity.this, MainActivity.class);
                // You can pass the names to the MainActivity if needed
                intent.putExtra("RADISH_NAME", radishName);
                intent.putExtra("LETTUCE_NAME", lettuceName);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}