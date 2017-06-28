package com.example.android.mybakingapp.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.model.Step;
import com.example.android.mybakingapp.util.Constants;
import com.example.android.mybakingapp.util.NetworkUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by ltorres on 26/06/2017.
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
        mPlayerFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.step_key), mCurrentStep);
        mPlayerFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.player_container, mPlayerFragment)
                .addToBackStack(null)
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
