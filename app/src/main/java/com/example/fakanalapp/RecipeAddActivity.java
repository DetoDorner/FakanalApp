package com.example.fakanalapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecipeAddActivity extends AppCompatActivity {

    private EditText editTextName, editTextDescription, editTextIngredient;
    private Spinner spinnerCategory;
    private CheckBox checkBoxEasy;
    private Button buttonSave, buttonAddIngredient;
    private TextView textViewIngredients;
    private FirebaseFirestore db;

    private List<String> ingredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_add);

        // UI elemek összekötése
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextIngredient = findViewById(R.id.editTextIngredient);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        checkBoxEasy = findViewById(R.id.checkBoxEasy);
        buttonSave = findViewById(R.id.buttonSave);
        buttonAddIngredient = findViewById(R.id.buttonAddIngredient);
        textViewIngredients = findViewById(R.id.textViewIngredients);

        // Spinner adapter beállítása
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.recipe_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Hozzávaló hozzáadása
        buttonAddIngredient.setOnClickListener(v -> {
            String ingredient = editTextIngredient.getText().toString().trim();
            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient);
                editTextIngredient.setText("");
                updateIngredientsView();
            } else {
                Toast.makeText(this, "Írj be egy hozzávalót!", Toast.LENGTH_SHORT).show();
            }
        });

        // Mentés gomb
        buttonSave.setOnClickListener(v -> saveRecipe());
    }

    // Hozzávalók szöveg frissítése
    private void updateIngredientsView() {
        if (ingredients.isEmpty()) {
            textViewIngredients.setText("(Nincsenek hozzávalók)");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String ing : ingredients) {
                sb.append("• ").append(ing).append("\n");
            }
            textViewIngredients.setText(sb.toString().trim());
        }
    }

    private void saveRecipe() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        boolean easy = checkBoxEasy.isChecked();

        // Figyelem: első elem legyen "Válassz kategóriát!"
        if (name.isEmpty() || description.isEmpty() || category.equals("Válassz kategóriát!") || ingredients.isEmpty()) {
            Toast.makeText(this, "Tölts ki minden mezőt, válassz kategóriát és adj meg legalább egy hozzávalót!", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe recipe = new Recipe(name, description, category, easy, new ArrayList<>(ingredients));

        db.collection("recipes")
                .add(recipe)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Recept mentve!", Toast.LENGTH_SHORT).show();
                    MyNotificationHelper.showNotification(this, "Recept mentve", "A recept sikeresen hozzáadva!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Mentés sikertelen!", Toast.LENGTH_SHORT).show();
                });
    }
}
