package com.example.android.mybakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Luis on 29/06/2017.
 */

public class BakingWidgetAdapterService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientListAdapter(this.getApplicationContext());
    }

    class IngredientListAdapter implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private String[] ingredientList;

        IngredientListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> stringSet = new HashSet<>();
            preferences.getStringSet(getString(R.string.widget_ingredients), stringSet);
            ingredientList = stringSet.toArray(new String[0]);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (ingredientList != null) {
                return ingredientList.length;
            }
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_item_row);
            views.setTextViewText(R.id.txt_quantity, ingredientList[i]);
            Intent app = new Intent();
            views.setOnClickFillInIntent(R.id.ingredient_item, app);
            return null;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
