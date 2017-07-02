package com.example.android.mybakingapp.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity that shows all details of a Recipe (phone) but for tablets also show the player fragment like two panels
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

        FragmentManager fm = getSupportFragmentManager();

        int currentStep = 0;
        mRecipeFragment = (RecipeFragment) fm.findFragmentByTag(Constants.TAG_RECIPE_FRAGMENT);
        if (mRecipeFragment == null) {
            mRecipeFragment = RecipeFragment.newInstance(mRecipe);
            fm.beginTransaction()
                    .add(mRecipeContent.getId(), mRecipeFragment, Constants.TAG_RECIPE_FRAGMENT)
                    .commit();
        }
        mPlayerFragment = (PlayerFragment) fm.findFragmentByTag(Constants.TAG_PLAYER_FRAGMENT);
        if (mPlayerContent != null) {
            //tablet configuration
            if (mPlayerFragment == null) {
                mPlayerFragment = PlayerFragment.newInstance(mRecipe.getSteps()[0]);
                fm.beginTransaction()
                        .add(mPlayerContent.getId(), mPlayerFragment, Constants.TAG_PLAYER_FRAGMENT)
                        .commit();
            }
        }
        if (mTwoPanelMode) {
            mRecipeFragment.setStepClickListener(this);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
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
