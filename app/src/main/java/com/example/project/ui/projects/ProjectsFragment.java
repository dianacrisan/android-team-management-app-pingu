package com.example.project.ui.projects;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Output;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.project.DatabaseHelper;
import com.example.project.AddProjectActivity;
import com.example.project.EditProjectActivity;
import com.example.project.MainActivity;
import com.example.project.ProjectGridAdapter;
import com.example.project.R;
import com.example.project.entities.Activity;
import com.example.project.entities.Project;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProjectsFragment extends Fragment{

    private ProjectsViewModel projectsViewModel;
    Map<Long, Project> projectMap = new HashMap<>();
    GridView projectsGrid;
    ProjectGridAdapter projectGridAdapter;

    private DatabaseHelper db;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = null;
                        try {
                            date = format.parse(extras.get("projectDeadline").toString());
                            System.out.println(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        db.addProject(extras.get("projectName").toString(), extras.get("projectDeadline").toString());
                        Long projectId = db.findIdLastInsertedProject();
                        Project p = new Project(projectId, extras.get("projectName").toString(), date);

                        updateProjectsMap();
                        Toast.makeText(getContext(), "Project added to grid view!", Toast.LENGTH_SHORT).show();

                        Long currentProjectId = db.findIdLastInsertedProject();
                        db.updateActivitiesWithNullProjectId(currentProjectId);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Project not added!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if(requestCode == 200)
        {
            if(resultCode == RESULT_OK)
            {
                if(data != null)
                {
                    Bundle extras = data.getExtras();
                    if(extras != null)
                    {
                        db.updateProject(Long.parseLong(extras.get("pId").toString()), extras.get("pName").toString(), extras.get("pDeadline").toString());
                        updateProjectsMap();
                        db.updateActivitiesWithNullProjectId(Long.parseLong(extras.get("pId").toString()));
                    }
                }
            }
        }
        if(requestCode == 300)
        {
            if(resultCode == RESULT_OK) {
                if(data != null)
                {
                    try
                    {
                        Uri uri = data.getData();
                        Context applicationContext = MainActivity.getContextOfApplication();
                        OutputStream outputStream = applicationContext.getContentResolver().openOutputStream(uri);
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Project p : projectMap.values()) {
                            stringBuilder.append(p.toString());
                            stringBuilder.append("\n");
                        }
                        outputStream.write(stringBuilder.toString().getBytes());
                        outputStream.close();

                        Toast.makeText(this.getContext(), "File saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                    catch(IOException e)
                    {
                        Toast.makeText(this.getContext(), "Failed to save file!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                Toast.makeText(this.getContext(), "File not saved!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        projectsViewModel = new ViewModelProvider(this).get(ProjectsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_projects, container, false);

        //initializing the database
        db = new DatabaseHelper(getContext());

        final TextView textView = root.findViewById(R.id.tvProjects);
        final Button btnAddProject = root.findViewById(R.id.btnAddProject);
        final Button btnSaveReport = root.findViewById(R.id.btnSaveReport);

        projectsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //CUSTOM ADAPTER EXAMPLE
        projectsGrid = root.findViewById(R.id.gvProjects);

        List<Activity> activities = new ArrayList<>();
        Activity a1 = new Activity(1l, 100l, "activity1", 1l, 52);
        Activity a2 = new Activity(2l, 100l, "activity2", 2l, 37);
        activities.add(a1);
        activities.add(a2);

        List<Project> projects = db.getProjects();
        for (Project p : projects) {
            //setting the projectActivities in the class
            List<Activity> activityList = db.getActivitiesFromProjectId(p.getProjectId());
            p.setActivities(activityList);

            projectMap.put(p.getProjectId(), p);
        }

        projectGridAdapter = new ProjectGridAdapter(getContext(), projectMap);
        projectsGrid.setAdapter(projectGridAdapter);

        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddProjectActivity.class);
                Bundle bundle = new Bundle();
//                bundle.putString("param1", "You are now in the edit page!"); //!!!!
                intent.putExtras(bundle);
                //start a normal activity
                //startActivity(intent);
                //start an activity for getting back a result
                startActivityForResult(intent, 100);
            }
        });

        //edit
        projectsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long itemId) {
                Project p = projectMap.get(itemId);

                Intent intent = new Intent(getContext(), EditProjectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("pId", p.getProjectId().toString());
                bundle.putString("pName", p.getName());
                bundle.putString("pDeadline", p.getDeadline().toString());
                intent.putExtras(bundle);
                //start a normal activity
                //startActivity(intent);
                //start an activity for getting back a result
                startActivityForResult(intent, 200);
            }
        });

        //delete
        projectsGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                db.deleteProject(projectMap.get(id).getProjectId());
                projectGridAdapter.notifyDataSetChanged();
                projectsGrid.invalidateViews();
                return false;
            }
        });

        btnSaveReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndSaveFile();

            }
        });

        return root;
    }

    private void createAndSaveFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "projectsReport.txt");

        startActivityForResult(intent, 300);
    }

//    @Override
//    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("Choose your option");
//        getMenuInflater().inflate(R.menu.context_menu_project, menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.editProject:
//                Toast.makeText(getContext(), "edit", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.deleteProject:
//                Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

    void updateProjectsMap() {
        projectMap.clear();
        List<Project> projects = db.getProjects();
        for (Project p : projects) {
            projectMap.put(p.getProjectId(), p);
        }
        projectGridAdapter.notifyDataSetChanged();
    }

}