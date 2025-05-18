package com.example.fakanalapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecipeEditActivity extends AppCompatActivity {

    private EditText editTextName, editTextDescription;
    private Spinner spinnerEditCategory;
    private Button buttonSave;
    private FirebaseFirestore db;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_edit);

        editTextName = findViewById(R.id.editTextEditName);
        editTextDescription = findViewById(R.id.editTextEditDescription);
        spinnerEditCategory = findViewById(R.id.spinnerEditCategory);
        buttonSave = findViewById(R.id.buttonEditSave);

        // Spinner beállítása
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.recipe_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditCategory.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        docId = getIntent().getStringExtra("docId");

        // Firestore adat betöltés
        if (docId != null) {
            db.collection("recipes").document(docId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            editTextName.setText(documentSnapshot.getString("name"));
                            editTextDescription.setText(documentSnapshot.getString("description"));
                            String category = documentSnapshot.getString("category");
                            if (category != null) {
                                int position = adapter.getPosition(category);
                                spinnerEditCategory.setSelection(position);
                            }
                        }
                    });
        }

        buttonSave.setOnClickListener(v -> saveRecipe());
    }

    private void saveRecipe() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category = spinnerEditCategory.getSelectedItem().toString();

        if (name.isEmpty() || description.isEmpty() || category.equals("Válassz kategóriát!")) {
            Toast.makeText(this, "Tölts ki minden mezőt és válassz kategóriát!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean easy = description.length() <= 100;

        if (docId != null) {
            DocumentReference docRef = db.collection("recipes").document(docId);
            docRef.update(
                    "name", name,
                    "description", description,
                    "category", category,
                    "easy", easy
            ).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Recept frissítve!", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Frissítés sikertelen!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
