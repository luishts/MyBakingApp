package com.example.android.mybakingapp.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.adapter.RecipeAdapter;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.model.RecipeBaseComponent;
import com.example.android.mybakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;

import component.CustomRecyclerView;


/**
 * Created by ltorres on 8/22/2016.
 */
public class RecipeFragment extends Fragment implements RecipeAdapter.ComponentClickListener {

    private String TAG = RecipeFragment.class.getName();

    private Recipe mRecipe;
    private RecipeAdapter mAdapter;
    private CustomRecyclerView mRecyclerView;

    private List<RecipeBaseComponent> mRecipeBaseComponents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRecipe = bundle.getParcelable(getString(R.string.recipe_key));
            if (mRecipe != null) {
                initRecipeComponents();
            }
        }
        mAdapter = new RecipeAdapter(getActivity(), mRecipeBaseComponents);
        mAdapter.setComponentClickListener(this);
    }

    private void initRecipeComponents() {
        mRecipeBaseComponents = new ArrayList<>();
        RecipeBaseComponent ingredientComponent;
        if (mRecipe.getSteps() != null) {
            RecipeBaseComponent stepComponent;
            for (Step step : mRecipe.getSteps()) {
                stepComponent = new RecipeBaseComponent((step.getId() + 1) + ". " + step.getShortDescription());
                mRecipeBaseComponents.add(stepComponent);
            }
        }
        if (mRecipe.getIngredients() != null) {
            ingredientComponent = new RecipeBaseComponent(getString(R.string.recipe_component_ingredient) + ": " + mRecipe.getIngredients().length);
            mRecipeBaseComponents.add(0, ingredientComponent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recipe_fragment, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = (CustomRecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.getLayoutManager().setAutoMeasureEnabled(true);

        ViewStub stubView = (ViewStub) rootView.findViewById(R.id.stub);
        stubView.setLayoutResource(R.layout.empty_recipe);
        mRecyclerView.setEmptyView(stubView);
        mRecyclerView.setAdapter(mAdapter);

        getActivity().setTitle(getText(R.string.recipe_fragment_title));

        return rootView;

    }

    @Override
    public void onComponentSelected(int position) {
        if (position == 0) {
            //Ingredient
        } else {
            Intent intent = new Intent(getActivity(), StepDetailActivity.class);
            intent.putExtra(getString(R.string.recipe_key), mRecipe);
            intent.putExtra(getString(R.string.step_key), position-1);
            startActivity(intent);
        }
    }
}
