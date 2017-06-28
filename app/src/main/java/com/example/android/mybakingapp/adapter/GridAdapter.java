package com.example.android.mybakingapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Recipe;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Luis on 27/06/2017.
 */

public class GridAdapter extends BaseAdapter {

    private List<Recipe> mRecipeList;
    private WeakReference<Activity> mContext;

    public GridAdapter(Activity activity, List<Recipe> recipeList) {
        this.mContext = new WeakReference<>(activity);
        mRecipeList = recipeList;
    }

    @Override
    public int getCount() {
        if (mRecipeList != null) {
            return mRecipeList.size();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Recipe recipe = mRecipeList.get(position);
        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
            listViewHolder.txtRecipeName = (TextView) convertView.findViewById(R.id.txtRecipeName);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }

        listViewHolder.txtRecipeName.setText(recipe.getName());
        return convertView;
    }

    static class ViewHolder {
        TextView txtRecipeName;
    }

    @Override
    public Object getItem(int i) {
        if (mRecipeList != null) {
            return mRecipeList.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
