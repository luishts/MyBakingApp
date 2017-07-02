package com.example.android.mybakingapp.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.adapter.IngredientAdapter;
import com.example.android.mybakingapp.adapter.StepsAdapter;
import com.example.android.mybakingapp.component.CustomRecyclerView;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.util.Util;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Fragment that displays all the steps of a given Recipe. When a step is clicked at phone device,
 * StepDetailActivity is called but when a step is clicked from a tablet, just update the
 * ExoPlayer that is shown in the other fragment
 */
public class RecipeFragment extends Fragment implements StepsAdapter.StepClickListener {

    private String TAG = RecipeFragment.class.getName();

    private boolean mTwoPanelMode;

    private TwoPanelStepClickListener mTwoPanelStepClickListener;

    private Unbinder unbinder;

    private Recipe mRecipe;

    //step
    private int mCurrentStep;
    private StepsAdapter mStepAdapter;
    @BindView(R.id.step_list)
    public CustomRecyclerView mStepList;
    //step

    //ingredients
    @BindView(R.id.ingredients_title)
    TextView txtIngredientsTitle;
    @BindView(R.id.card_view)
    CardView cardViewMain;
    @BindView(R.id.card_view_detail)
    CardView cardViewDetail;
    @BindView(R.id.ingredient_list)
    CustomRecyclerView mIngredientList;
    //ingredients

    private IngredientAdapter mIngredientAdapter;

    public static RecipeFragment newInstance(Recipe recipe) {
        RecipeFragment recipeFragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putParcelable("recipe_key", recipe);
        recipeFragment.setArguments(args);
        return recipeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTwoPanelMode = getResources().getBoolean(R.bool.twoPaneMode);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRecipe = bundle.getParcelable(getString(R.string.recipe_key));
        }
        mCurrentStep = -1;
        mStepAdapter = new StepsAdapter(getActivity(), Arrays.asList(mRecipe.getSteps()));
        mStepAdapter.setStepClickListener(this);

        mIngredientAdapter = new IngredientAdapter(getActivity(), Arrays.asList(mRecipe.getIngredients()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        mStepList.setLayoutManager(new LinearLayoutManager(getContext()));
        mStepList.getLayoutManager().setAutoMeasureEnabled(true);

        ViewStub stubStepView = (ViewStub) rootView.findViewById(R.id.stub_step);
        stubStepView.setLayoutResource(R.layout.empty_recipe);
        mStepList.setEmptyView(stubStepView);
        mStepList.setAdapter(mStepAdapter);

        mIngredientList.setLayoutManager(new LinearLayoutManager(getContext()));
        mIngredientList.getLayoutManager().setAutoMeasureEnabled(true);

        ViewStub stubIngredientView = (ViewStub) rootView.findViewById(R.id.stub_ingredient);
        stubIngredientView.setLayoutResource(R.layout.empty_ingredient);
        mIngredientList.setEmptyView(stubIngredientView);
        mIngredientList.setAdapter(mIngredientAdapter);

        txtIngredientsTitle.setText(getString(R.string.recipe_component_ingredient));

        getActivity().setTitle(mRecipe.getName());

        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.card_view)
    public void onClickIngredient() {
        if (cardViewDetail.getVisibility() == View.VISIBLE) {
            cardViewMain.setLayoutParams(Util.getLayoutParamsGone(getContext()));
            cardViewDetail.setVisibility(View.GONE);
        } else {
            cardViewMain.setLayoutParams(Util.getLayoutParamsVisible(getContext()));
            cardViewDetail.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStepSelected(int position) {
        mCurrentStep = position;
        if (mTwoPanelMode && mTwoPanelStepClickListener != null) {
            mTwoPanelStepClickListener.onStepTwoPanelSelected(position);
        } else {
            Intent intent = new Intent(getActivity(), StepDetailActivity.class);
            intent.putExtra(getString(R.string.recipe_key), mRecipe);
            intent.putExtra(getString(R.string.step_key), position);
            startActivity(intent);
        }
    }

    //callback for tablets, update the player fragment with new step
    public interface TwoPanelStepClickListener {
        void onStepTwoPanelSelected(int position);
    }

    //tablet callback for exoplayer video
    public void setStepClickListener(TwoPanelStepClickListener twoPanelStepClickListener) {
        this.mTwoPanelStepClickListener = twoPanelStepClickListener;
    }
}
