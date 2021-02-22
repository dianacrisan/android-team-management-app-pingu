package com.example.project;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.project.entities.Project;
import com.example.project.ui.projects.ProjectsFragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class ProjectGridAdapter extends BaseAdapter {

    private static final String TAG = ProjectsFragment.class.getSimpleName();

    Map<Long, Project> projectList;

    LayoutInflater layoutInflater;
    Context context;

    public ProjectGridAdapter(Context context, Map<Long, Project> projectList) {
        this.projectList = projectList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return projectList.size();
    }

    @Override
    public Object getItem(int position) {
        Object[] objects = projectList.keySet().toArray();
        return projectList.get(objects[position]);
    }

    @Override
    public long getItemId(int position) {
        Object[] objects = projectList.keySet().toArray();
        return projectList.get(objects[position]).getProjectId();
    }

    private static class ProjectViewHolder
    {
        public TextView name, deadline;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProjectViewHolder holder;
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.gv_project_item, parent, false);
            holder = new ProjectViewHolder();
            holder.name = convertView.findViewById(R.id.tvName);
            holder.deadline = convertView.findViewById(R.id.tvDeadline);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ProjectViewHolder) convertView.getTag();
        }
        Project project = projectList.get(getItemId(position));

        holder.name.setText(project.getName());
//        holder.deadline.setText(project.getDeadline().toString());

        SimpleDateFormat timeStampFormat = new SimpleDateFormat("dd-MM-yyyy");
        String DateStr = timeStampFormat.format(project.getDeadline());

        holder.deadline.setText(DateStr);
        return convertView;
    }

}
