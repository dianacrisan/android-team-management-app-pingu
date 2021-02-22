package com.example.project.ui.resources;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.R;
import com.example.project.recycler_view.DownloadAsync;
import com.example.project.recycler_view.ImageGalleryAdapter;
import com.example.project.recycler_view.ProjectsData;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResourcesFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private ResourcesViewModel resourcesViewModel;
    ImageGalleryAdapter adapter;
    RecyclerView recyclerView;
    ImageGalleryAdapter.ClickListener listener;
    List<ProjectsData> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        resourcesViewModel =
                new ViewModelProvider(this).get(ResourcesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_resources, container, false);

        final TextView textView = root.findViewById(R.id.tvResourcesFragment);
        resourcesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        List<ProjectsData> list = new ArrayList<>();

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        listener = new ImageGalleryAdapter.ClickListener() {
            @Override
            public void click(int index) {

            }
        };

        adapter = new ImageGalleryAdapter(list, ResourcesFragment.this.getContext(), listener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnDownload = root.findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadAsync downloadAsync = new DownloadAsync() {
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray jaProjects = jsonObject.getJSONArray("projects");
                            for (int index = 0; index < jaProjects.length(); index++) {
                                JSONObject joProject = jaProjects.getJSONObject(index);
                                JSONArray jaActivities = joProject.getJSONArray("activities");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int index2 = 0; index2 < jaActivities.length(); index2++) {
                                    JSONObject joActivity = jaActivities.getJSONObject(index2);
                                    stringBuilder.append(joActivity.getString("activityName"));
                                    stringBuilder.append(", ");
                                }
                                list.add(new ProjectsData(joProject.getString("projectName"), joProject.getString("deadline"),
                                        stringBuilder.toString()));
                                //Toast.makeText(getContext(), list.get(0).getProjectName(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                downloadAsync.execute("https://api.mocki.io/v1/b20890d6");

                adapter.notifyDataSetChanged();
            }
        });

        return root;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


}