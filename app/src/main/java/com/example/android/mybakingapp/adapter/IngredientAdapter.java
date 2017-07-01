package com.example.android.mybakingapp.adapter;

/**
 * Adapter from Ingredients from a given Recipe. Displays quantity, measure and name.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Ingredient;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ltorres on 3/28/2016.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Ingredient> mIngredients;
    private WeakReference<Activity> mContext;

    public IngredientAdapter(Activity activity, List<Ingredient> ingredients) {
        this.mContext = new WeakReference<>(activity);
        setModels(ingredients);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_quantity)
        TextView txtQuantity;

        @BindView(R.id.txt_measure)
        TextView txtMeasure;

        @BindView(R.id.txt_ingredient)
        TextView txtIngredient;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item_row, parent, false);
        ViewHolder vhItem = new ViewHolder(v);
        return vhItem;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ingredient ingredient = mIngredients.get(position);
        if (ingredient != null) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ingredient.getQuantity()).append(" ")
                    .append(ingredient.getMeasure()).append(" ")
                    .append(ingredient.getIngredient());
            holder.txtQuantity.setText(String.valueOf(ingredient.getQuantity()));
            holder.txtMeasure.setText(ingredient.getMeasure());
            holder.txtIngredient.setText(ingredient.getIngredient());
        }
    }

    @Override
    public int getItemCount() {
        if (mIngredients != null) {
            return mIngredients.size();
        }
        return 0;
    }

    public void setModels(List<Ingredient> ingredients) {
        mIngredients = ingredients;
        notifyDataSetChanged();
    }
}
