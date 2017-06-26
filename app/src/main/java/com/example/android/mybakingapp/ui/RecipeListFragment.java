package com.example.android.mybakingapp.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.adapter.RecipeListAdapter;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.task.RecipeTask;
import com.example.android.mybakingapp.util.Constants;

import java.util.List;

import component.CustomRecyclerView;
import component.EndlessRecyclerViewScrollListener;


/**
 * Created by ltorres on 8/22/2016.
 */
public class RecipeListFragment extends Fragment implements RecipeTask.OnReceipeTaskCompleted, RecipeListAdapter.RecipeClickListener {

    private String TAG = RecipeListFragment.class.getName();

    private final int MAX_RECIPE_PER_PAGE = 10;

    private ProgressDialog mProgressDialog;

    private RecipeListAdapter mAdapter;
    private CustomRecyclerView mRecyclerView;
    private EndlessRecyclerViewScrollListener scrollListener;

    private List<Recipe> mRecipes;
    private RecipeListCallback mRecipeListCallback;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        mAdapter = new RecipeListAdapter(getActivity(), mRecipes);
        mAdapter.setRecipeClickListener(this);
        new RecipeTask(RecipeListFragment.this).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recipe_list_fragment, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView = (CustomRecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.getLayoutManager().setAutoMeasureEnabled(true);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(final int page, int totalItemCount) {
                //if totalItemCount < than page*MAX_CALL_PER_PAGE, it means that all calls from db are already loaded
                if (totalItemCount < MAX_RECIPE_PER_PAGE * page) {
                    Log.d(TAG, "all calls from db are already loaded. totalItemCount = " + totalItemCount + " MAX_CALL_PER_PAGE * page = " + (MAX_RECIPE_PER_PAGE * page));
                    return;
                } else {
                    mAdapter.addLoadingItem();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextRecipesFromDB(page);
                        }
                    }, Constants.ENDLESS_SCROLL_ANIMATION_TIME);
                }
            }
        };
        scrollListener.setVisibleThreshold(3);
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        ViewStub stubView = (ViewStub) rootView.findViewById(R.id.stub);
        stubView.setLayoutResource(R.layout.empty_recipe);
        mRecyclerView.setEmptyView(stubView);
        mRecyclerView.setAdapter(mAdapter);

        getActivity().setTitle(getText(R.string.recipe_fragment_title));

        return rootView;

    }

    /**
     * Method that updates adapter with more notification from database given a 'page' value
     *
     * @param page
     */
    private void loadNextRecipesFromDB(int page) {
        mAdapter.setMoreRecipes(mRecipes.subList(page * MAX_RECIPE_PER_PAGE, MAX_RECIPE_PER_PAGE));
    }

    /**
     * show or hide progress spinner while login
     *
     * @param show
     */
    private void showProgress(final boolean show) {
        if (show) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.recipe_download_message));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    /**
     * Method loads calls from database and updates adapter
     */
    public void refreshRecipes() {
        new RecipeTask(RecipeListFragment.this).execute();
    }

    @Override
    public void onTaskCreated() {
        showProgress(true);
    }

    @Override
    public void onTaskCompleted(List<Recipe> receipes) {
        mRecipes = receipes;
        if (mAdapter != null) {
            if (mRecipes != null && mRecipes.size() > MAX_RECIPE_PER_PAGE) {
                mAdapter.setModels(mRecipes.subList(0, MAX_RECIPE_PER_PAGE));
            } else if (mRecipes != null) {
                mAdapter.setModels(mRecipes.subList(0, mRecipes.size()));
            } else {
                mAdapter.setModels(null);
            }
            scrollListener.resetState();
        }
        showProgress(false);
    }

    @Override
    public void onRecipeSelected(int position) {
        if (mRecipeListCallback != null && mRecipes != null) {
            mRecipeListCallback.onRecipeSelected(mRecipes.get(position));
        }
    }

    public interface RecipeListCallback {
        void onRecipeSelected(Recipe recipe);
    }

    public void setRecipeListCallback(RecipeListCallback mRecipeListCallback) {
        this.mRecipeListCallback = mRecipeListCallback;
    }
}
