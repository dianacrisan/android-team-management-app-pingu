package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.project.entities.Activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProjectActivity extends AppCompatActivity {
    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText editTextProjectDate;
    EditText editTextProjectName;

    String projectName = null;
    String projectDeadline = null;
    String activityName = null;
    String userId = null;
    Long ID = null;
    Integer difficulty = 0;

    ListView lvActivities;

    Button btnAddActivity;
    EditText etActivityName;
    EditText etUserId;
    CheckBox cbActivity;
    SeekBar sbDifficulty;

    Map<Long, Activity> activityMap = new HashMap<>();
    ActivitiesAdapter activitiesAdapter;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        //initialize the database
        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Button btnDate = findViewById(R.id.btnChooseDeadline);
        editTextProjectDate = findViewById(R.id.editProjectDeadline);
        editTextProjectName = findViewById(R.id.editProjectName);


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddProjectActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                editTextProjectDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        lvActivities = findViewById(R.id.lvActivities);
        activitiesAdapter = new ActivitiesAdapter(this, activityMap);
        lvActivities.setAdapter(activitiesAdapter);

        btnAddActivity = findViewById(R.id.btnAddActivity);
        etActivityName = findViewById(R.id.editActivityName);
        etUserId = findViewById(R.id.editUserID);
        cbActivity = findViewById(R.id.cbIsDone);
        sbDifficulty = findViewById(R.id.seekBar);
        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityName = etActivityName.getText().toString();
                userId = etUserId.getText().toString();
                if(userId != null & !userId.equals(""))
                    ID = Long.parseLong(userId);
                difficulty = sbDifficulty.getProgress();

                //validate added activity info
                if (!validateActivityName(activityName) | !validateUserId(userId)) {
                    Toast.makeText(AddProjectActivity.this, "Invalid activity!", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.addActivity(activityName, 0, ID, difficulty); //initially, the activity is not done so cb only in adapter

                    //updateActivitiesMap();
                    Long activityId = db.findIdLastInsertedActivity();
                    Activity a = new Activity(activityId, activityName, difficulty, ID);
                    activityMap.put(a.getActivityId(), a);
                    activitiesAdapter.notifyDataSetChanged();

                    //clearing fields after adding activity
                    etActivityName.setText("");
                    etUserId.setText("");
                    sbDifficulty.setProgress(0);
                }
            }
        });
    }

    public void returnToProjectsGrid(View view) {
        Intent intent = new Intent();
        projectName = editTextProjectName.getText().toString();
        projectDeadline = editTextProjectDate.getText().toString();

        //validate project info
        if (!validateProjectName(projectName) | !validateProjectDeadline(projectDeadline)) {
            Toast.makeText(AddProjectActivity.this, "Invalid project info!", Toast.LENGTH_SHORT).show();
        }
        else {
            intent.putExtra("projectName", projectName);
            intent.putExtra("projectDeadline", projectDeadline);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    public void cancel(View view) {
        db.deleteActivitiesWithNullProjectId();
        finish();
    }

    void updateActivitiesMap() {
        activityMap.clear();
        List<Activity> activities = db.getActivities();
        for (Activity a : activities) {
            activityMap.put(a.getActivityId(), a);
        }
        activitiesAdapter.notifyDataSetChanged();
    }

    private boolean validateActivityName(String activityName) {
        if(activityName.isEmpty()){
            etActivityName.setError("Field can't be empty");
            return false;
        }
        else if(activityName.length() < 3) {
            etActivityName.setError("Please enter at least 3 characters");
            return false;
        }
        else {
            etActivityName.setError(null);
            return true;
        }
    }

    private boolean validateUserId(String userId) {
        if (userId.isEmpty() | userId.equals("")) {
            etUserId.setError("Field can't be empty");
            return false;
        }
        else if (!userId.isEmpty() & !userId.equals("")) {
            //check database for the userId
            Long ID = Long.parseLong(userId);
            boolean check = db.checkIfUserExists(ID);
            if (!check) {
                etUserId.setError("ID not found in database.");
                return false;
            }
            else {
                etUserId.setError(null);
                return true;
            }
        }
        else {
            etUserId.setError(null);
            return true;
        }
    }

    private boolean validateProjectName(String projectName) {
        if(projectName.isEmpty()){
            editTextProjectName.setError("Field can't be empty");
            return false;
        }
        else if(projectName.length() < 3) {
            editTextProjectName.setError("Please enter at least 3 characters");
            return false;
        }
        else {
            etActivityName.setError(null);
            return true;
        }
    }

    private boolean validateProjectDeadline(String projectDeadline) {
        if(projectDeadline.isEmpty()) {
            editTextProjectDate.setError("Field can't be empty");
            return false;
        }
        else if(!projectDeadline.isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Calendar calendar = Calendar.getInstance();
            String currentDate = format.format(calendar.getTime());
            Date deadline = null;
            Date current = null;
            try {
                deadline = format.parse(projectDeadline);
                current = format.parse(currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(deadline.before(current)) {
                editTextProjectDate.setError("Deadline must be in the future");
                return false;
            }
            else {
                editTextProjectDate.setError(null);
                return true;
            }
        }
        else {
            editTextProjectDate.setError(null);
            return true;
        }
    }

}

