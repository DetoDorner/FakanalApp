package com.example.fakanalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onEdit(Recipe recipe, int position);
        void onDelete(Recipe recipe, int position);
        void onFavorite(Recipe recipe, int position);
        void onShowDetail(Recipe recipe);
    }

    private List<Recipe> recipeList;
    private OnRecipeClickListener listener;
    private SharedPreferences prefs;
    private Set<String> favorites;

    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);

        if (prefs == null) {
            prefs = parent.getContext().getSharedPreferences("fakanal_prefs", Context.MODE_PRIVATE);
            favorites = prefs.getStringSet("favorites", new HashSet<>());
        }

        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.textViewName.setText(recipe.getName());
        holder.textViewCategory.setText(recipe.getCategory());
        holder.textViewDescription.setText(recipe.getDescription());

        // Hozzávalók
        List<String> ingredientsList = recipe.getIngredients();
        if (ingredientsList != null && !ingredientsList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String ing : ingredientsList) {
                sb.append("• ").append(ing).append("\n");
            }
            String allIngredients = sb.toString().trim();
            String[] lines = allIngredients.split("\n");
            if (lines.length > 2) {
                allIngredients = lines[0] + "\n" + lines[1] + "\n…";
            }
            holder.textViewIngredients.setText(allIngredients);
            holder.textViewIngredients.setVisibility(View.VISIBLE);
        } else {
            holder.textViewIngredients.setVisibility(View.GONE);
        }

        // Kategória színezés
        String category = recipe.getCategory() != null ? recipe.getCategory().trim().toLowerCase() : "";
        int color = Color.GRAY;
        switch (category) {
            case "leves":    color = Color.parseColor("#388E3C"); break;
            case "főétel":   color = Color.parseColor("#1976D2"); break;
            case "desszert": color = Color.parseColor("#D32F2F"); break;
            case "snack":    color = Color.parseColor("#FFD600"); break;
        }
        holder.viewCategoryStripe.setBackgroundColor(color);

        // Hosszú recept háttér
        String description = recipe.getDescription();
        if (description != null && description.length() > 100) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFF3E0"));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // Kedvenc ikon beállítása
        boolean isFavorite = favorites != null && favorites.contains(recipe.getId());
        holder.buttonFavorite.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        // --- Gomb események ---
        holder.buttonEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(recipe, position);
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(recipe, position);
        });

        holder.buttonFavorite.setOnClickListener(v -> {
            if (favorites == null) favorites = new HashSet<>();
            if (isFavorite) {
                favorites.remove(recipe.getId());
                holder.buttonFavorite.setImageResource(R.drawable.ic_heart_outline);
            } else {
                favorites.add(recipe.getId());
                holder.buttonFavorite.setImageResource(R.drawable.ic_heart_filled);
            }

            prefs.edit().putStringSet("favorites", favorites).apply();

            if (listener != null) listener.onFavorite(recipe, position);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onShowDetail(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        View viewCategoryStripe;
        TextView textViewName, textViewCategory, textViewDescription, textViewIngredients;
        ImageButton buttonEdit, buttonDelete, buttonFavorite;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryStripe = itemView.findViewById(R.id.viewCategoryStripe);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewIngredients = itemView.findViewById(R.id.textViewIngredients);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
        }
    }

    public void setRecipeList(List<Recipe> newRecipes) {
        this.recipeList = newRecipes;
        notifyDataSetChanged();
    }
}
