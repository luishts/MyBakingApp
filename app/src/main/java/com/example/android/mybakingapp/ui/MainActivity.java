package com.example.android.mybakingapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Receipe;
import com.example.android.mybakingapp.task.ReceipeTask;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ReceipeTask.OnReceipeTaskCompleted {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ReceipeTask(MainActivity.this).execute();
    }

    @Override
    public void onTaskCreated() {

    }

    @Override
    public void onTaskCompleted(List<Receipe> receipes) {

    }
}
