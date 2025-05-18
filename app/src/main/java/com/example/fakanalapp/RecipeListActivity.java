package com.example.fakanalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecipes;
    private Spinner spinnerFilter;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private FirebaseFirestore db;
    private int selectedFilterIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));
        spinnerFilter = findViewById(R.id.spinnerFilter);
        db = FirebaseFirestore.getInstance();

        adapter = new RecipeAdapter(recipeList, new RecipeAdapter.OnRecipeClickListener() {
            @Override
            public void onEdit(Recipe recipe, int position) {
                Intent intent = new Intent(RecipeListActivity.this, RecipeEditActivity.class);
                intent.putExtra("docId", recipe.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Recipe recipe, int position) {
                if (recipe.getId() == null || recipe.getId().isEmpty()) {
                    Toast.makeText(RecipeListActivity.this, "Hibás recept ID!", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("recipes").document(recipe.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            recipeList.remove(position);
                            adapter.notifyItemRemoved(position);
                            Toast.makeText(RecipeListActivity.this, "Recept törölve!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(RecipeListActivity.this, "Hiba történt a törlés során!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFavorite(Recipe recipe, int position) {
                boolean newState = !recipe.isFavorite();
                recipe.setFavorite(newState);

                if (recipe.getId() != null) {
                    db.collection("recipes").document(recipe.getId())
                            .update("favorite", newState)
                            .addOnSuccessListener(unused -> {
                                adapter.notifyItemChanged(position);
                                Toast.makeText(
                                        RecipeListActivity.this,
                                        newState ? "Kedvencekhez adva!" : "Eltávolítva a kedvencek közül.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                }
            }

            @Override
            public void onShowDetail(Recipe recipe) {
                Intent intent = new Intent(RecipeListActivity.this, RecipeDetailActivity.class);
                intent.putExtra("name", recipe.getName());
                intent.putExtra("category", recipe.getCategory());
                intent.putExtra("description", recipe.getDescription());
                intent.putStringArrayListExtra("ingredients", new ArrayList<>(recipe.getIngredients()));
                startActivity(intent);
            }
        });

        recyclerViewRecipes.setAdapter(adapter);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.recipe_filters,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilterIndex = position;
                applyFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadAllRecipes(); // Alapértelmezett betöltés
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilter(selectedFilterIndex);
    }

    private void applyFilter(int position) {
        switch (position) {
            case 0: loadAllRecipes(); break;
            case 1: loadDessertsOnly(); break;
            case 2: loadSortedByName(); break;
            case 3: loadLimitedRecipes(); break;
            case 4: loadFavoritesOnly(); break;
        }
    }

    private void loadAllRecipes() {
        db.collection("recipes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recipeList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        recipe.setId(doc.getId());
                        recipeList.add(recipe);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadDessertsOnly() {
        db.collection("recipes")
                .whereEqualTo("category", "desszert")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recipeList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        recipe.setId(doc.getId());
                        recipeList.add(recipe);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadSortedByName() {
        db.collection("recipes")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recipeList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        recipe.setId(doc.getId());
                        recipeList.add(recipe);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadLimitedRecipes() {
        db.collection("recipes")
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recipeList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        recipe.setId(doc.getId());
                        recipeList.add(recipe);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadFavoritesOnly() {
        db.collection("recipes")
                .whereEqualTo("favorite", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recipeList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        recipe.setId(doc.getId());
                        recipeList.add(recipe);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
