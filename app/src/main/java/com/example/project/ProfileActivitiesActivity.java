package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.project.entities.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivitiesActivity extends AppCompatActivity {

    String username = null;
    Long userID = null;

    ActivitiesProfileAdapter activitiesAdapter;
    Map<Long, Activity> activityMap = new HashMap<>();

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activities);

        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userID = db.getUserId(username);

        ListView listView = findViewById(R.id.lvActivities);

        List<Activity> activities = db.getActivitiesFromUserID(userID);
        for (Activity a : activities) {
            activityMap.put(a.getActivityId(), a);
        }

        activitiesAdapter = new ActivitiesProfileAdapter(this, activityMap);
        listView.setAdapter(activitiesAdapter);

    }
}