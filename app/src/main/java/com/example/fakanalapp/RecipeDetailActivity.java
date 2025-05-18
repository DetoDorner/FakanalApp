package com.example.fakanalapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView textViewDetailName, textViewDetailCategory, textViewDetailDescription, textViewDetailIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        textViewDetailName = findViewById(R.id.textViewDetailName);
        textViewDetailCategory = findViewById(R.id.textViewDetailCategory);
        textViewDetailDescription = findViewById(R.id.textViewDetailDescription);
        textViewDetailIngredients = findViewById(R.id.textViewDetailIngredients);

        // Értékek átvétele
        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        String description = getIntent().getStringExtra("description");
        ArrayList<String> ingredients = getIntent().getStringArrayListExtra("ingredients");

        textViewDetailName.setText(name);
        textViewDetailCategory.setText("Kategória: " + category);
        textViewDetailDescription.setText(description);
        if (ingredients != null && !ingredients.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String ing : ingredients) {
                sb.append("• ").append(ing).append("\n");
            }
            textViewDetailIngredients.setText("Hozzávalók:\n" + sb.toString().trim());
        } else {
            textViewDetailIngredients.setText("Nincs hozzávaló megadva.");
        }
    }
}
