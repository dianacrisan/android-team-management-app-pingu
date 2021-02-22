package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.entities.Activity;
import com.example.project.entities.Project;
import com.example.project.entities.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // a static variable to get a reference of our application context
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseHelper db;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //context for saving file getContentResolver()
        contextOfApplication = getApplicationContext();

        //initializing the database
        db = new DatabaseHelper(this);

        //getting the username from login page or settings page
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        username = extras.getString("user");

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //messages button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Messaging your colleagues", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");

                List<String> emails = new ArrayList<>();
                List<User> users = db.getUsers();
                for (User u: users) {
                    emails.add(u.getEmail());
                }

                String[] emailVector = new String[emails.size()];
                for(int j = 0; j < emails.size(); j++) {
                    emailVector[j] = emails.get(j);
                }

                List<Project> projects = db.getProjects();
                StringBuilder stringBuilder = new StringBuilder();
                for (Project p: projects) {
                    List<Activity> activities = db.getActivitiesFromProjectId(p.getProjectId());
                    p.setActivities(activities);
                    stringBuilder.append(p.toString());
                }

                i.putExtra(Intent.EXTRA_EMAIL  , emailVector);
                i.putExtra(Intent.EXTRA_SUBJECT, "Android Project Message");
                i.putExtra(Intent.EXTRA_TEXT   , stringBuilder.toString());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //side menu buttons
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_resources, R.id.nav_projects, R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //modifying the header of the navigation drawer
        View headerView = navigationView.getHeaderView(0);
        TextView navFullName = (TextView) headerView.findViewById(R.id.navFullName);
        User loggedInUser = db.getUserInfo(username);
        String loggedInUserFullName = loggedInUser.getFullName();
        navFullName.setText(loggedInUserFullName);
        TextView navEmail = (TextView) headerView.findViewById(R.id.navEmail);
        //getting the email based on username from database
        String loggedInUserEmail = loggedInUser.getEmail();
        navEmail.setText(loggedInUserEmail);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout(View view) {
        Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(logoutIntent);
    }

    public void goToSettings(MenuItem item) {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        settingsIntent.putExtras(bundle);
        //start a normal activity
        //startActivity(settingsIntent);
        //start an activity for getting back a result
        startActivityForResult(settingsIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100)
        {
            if(resultCode == RESULT_OK)
            {
                if(data != null)
                {
                    Bundle extras = data.getExtras();
                    if(extras != null)
                    {
                        username = extras.get("username").toString();
                    }
                    else
                    {
                        Toast.makeText(this, "Username not returned from settings!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}