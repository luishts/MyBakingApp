package com.example.android.mybakingapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.adapter.GridAdapter;
import com.example.android.mybakingapp.adapter.RecipeListAdapter;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.task.RecipeTask;
import com.example.android.mybakingapp.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import component.CustomRecyclerView;
import component.EndlessRecyclerViewScrollListener;

public class MainActivity extends AppCompatActivity implements RecipeTask.OnReceipeTaskCompleted, RecipeListAdapter.RecipeClickListener {

    private String TAG = MainActivity.class.getName();

    private Handler mHandler;
    private ProgressDialog mProgressDialog;

    private ArrayList<Recipe> mRecipeList;

    private GridAdapter mGridAdapter;

    @Nullable
    @BindView(R.id.gridview)
    public GridView mRecipeGridView;

    private RecipeListAdapter mAdapter;

    @Nullable
    @BindView(R.id.recyclerView)
    public CustomRecyclerView mListView;
    private EndlessRecyclerViewScrollListener scrollListener;

    private final int MAX_RECIPE_PER_PAGE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (mListView != null) {
            mHandler = new Handler();
            mAdapter = new RecipeListAdapter(this, mRecipeList);
            mAdapter.setRecipeClickListener(this);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mListView.setLayoutManager(linearLayoutManager);
            mListView.getLayoutManager().setAutoMeasureEnabled(true);
            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(final int page, int totalItemCount) {
                    //if totalItemCount < than page*MAX_RECIPE_PER_PAGE, it means that all recipes from recipes list are already being shown
                    if (totalItemCount < MAX_RECIPE_PER_PAGE * page) {
                        Log.d(TAG, "all recipes from list are already being shown. totalItemCount = " + totalItemCount + " MAX_RECIPE_PER_PAGE * page = " + (MAX_RECIPE_PER_PAGE * page));
                        return;
                    } else {
                        mAdapter.addLoadingItem();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadNextRecipesFromList(page);
                            }
                        }, Constants.ENDLESS_SCROLL_ANIMATION_TIME);
                    }
                }
            };
            scrollListener.setVisibleThreshold(3);
            // Adds the scroll listener to RecyclerView
            mListView.addOnScrollListener(scrollListener);

            ViewStub stubView = (ViewStub) findViewById(R.id.stub);
            stubView.setLayoutResource(R.layout.empty_recipe);
            mListView.setEmptyView(stubView);
            mListView.setAdapter(mAdapter);

        } else if (mRecipeGridView != null) {

            mRecipeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onRecipeSelected(mRecipeList.get(position));
                }
            });
        }

        if (savedInstanceState != null) {
            mRecipeList = savedInstanceState.getParcelableArrayList(Constants.RECIPE_LIST);
            onTaskCompleted(mRecipeList);
        } else {
            new RecipeTask(this).execute();
        }

        setTitle(getString(R.string.recipe_activity_title));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.RECIPE_LIST, mRecipeList);
    }

    @Override
    public void onTaskCreated() {
        showProgress(true);
    }

    @Override
    public void onTaskCompleted(ArrayList<Recipe> receipes) {
        mRecipeList = receipes;
        if (mListView != null) {
            setRecipeList();
            scrollListener.resetState();
        } else if(mRecipeGridView != null){
            mGridAdapter = new GridAdapter(this, mRecipeList);
            mRecipeGridView.setAdapter(mGridAdapter);
            mGridAdapter.notifyDataSetChanged();
        }
        showProgress(false);
    }

    /**
     * Method that updates adapter with more notification from database given a 'page' value
     *
     * @param page
     */
    private void loadNextRecipesFromList(int page) {
        mAdapter.setMoreRecipes(mRecipeList.subList(page * MAX_RECIPE_PER_PAGE, MAX_RECIPE_PER_PAGE));
    }

    /**
     * show or hide progress spinner while login
     *
     * @param show
     */
    private void showProgress(final boolean show) {
        if (show) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.recipe_download_message));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    private void setRecipeList() {
        if (mAdapter != null) {
            if (mRecipeList != null && mRecipeList.size() > MAX_RECIPE_PER_PAGE) {
                mAdapter.setModels(mRecipeList.subList(0, MAX_RECIPE_PER_PAGE));
            } else if (mRecipeList != null) {
                mAdapter.setModels(mRecipeList.subList(0, mRecipeList.size()));
            } else {
                mAdapter.setModels(null);
            }
        }
    }

    public void onRecipeSelected(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(getString(R.string.recipe_key), recipe);
        startActivity(intent);
    }

    @Override
    public void onRecipeSelected(int position) {
        onRecipeSelected(mRecipeList.get(position));
    }
}
