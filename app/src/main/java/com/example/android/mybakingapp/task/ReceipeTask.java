package com.example.android.mybakingapp.task;

import android.os.AsyncTask;

import com.example.android.mybakingapp.model.Receipe;
import com.example.android.mybakingapp.util.JsonReceipeUtil;
import com.example.android.mybakingapp.util.NetworkUtils;

import java.net.URL;
import java.util.List;

public class ReceipeTask extends AsyncTask<String, Void, List<Receipe>> {

    private OnReceipeTaskCompleted mObserver;

    public ReceipeTask(OnReceipeTaskCompleted observer) {
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
    protected List<Receipe> doInBackground(String... params) {
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
    protected void onPostExecute(List<Receipe> receipes) {
        if (mObserver != null) {
            mObserver.onTaskCompleted(receipes);
        }
    }

    public interface OnReceipeTaskCompleted {
        void onTaskCreated();

        void onTaskCompleted(List<Receipe> receipes);
    }
}
