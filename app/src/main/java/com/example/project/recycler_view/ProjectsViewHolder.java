package com.example.project.recycler_view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;

public class ProjectsViewHolder extends RecyclerView.ViewHolder {

    TextView projectName;
    TextView projectDeadline;
    TextView activities;
    View view;

    ProjectsViewHolder(@NonNull View itemView) {
        super(itemView);
        projectName = (TextView)itemView.findViewById(R.id.projectName);
        projectDeadline = (TextView)itemView.findViewById(R.id.projectDeadline);
        activities = (TextView)itemView.findViewById(R.id.activities);
        view = itemView;
    }
}
