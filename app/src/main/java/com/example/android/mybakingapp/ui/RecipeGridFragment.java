package com.example.android.mybakingapp.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.adapter.GridAdapter;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.task.RecipeTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by ltorres on 8/22/2016.
 */
public class RecipeGridFragment extends Fragment implements RecipeTask.OnReceipeTaskCompleted {

    private String TAG = RecipeGridFragment.class.getName();

    private Unbinder unbinder;

    private ProgressDialog mProgressDialog;

    @Nullable
    @BindView(R.id.gridview)
    public GridView mRecipeGridView;

    private GridAdapter mAdapter;

    private List<Recipe> mRecipes;
    private RecipeGridCallback mRecipeGridCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new RecipeTask(this).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recipe_grid_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        mRecipeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mRecipeGridCallback != null) {
                    mRecipeGridCallback.onRecipeGridSelected(mRecipes.get(position));
                }
            }
        });


        getActivity().setTitle(getText(R.string.recipe_fragment_title));

        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
        new RecipeTask(RecipeGridFragment.this).execute();
    }

    @Override
    public void onTaskCreated() {
        showProgress(true);
    }

    @Override
    public void onTaskCompleted(List<Recipe> receipes) {
        mRecipes = receipes;
        mAdapter = new GridAdapter(getActivity(), mRecipes);
        mRecipeGridView.setAdapter(mAdapter);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        showProgress(false);
    }

    public interface RecipeGridCallback {
        void onRecipeGridSelected(Recipe recipe);
    }

    public void setRecipeGridCallback(RecipeGridCallback mRecipeGridCallback) {
        this.mRecipeGridCallback = mRecipeGridCallback;
    }
}
