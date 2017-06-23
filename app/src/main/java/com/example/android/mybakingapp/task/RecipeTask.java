package com.example.android.mybakingapp.task;

import android.os.AsyncTask;

import com.example.android.mybakingapp.model.Recipe;
import com.example.android.mybakingapp.util.JsonReceipeUtil;
import com.example.android.mybakingapp.util.NetworkUtils;

import java.net.URL;
import java.util.List;

public class RecipeTask extends AsyncTask<String, Void, List<Recipe>> {

    private OnReceipeTaskCompleted mObserver;

    public RecipeTask(OnReceipeTaskCompleted observer) {
        this.mObserver = observer;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mObserver != null) {
            mObserver.onTaskCreated();
        }
    }

    @Override
    protected List<Recipe> doInBackground(String... params) {
        URL receipeUrl = NetworkUtils.buildReceipeUrl();
        try {
            String jsonReceipeResponse = NetworkUtils.getResponseFromHttpUrl(receipeUrl);
            if (!"".equalsIgnoreCase(jsonReceipeResponse)) {
                return JsonReceipeUtil.getReceipeFromJson(jsonReceipeResponse);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Recipe> receipes) {
        if (mObserver != null) {
            mObserver.onTaskCompleted(receipes);
        }
    }

    public interface OnReceipeTaskCompleted {
        void onTaskCreated();

        void onTaskCompleted(List<Recipe> receipes);
    }
}
