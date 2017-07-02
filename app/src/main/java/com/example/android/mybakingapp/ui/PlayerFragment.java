package com.example.android.mybakingapp.ui;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Step;
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
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment that displays a ExoPlayer + Step description at phone portrait or tablet and full screen ExoPlayer for phone landscape
 */

public class PlayerFragment extends Fragment {

    private boolean mTwoPanelMode;

    private long mLastPosition;

    @Nullable
    @BindView(R.id.playerView)
    public SimpleExoPlayerView mPlayerView;

    @Nullable
    @BindView(R.id.step_description)
    public TextView mStepDescription;

    @Nullable
    @BindView(R.id.iv_no_video)
    public ImageView mErrorView;

    private SimpleExoPlayer mExoPlayer;

    private Unbinder unbinder;

    private Step mCurrentStep;

    public static PlayerFragment newInstance(Step step) {
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable("step_key", step);
        playerFragment.setArguments(args);
        return playerFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTwoPanelMode = getResources().getBoolean(R.bool.twoPaneMode);

        if (!mTwoPanelMode && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.player_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mCurrentStep = bundle.getParcelable(getString(R.string.step_key));
            initializeUI();
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLastPosition = mExoPlayer.getCurrentPosition();
        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeUI();
    }

    private void initializeUI() {
        if (mStepDescription != null) {
            mStepDescription.setText(mCurrentStep.getDescription());
        }
        if (!TextUtils.isEmpty(mCurrentStep.getThumbnailURL())) {
            Picasso.with(getContext())
                    .load(mCurrentStep.getThumbnailURL())
                    .placeholder(R.drawable.ic_videocam_off_black_48dp)
                    .error(R.drawable.ic_videocam_off_black_48dp)
                    .into(mErrorView);
        }
        Uri videoUri = com.example.android.mybakingapp.util.Util.getUriFromURL(mCurrentStep.getVideoURL());
        if (videoUri != null) {
            initializePlayer(videoUri);
            showErrorView(false);
        } else {
            mPlayerView.setVisibility(View.GONE);
            showErrorView(true);
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
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);

            if (mLastPosition != 0) {
                mExoPlayer.seekTo(mLastPosition);
            }

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

    public void changeStep(Step step) {
        releasePlayer();
        mCurrentStep = step;
        mLastPosition = 0;
        initializeUI();
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
}