package com.example.project.entities;

import android.graphics.Bitmap;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Activity {
    private Long activityId;
    private Long projectId;
    private String activityName;
    private Boolean isDone;
    private Integer difficulty;
    private Long userId;

    public Activity(Long activityId, Long projectId, String activityName, Long userId, Integer difficulty) {
        this.activityId = activityId;
        this.projectId = projectId;
        this.activityName = activityName;
        this.isDone = false;
        this.userId = userId;
        this.difficulty = difficulty;
    }

    public Activity(Long activityId, String activityName, Long userId, Boolean isDone, Integer difficulty) {
        this.activityId = activityId;
        this.projectId = null;
        this.activityName = activityName;
        this.isDone = isDone;
        this.userId = userId;
        this.difficulty = difficulty;
    }

    public Activity(Long activityId, String activityName, Long userId, Boolean isDone, Integer difficulty, Long projectId) {
        this.activityId = activityId;
        this.projectId = projectId;
        this.activityName = activityName;
        this.isDone = isDone;
        this.userId = userId;
        this.difficulty = difficulty;
    }

    public Activity(Long activityId, String activityName, Integer difficulty, Long userId) {
        this.projectId = null;
        this.activityId = activityId;
        this.isDone = false;
        this.activityName = activityName;
        this.difficulty = difficulty;
        this.userId = userId;
    }

    public Activity() {

    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
}
