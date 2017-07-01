package com.example.android.mybakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Ingredient;
import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.ui.MainActivity;
import com.example.android.mybakingapp.ui.RecipeActivity;
import com.example.android.mybakingapp.util.Constants;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, Recipe recipe) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent intent;

        if (recipe != null) {
            //if there is a recipe set, send user to RecipeActivity showing all details of recipe set
            intent = new Intent(context, RecipeActivity.class);
            intent.putExtra(context.getString(R.string.recipe_key), recipe);
            //setup listview 'adapter'
            Intent adapterIntent = new Intent(context, BakingWidgetAdapterService.class);
            adapterIntent.setData(Uri.fromParts("content", String.valueOf(appWidgetId), null));
            ArrayList ingredientArray = new ArrayList<>();
            String ingredientText;
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredientText = ingredient.getQuantity() + " " + ingredient.getMeasure() + " of " + ingredient.getIngredient();
                ingredientArray.add(ingredientText);
            }
            adapterIntent.putStringArrayListExtra(Constants.INGREDIENT_LIST, ingredientArray);
            views.setRemoteAdapter(R.id.widget_listView, adapterIntent);
            ////setup listview 'adapter'
            views.setViewVisibility(R.id.widget_listView, View.VISIBLE);
            views.setViewVisibility(R.id.empty_view, View.GONE);
            views.setTextViewText(R.id.recipe_title, recipe.getName());
        } else {
            //if there is no recipe set, send user to MainActivity to pick one
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setViewVisibility(R.id.widget_listView, View.GONE);
            views.setViewVisibility(R.id.empty_view, View.VISIBLE);
        }

        final Bundle bundle = new Bundle();
        bundle.putBoolean(context.getString(R.string.widget_click), Boolean.TRUE);
        bundle.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listView);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, null);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            updateAppWidget(context, AppWidgetManager.getInstance(context),
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0), null);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }
}

