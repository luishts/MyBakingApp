package com.example.android.mybakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.model.Step;
import com.example.android.mybakingapp.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Activity that holds a reference for ExoPlayer fragment and shows two buttons for next step
 * and previous step (portrait). For landscape, it shows the ExoPlayer fragment with fullscreen set.
 */

public class StepDetailActivity extends AppCompatActivity {

    private String TAG = StepDetailActivity.class.getName();

    private Recipe mRecipe;
    private Step mCurrentStep;
    private int mCurrentStepPosition;

    private PlayerFragment mPlayerFragment;

    @Nullable
    @BindView(R.id.next_button)
    public ImageView mNextButton;

    @Nullable
    @BindView(R.id.before_button)
    public ImageView mBeforeButton;

    @Nullable
    @BindView(R.id.text_navigate_before)
    public TextView mTextBefore;

    @Nullable
    @BindView(R.id.text_navigate_next)
    public TextView mTextNext;

    @Nullable
    @BindView(R.id.text_current_step)
    public TextView mTextCurrentStep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_detail_activity);
        ButterKnife.bind(this);

        mCurrentStepPosition = -1;
        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(Constants.CURRENT_RECIPE);
            mCurrentStepPosition = savedInstanceState.getInt(Constants.CURRENT_STEP_POSITION);
        } else if (getIntent() != null) {
            mRecipe = getIntent().getParcelableExtra(getString(R.string.recipe_key));
            mCurrentStepPosition = getIntent().getIntExtra(getString(R.string.step_key), 0);
        }
        mCurrentStep = mRecipe.getSteps()[mCurrentStepPosition];
        initUI();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initUI() {
        setTitle(mRecipe.getName());
        setupButtons();
        FragmentManager fm = getSupportFragmentManager();
        mPlayerFragment = (PlayerFragment) fm.findFragmentByTag(Constants.TAG_PLAYER_FRAGMENT);
        if (mPlayerFragment == null) {
            mPlayerFragment = PlayerFragment.newInstance(mCurrentStep);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.player_container, mPlayerFragment, Constants.TAG_PLAYER_FRAGMENT)
                .commit();
    }

    @Optional
    @OnClick(R.id.before_button)
    public void beforeComponent() {
        if (mCurrentStepPosition - 1 >= 0) {
            mCurrentStepPosition--;
        } else {
            mCurrentStepPosition = mRecipe.getSteps().length - 1;
        }
        mCurrentStep = mRecipe.getSteps()[mCurrentStepPosition];
        setupButtons();
        mPlayerFragment.changeStep(mCurrentStep);
    }

    @Optional
    @OnClick(R.id.next_button)
    public void nextComponent() {
        if (mCurrentStepPosition + 1 < mRecipe.getSteps().length) {
            mCurrentStepPosition++;
        } else {
            mCurrentStepPosition = 0;
        }
        mCurrentStep = mRecipe.getSteps()[mCurrentStepPosition];
        setupButtons();
        mPlayerFragment.changeStep(mCurrentStep);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.CURRENT_RECIPE, mRecipe);
        outState.putInt(Constants.CURRENT_STEP_POSITION, mCurrentStepPosition);
    }

    private void setupButtons() {
        if (mTextBefore != null && mTextNext != null && mTextCurrentStep != null) {
            int stepsLength = mRecipe.getSteps().length;
            if (mCurrentStepPosition - 1 >= 0) {
                mTextBefore.setText(mRecipe.getSteps()[mCurrentStepPosition - 1].getShortDescription());
            } else {
                mTextBefore.setText(mRecipe.getSteps()[stepsLength - 1].getShortDescription());
            }

            if (mCurrentStepPosition + 1 < stepsLength) {
                mTextNext.setText(mRecipe.getSteps()[mCurrentStepPosition + 1].getShortDescription());
            } else {
                mTextNext.setText(mRecipe.getSteps()[0].getShortDescription());
            }

            mTextCurrentStep.setText(mCurrentStep.getShortDescription());
        }
    }

}
