package com.example.project.entities;

import com.example.project.DatabaseHelper;
import com.example.project.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Project {

    //id database pk autoincrement!
    private Long projectId;
    private String projectName;
    private Date projectDeadline;
    private List<Activity> projectActivities;

    public Project(Long projectId, String name, Date deadline, List<Activity> activities) {
        this.projectId = projectId;
        this.projectName = name;
        this.projectDeadline = deadline;
        this.setActivities(activities);
    }

    public Project(Long projectId, String name, Date deadline) {
        this.projectId = projectId;
        this.projectName = name;
        this.projectDeadline = deadline;
        this.projectActivities = null;
    }

    public Project(String name, Date deadline) { //for db, ID autoincrement
        this.projectId = null;
        this.projectName = name;
        this.projectDeadline = deadline;
        this.projectActivities = null;
    }

    public Project() {

    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return projectName;
    }

    public void setName(String name) {
        this.projectName = name;
    }

    public Date getDeadline() {
        return this.projectDeadline;
    }

    public void setDeadline(Date deadline) {
        this.projectDeadline = deadline;
    }

    public List<Activity> getActivities() {
        return projectActivities;
    }

    public void setActivities(List<Activity> activities)
    {
        if(activities == null)
            this.projectActivities = null;
        else {
            this.projectActivities = new ArrayList<>();
            for (Activity a : activities) {
                this.projectActivities.add(a);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Project name is ");
        stringBuilder.append(projectName);
        stringBuilder.append(". \n");
        stringBuilder.append("Project deadline is ");
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = timeStampFormat.format(projectDeadline);
        stringBuilder.append(date);
        stringBuilder.append(". \n");
        if(projectActivities != null) {
            stringBuilder.append("Project activities are: \n");
            for (Activity a : projectActivities) {
                stringBuilder.append(a.getActivityName());
                stringBuilder.append(" done by user with ID ");
                stringBuilder.append(a.getUserId());
                stringBuilder.append(", ");
                stringBuilder.append("\n");
            }
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
