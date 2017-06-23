package com.example.android.mybakingapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Recipe;

public class MainActivity extends AppCompatActivity implements RecipeListFragment.RecipeListCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecipeListFragment recipeListFragment = new RecipeListFragment();
        recipeListFragment.setRecipeListCallback(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_container, recipeListFragment)
                .commit();
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        RecipeFragment recipeFragment = new RecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.recipe_key), recipe);
        recipeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_container, recipeFragment)
                .commit();
    }
}
