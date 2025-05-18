package com.example.fakanalapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 🔔 Értesítési engedély kérése Android 13+ esetén
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }

        // Gombok animációval és egyedi stílussal
        Button buttonAddRecipe = findViewById(R.id.buttonAddRecipe);
        Button buttonShowRecipes = findViewById(R.id.buttonShowRecipes);

        buttonAddRecipe.setOnClickListener(v -> {
            animateButton(v);
            startActivity(new Intent(MainActivity.this, RecipeAddActivity.class));
        });

        buttonShowRecipes.setOnClickListener(v -> {
            animateButton(v);
            startActivity(new Intent(MainActivity.this, RecipeListActivity.class));
        });

        // Rendszer insets kezelése
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 🔄 Engedélykérés eredményének kezelése
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Értesítési engedély megadva", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Értesítési engedély megtagadva", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 🌀 Kattintási animáció (bepattanás)
    private void animateButton(View button) {
        button.setScaleX(0.95f);
        button.setScaleY(0.95f);
        button.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
    }
}
