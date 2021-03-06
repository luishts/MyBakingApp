package com.example.android.mybakingapp.ui;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.example.android.mybakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.adapter.RecipeListAdapter;
import com.example.android.mybakingapp.component.CustomRecyclerView;
import com.example.android.mybakingapp.component.EndlessRecyclerViewScrollListener;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.receiver.ConnectivityUtil;
import com.example.android.mybakingapp.retrofit.BakingApiService;
import com.example.android.mybakingapp.util.Constants;
import com.example.android.mybakingapp.util.Util;
import com.example.android.mybakingapp.widget.BakingWidgetProvider;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MainActivity that displays a list (phone) or a grid (tablet) of recipes and also handles widgets clicks
 */

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.RecipeClickListener {

    private String TAG = MainActivity.class.getName();

    private Handler mHandler;
    private ProgressDialog mProgressDialog;

    private boolean mTabletMode;

    private int mWidgetId;
    private boolean isClickFromWidget;

    private ArrayList<Recipe> mRecipeList;

    @Nullable
    @BindView(R.id.recyclerView)
    public CustomRecyclerView mListView;
    private RecipeListAdapter mAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Nullable
    @BindView(R.id.error)
    public TextView errorView;

    private final int MAX_RECIPE_PER_PAGE = 10;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mTabletMode = getResources().getBoolean(R.bool.tabletMode);

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean(getString(R.string.widget_click))) {
            isClickFromWidget = true;
            mWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        }

        if (mTabletMode) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
            mListView.setLayoutManager(gridLayoutManager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mListView.setLayoutManager(linearLayoutManager);
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
        }

        mHandler = new Handler();
        mAdapter = new RecipeListAdapter(this, mRecipeList);
        mListView.setHasFixedSize(true);
        mAdapter.setRecipeClickListener(this);
        mListView.getLayoutManager().setAutoMeasureEnabled(true);

        ViewStub stubView = (ViewStub) findViewById(R.id.stub);
        stubView.setLayoutResource(R.layout.empty_recipe);
        mListView.setEmptyView(stubView);
        mListView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mRecipeList = savedInstanceState.getParcelableArrayList(Constants.RECIPE_LIST);
            initUI();
        } else {
            if (isConnected()) {
                errorView.setVisibility(View.GONE);
                //used for Espresso tests, set mIdlingResource to busy while downloading recipes list
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(false);
                }
                showProgress(true);
                BakingApiService bakingApiService = BakingApiService.retrofit.create(BakingApiService.class);
                Call<JsonArray> jsonCall = bakingApiService.recipesJson();
                jsonCall.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Recipe>>() {
                        }.getType();
                        mRecipeList = gson.fromJson(response.body().getAsJsonArray(), listType);
                        initUI();
                        showProgress(false);
                        //used for Espresso tests, set mIdlingResource to idle after download is finished
                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        Util.getSnackMessage(findViewById(R.id.main_content), getString(R.string.error_downloading), true).show();
                        Log.e(TAG, t.toString());
                    }
                });
            } else {
                errorView.setVisibility(View.VISIBLE);
            }
        }

        // Get the IdlingResource instance
        getIdlingResource();

        if (isClickFromWidget) {
            setTitle(getString(R.string.recipe_widget_title));
        } else {
            setTitle(getString(R.string.recipe_activity_title));
        }

    }

    /**
     * Init listview (phone) given a list of recipes
     */
    private void initUI() {
        if (mTabletMode) {
            mAdapter.setModels(mRecipeList);
        } else {
            scrollListener.resetState();
            setRecipeList();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.RECIPE_LIST, mRecipeList);
    }

    /**
     * Method that updates adapter with more recipes from recipe list given a 'page' value
     *
     * @param page
     */
    private void loadNextRecipesFromList(int page) {
        mAdapter.setMoreRecipes(mRecipeList.subList(page * MAX_RECIPE_PER_PAGE, MAX_RECIPE_PER_PAGE));
    }

    /**
     * show or hide progress spinner while getting recipes from server
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
        if (isClickFromWidget) {
            Log.d(TAG, "widget selected recipe " + recipe.getId());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            BakingWidgetProvider.updateAppWidget(this, appWidgetManager, mWidgetId, recipe);
            finish();
        } else {
            Intent intent = new Intent(this, RecipeActivity.class);
            intent.putExtra(getString(R.string.recipe_key), recipe);
            startActivity(intent);
        }
    }

    @Override
    public void onRecipeSelected(int position) {
        onRecipeSelected(mRecipeList.get(position));
    }

    // Method to manually check connection status
    private boolean isConnected() {
        return ConnectivityUtil.isConnected(getApplicationContext());
    }
}
