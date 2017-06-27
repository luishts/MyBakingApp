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
    private SimpleExoPlayer mExoPlayer;

    @Nullable
    @BindView(R.id.playerView)
    public SimpleExoPlayerView mPlayerView;

    @Nullable
    @BindView(R.id.iv_no_video)
    public ImageView mErrorView;

    @Nullable
    @BindView(R.id.step_description)
    public TextView mStepDescription;

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
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void initUI() {
        setTitle(mRecipe.getName());
        setupPlayer();
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
        changeStep(mCurrentStep);
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
        changeStep(mCurrentStep);
    }

    public void changeStep(Step step) {
        releasePlayer();
        mCurrentStep = step;
        setupPlayer();
    }

    public void setupPlayer() {
        if (mStepDescription != null) {
            mStepDescription.setText(mCurrentStep.getDescription());
        }
        setupButtons();
        Uri videoUri = NetworkUtils.getUriFromURL(mCurrentStep.getVideoURL());
        if (videoUri != null) {
            initializePlayer(videoUri);
            showErrorView(false);
        } else {
            mPlayerView.setVisibility(View.GONE);
            showErrorView(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.CURRENT_RECIPE, mRecipe);
        outState.putInt(Constants.CURRENT_STEP_POSITION, mCurrentStepPosition);
    }

    private void showErrorView(boolean show) {
        if (mErrorView != null) {
            if (show) {
                mErrorView.setVisibility(View.VISIBLE);
                mPlayerView.setVisibility(View.GONE);
            } else {
                mErrorView.setVisibility(View.GONE);
                mPlayerView.setVisibility(View.VISIBLE);
            }
        }
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

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }
}
