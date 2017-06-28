package com.example.android.mybakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ltorres on 28/06/2017.
 */

public class RecipeActivity extends AppCompatActivity implements RecipeFragment.TwoPanelStepClickListener {

    private String TAG = RecipeActivity.class.getName();

    private Recipe mRecipe;

    private boolean mTwoPanelMode;

    @Nullable
    @BindView(R.id.recipe_container)
    public FrameLayout mRecipeContent;

    @Nullable
    @BindView(R.id.player_container)
    public FrameLayout mPlayerContent;

    public RecipeFragment mRecipeFragment;

    public PlayerFragment mPlayerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity);
        ButterKnife.bind(this);

        mTwoPanelMode = getResources().getBoolean(R.bool.twoPaneMode);

        if (getIntent() != null) {
            mRecipe = getIntent().getParcelableExtra(getString(R.string.recipe_key));
        }

        Bundle bundle = new Bundle();
        if (mPlayerContent != null) {
            //tablet configuration
            bundle.putParcelable(getString(R.string.step_key), mRecipe.getSteps()[0]);
            mPlayerFragment = new PlayerFragment();
            mPlayerFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(mPlayerContent.getId(), mPlayerFragment)
                    .addToBackStack(null)
                    .commit();
        }

        bundle.putParcelable(getString(R.string.recipe_key), mRecipe);
        mRecipeFragment = new RecipeFragment();
        if (mTwoPanelMode) {
            mRecipeFragment.setStepClickListener(this);
        }
        mRecipeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(mRecipeContent.getId(), mRecipeFragment)
                .addToBackStack(null)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onStepTwoPanelSelected(int position) {
        mPlayerFragment.changeStep(mRecipe.getSteps()[position]);
    }
}
