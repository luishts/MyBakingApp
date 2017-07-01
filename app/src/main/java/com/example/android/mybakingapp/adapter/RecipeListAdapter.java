package com.example.android.mybakingapp.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Recipe;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter for all Recipes from server. Displays image, name and servings
 */
public class RecipeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int RECIPE = 1, LOADING = 2;

    private RecipeClickListener mRecipeClickListener;
    private WeakReference<Activity> mContext;
    private List<Recipe> mRecipes;

    public RecipeListAdapter(Activity mContext, List<Recipe> recipes) {
        this.mContext = new WeakReference<>(mContext);
        setModels(recipes);
    }

    public class ViewHolderRecipe extends RecyclerView.ViewHolder {

        @BindView(R.id.imgRecipeImg)
        ImageView imgRecipeImg;
        @BindView(R.id.txtRecipeName)
        TextView txtRecipeName;
        @BindView(R.id.txtServings)
        TextView txtServings;
        @BindView(R.id.card_view)
        CardView cardViewMain;

        public ViewHolderRecipe(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.card_view)
        public void onClickRecipe() {
            if (mRecipeClickListener != null) {
                mRecipeClickListener.onRecipeSelected(getAdapterPosition());
            }
        }
    }

    private class ViewHolderLoading extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public ViewHolderLoading(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder vhItem = null;

        switch (viewType) {
            case RECIPE:
                View v1 = inflater.inflate(R.layout.recipe_item_row, parent, false);
                vhItem = new ViewHolderRecipe(v1);
                break;
            case LOADING:
                View v3 = inflater.inflate(R.layout.progress_item, parent, false);
                vhItem = new ViewHolderLoading(v3);
                break;
        }

        return vhItem;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case RECIPE:
                configureViewHolderRecipe((ViewHolderRecipe) viewHolder, position);
                break;
            case LOADING:
                ((ViewHolderLoading) viewHolder).progressBar.setIndeterminate(true);
                break;
        }
    }


    /**
     * Method that fills a row with all information of a Recipe
     *
     * @param holder
     * @param position
     */
    private void configureViewHolderRecipe(ViewHolderRecipe holder, int position) {

        Recipe recipe = mRecipes.get(position);
        if (null != recipe) {
            holder.txtRecipeName.setText(recipe.getName());
            holder.txtServings.setText(mContext.get().getString(R.string.serving) + " " + String.valueOf(recipe.getServings()));
            if (!TextUtils.isEmpty(recipe.getImage())) {
                Picasso.with(mContext.get())
                        .load(recipe.getImage())
                        .placeholder(R.drawable.ic_local_dining_black_48dp)
                        .error(R.drawable.ic_local_dining_black_48dp)
                        .into(holder.imgRecipeImg);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mRecipes.get(position) instanceof Recipe) {
            return RECIPE;
        } else {
            return LOADING;
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipes != null) {
            return mRecipes.size();
        } else {
            return 0;
        }
    }

    public int getRecipesCount() {
        int count = 0;
        for (Recipe recipe : mRecipes) {
            if (recipe != null) {
                count++;
            }
        }
        return count;
    }

    public void setModels(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    /**
     * Method that add to current list of recipes, more recipes because user is scrolling the list
     *
     * @param newRecipes
     */
    public void setMoreRecipes(List<Recipe> newRecipes) {
        removeLoadingItem();
        if (newRecipes != null && newRecipes.size() > 0) {
            newRecipes.addAll(mRecipes);
            setModels(newRecipes);
        }
    }

    /**
     * Method that add a spinner while loading new items from list
     */
    public void addLoadingItem() {
        mRecipes.add(null);
        notifyItemInserted(mRecipes.size());
    }

    /**
     * Method that remove the spinner after loading new items from the list is finished
     */
    private void removeLoadingItem() {
        mRecipes.remove(mRecipes.size() - 1);
        notifyItemRemoved(mRecipes.size());
    }

    public interface RecipeClickListener {
        void onRecipeSelected(int position);
    }

    public void setRecipeClickListener(RecipeClickListener mRecipeClickListener) {
        this.mRecipeClickListener = mRecipeClickListener;
    }
}