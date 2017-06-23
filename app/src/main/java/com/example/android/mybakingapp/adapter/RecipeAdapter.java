package com.example.android.mybakingapp.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.RecipeBaseComponent;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ltorres on 8/22/2016.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private WeakReference<Activity> mContext;
    private List<RecipeBaseComponent> mRecipeComponents;

    public RecipeAdapter(Activity mContext, List<RecipeBaseComponent> recipeBaseComponents) {
        this.mContext = new WeakReference<>(mContext);
        setModels(recipeBaseComponents);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgComponentImg;
        TextView txtComponentName;
        CardView cardViewMain;

        public ViewHolder(View itemView) {
            super(itemView);
            cardViewMain = (CardView) itemView.findViewById(R.id.card_view);
            imgComponentImg = (ImageView) cardViewMain.findViewById(R.id.recipe_component_img);
            txtComponentName = (TextView) cardViewMain.findViewById(R.id.recipe_component_name);
            cardViewMain.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.card_view:
                    Toast.makeText(mContext.get(), "clicou no + " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_component_row, parent, false);
        ViewHolder vhItem = new ViewHolder(v);
        return vhItem;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        RecipeBaseComponent recipeBaseComponent = mRecipeComponents.get(position);
        //viewHolder.imgComponentImg.setImageResource(recipeBaseComponent.getComponentImage());
        viewHolder.txtComponentName.setText(recipeBaseComponent.getComponentName());
    }

    @Override
    public int getItemCount() {
        if (mRecipeComponents != null) {
            return mRecipeComponents.size();
        } else {
            return 0;
        }
    }

    /**
     * Method that given a list of calls, finds all different Dates and creates a header for each one. Each header represents
     * a data with a list of calls.
     *
     * @param recipeBaseComponents
     */
    public void setModels(List<RecipeBaseComponent> recipeBaseComponents) {
        mRecipeComponents = recipeBaseComponents;
        //if (mSales != null && mSales.size() > 0) {
        //    Collections.sort(mSales, Collections.<Sales>reverseOrder());
        //}
        notifyDataSetChanged();
    }
}