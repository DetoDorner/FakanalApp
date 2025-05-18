package com.example.fakanalapp;

import java.util.List;

public class Recipe {
    private String name;
    private String description;
    private String category;
    private boolean easy;
    private String id;
    private List<String> ingredients;
    private boolean favorite;

    // Üres konstruktor Firestore-hoz
    public Recipe() {}

    public Recipe(String name, String description, String category, boolean easy, List<String> ingredients) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.easy = easy;
        this.ingredients = ingredients;
        this.favorite = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isEasy() { return easy; }
    public void setEasy(boolean easy) { this.easy = easy; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
