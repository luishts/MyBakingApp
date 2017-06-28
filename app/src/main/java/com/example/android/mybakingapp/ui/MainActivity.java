package com.example.android.mybakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeListFragment.RecipeListCallback, RecipeGridFragment.RecipeGridCallback {

    @Nullable
    @BindView(R.id.recipe_grid_container)
    public FrameLayout mRecipeGridContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (mRecipeGridContainer != null) {

            RecipeGridFragment recipeGridFragment = new RecipeGridFragment();
            recipeGridFragment.setRecipeGridCallback(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(mRecipeGridContainer.getId(), recipeGridFragment)
                    .addToBackStack(null)
                    .commit();

        } else {

            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    int stackHeight = getSupportFragmentManager().getBackStackEntryCount();
                    if (stackHeight > 1) { // if we have something on the stack (doesn't include the current shown fragment)
                        getSupportActionBar().setHomeButtonEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    } else {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setHomeButtonEnabled(false);
                    }
                }

            });

            RecipeListFragment recipeListFragment = new RecipeListFragment();
            recipeListFragment.setRecipeListCallback(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_list_container, recipeListFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        RecipeFragment recipeFragment = new RecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.recipe_key), recipe);
        recipeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_list_container, recipeFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRecipeGridSelected(Recipe recipe) {
        RecipeFragment recipeFragment = new RecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.recipe_key), recipe);
        recipeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_grid_container, recipeFragment)
                .addToBackStack(null)
                .commit();
    }
}
