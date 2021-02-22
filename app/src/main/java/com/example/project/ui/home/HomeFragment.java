package com.example.project.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.project.DatabaseHelper;
import com.example.project.MapsActivity;
import com.example.project.R;
import com.example.project.entities.Project;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private DatabaseHelper db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = new DatabaseHelper(getContext());

        final TextView textView = root.findViewById(R.id.tvHome);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        PieChartView pieChartView = root.findViewById(R.id.chart);
        List<SliceValue> pieData = new ArrayList<>();

        List<Project> projects = db.getProjects();

        int[] noProjects = new int[12];
        for(int i = 0; i < 12; i++)
            noProjects[i] = 0;

        for (Project p: projects) {
            noProjects[p.getDeadline().getMonth()]++;
        }

        int sum = 0;
        for(int i = 0; i < 12; i++)
            sum += noProjects[i];

        int[] colors = new int[] {Color.BLUE, Color.YELLOW, Color.GRAY, Color.MAGENTA, Color.GREEN,
                Color.RED, Color.BLUE, Color.YELLOW, Color.GRAY, Color.MAGENTA, Color.GREEN, Color.RED};
        String[] months = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


        for(int j = 0; j < 12; j++) {
            if(noProjects[j] != 0)
                pieData.add(new SliceValue((float)noProjects[j] / sum * 100, colors[j]).setLabel(months[j]));
        }

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Projects finished / months").setCenterText1FontSize(14).setCenterText1Color(Color.rgb(26, 188, 156));
        pieChartView.setPieChartData(pieChartData);

        Button btnMaps = root.findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsIntent = new Intent(getActivity(), MapsActivity.class);
                startActivity(mapsIntent);
            }
        });

        return root;
    }
}