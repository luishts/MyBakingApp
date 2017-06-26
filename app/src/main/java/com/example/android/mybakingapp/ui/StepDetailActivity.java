package com.example.android.mybakingapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ltorres on 26/06/2017.
 */

public class StepDetailActivity extends AppCompatActivity {

    private String TAG = StepDetailActivity.class.getName();

    private Recipe mRecipe;
    private Step mCurrentStep;
    private int mCurrentStepPosition;

    private StepDetailFragment mStepDetailFragment;

    @BindView(R.id.next_button)
    public ImageView mNextButton;

    @BindView(R.id.before_button)
    public ImageView mBeforeButton;

    @BindView(R.id.text_navigate_before)
    public TextView mTextBefore;

    @BindView(R.id.text_navigate_next)
    public TextView mTextNext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_detail_activity);

        ButterKnife.bind(this);

        if (getIntent() != null) {
            mRecipe = getIntent().getParcelableExtra(getString(R.string.recipe_key));
            mCurrentStepPosition = getIntent().getIntExtra(getString(R.string.step_key), 0);
            mCurrentStep = mRecipe.getSteps()[mCurrentStepPosition];
            initUI();
        }
    }

    private void initUI() {
        mStepDetailFragment = new StepDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.step_key), mCurrentStep);
        mStepDetailFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_content, mStepDetailFragment)
                .commit();
        setupNextAndPreviousButton();
    }

    @OnClick(R.id.before_button)
    public void beforeComponent() {
        if(mCurrentStepPosition -1 >= 0){
            mCurrentStepPosition--;
        } else{
            mCurrentStepPosition = mRecipe.getSteps().length -1;
        }
        mCurrentStep = mRecipe.getSteps()[mCurrentStepPosition];
        setupNextAndPreviousButton();
        mStepDetailFragment.changeStep(mCurrentStep);
    }

    @OnClick(R.id.next_button)
    public void nextComponent() {
        if(mCurrentStepPosition +1 < mRecipe.getSteps().length){
            mCurrentStepPosition++;
        } else{
            mCurrentStepPosition = 0;
        }
        mCurrentStep = mRecipe.getSteps()[mCurrentStepPosition];
        setupNextAndPreviousButton();
        mStepDetailFragment.changeStep(mCurrentStep);
    }

    private void setupNextAndPreviousButton(){
        int stepsLength = mRecipe.getSteps().length;
        if(mCurrentStepPosition -1 >= 0){
            mTextBefore.setText(mRecipe.getSteps()[mCurrentStepPosition-1].getShortDescription());
        } else{
            mTextBefore.setText(mRecipe.getSteps()[stepsLength-1].getShortDescription());
        }

        if(mCurrentStepPosition +1 < stepsLength){
            mTextNext.setText(mRecipe.getSteps()[mCurrentStepPosition+1].getShortDescription());
        } else{
            mTextNext.setText(mRecipe.getSteps()[0].getShortDescription());
        }
    }
}
