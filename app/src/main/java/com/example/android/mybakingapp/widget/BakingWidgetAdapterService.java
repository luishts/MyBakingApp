package com.example.android.mybakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.util.Constants;

import java.util.ArrayList;

/**
 * Implementation of a RemoteViewsService to handle a list of ingredients into a ListView
 */

public class BakingWidgetAdapterService extends RemoteViewsService {

    ArrayList<String> ingredientList;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ingredientList = intent.getStringArrayListExtra(Constants.INGREDIENT_LIST);
        return new IngredientListAdapter(this.getApplicationContext());
    }

    class IngredientListAdapter implements RemoteViewsService.RemoteViewsFactory {

        private Context context;

        IngredientListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (ingredientList != null) {
                return ingredientList.size();
            }
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item_row);
            views.setTextViewText(R.id.txt_ingredient_description, ingredientList.get(i));
            return views;
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
