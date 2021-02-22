package com.example.project;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.entities.Activity;

import java.util.HashMap;
import java.util.Map;

public class ActivitiesProfileAdapter extends BaseAdapter {

    Map<Long, Activity> activityList;
    LayoutInflater layoutInflater;
    Context context;

    private DatabaseHelper db;

    private HashMap<Long, Boolean> itemState = new HashMap<>();

    public ActivitiesProfileAdapter(Context context, Map<Long, Activity> activityList) {
        this.activityList = activityList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return activityList.size();
    }

    @Override
    public Object getItem(int position) {
        //return countryList.get(position); //If we would have had List and not Map
        Object[] objects = activityList.keySet().toArray();
        return activityList.get(objects[position]);
    }

    @Override
    public long getItemId(int position) {
        Object[] objects = activityList.keySet().toArray();
        return activityList.get(objects[position]).getActivityId();
    }

    private static class ActivityViewHolder
    {
        public TextView name, projectName;
        public CheckBox finished;
        public SeekBar difficulty;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        db = new DatabaseHelper(context);

        for(Long key: activityList.keySet())
        {
            Activity activity = activityList.get(key);
            itemState.put(activity.getActivityId(), activity.getDone());
        }

        ActivityViewHolder holder;
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.lv_activities_item, parent, false); //attach to root false
            holder = new ActivityViewHolder();
            holder.projectName = convertView.findViewById(R.id.tvUsername); //we replaced the username from adapter with project name
            holder.name = convertView.findViewById(R.id.tvName); //here name refers to project name
            holder.finished = convertView.findViewById(R.id.cbIsDone);
            holder.difficulty = convertView.findViewById(R.id.seekBar);
            convertView.setTag(holder); //metadata associated to view
        }
        else
        {
            holder = (ActivityViewHolder) convertView.getTag();
        }

        Activity activity = activityList.get(getItemId(position));

        holder.finished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activity.setDone(isChecked);
                Integer check = 0;
                if(isChecked) check = 1;
                db.updateFinishedActivity(activity.getActivityId(), check);
            }
        });

        holder.difficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                activity.setDifficulty(progress);
                db.updateDifficultyActivity(activity.getActivityId(), progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        holder.name.setText(activity.getActivityName());

        String pName = db.getProjectNameFromId(activity.getProjectId());
        holder.projectName.setText(pName);

        Integer activityDoneStatus = db.getIsDone(activity.getActivityId());
        boolean ok = false;
        if(activityDoneStatus == 1) ok = true;
        holder.finished.setChecked(ok); //itemState.get(activity.getActivityId())

        Integer dif = db.getDifficulty(activity.getActivityId());
        holder.difficulty.setProgress(dif); //activity.getDifficulty()

        return convertView;
    }
}

