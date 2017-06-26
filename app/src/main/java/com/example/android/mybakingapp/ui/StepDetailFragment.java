package com.example.android.mybakingapp.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Step;
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
import butterknife.Unbinder;

/**
 * Created by ltorres on 26/06/2017.
 */

public class StepDetailFragment extends Fragment {

    @BindView(R.id.playerView)
    public SimpleExoPlayerView mPlayerView;

    @BindView(R.id.step_description)
    public TextView mStepDescription;

    private SimpleExoPlayer mExoPlayer;

    private Unbinder unbinder;

    private Step mCurrentStep;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.step_detail_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        // Load the question mark as the background image until the user answers the question.
        //mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
        //      (getResources(), R.drawable.question_mark));

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
        releasePlayer();
        unbinder.unbind();
    }

    private void initializeUI() {
        mStepDescription.setText(mCurrentStep.getDescription());
        initializePlayer(NetworkUtils.getUriFromURL(mCurrentStep.getVideoURL()));
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
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    public void changeStep(Step step){
        releasePlayer();
        mCurrentStep = step;
        initializeUI();
    }
}
