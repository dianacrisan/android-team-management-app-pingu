package com.example.project.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.project.DatabaseHelper;
import com.example.project.ProfileActivitiesActivity;
import com.example.project.R;
import com.example.project.RegisterActivity;
import com.example.project.entities.User;
import com.example.project.ui.projects.ProjectsViewModel;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private DatabaseHelper db;

    EditText etUsername;
    EditText etFullName;
    EditText etEmail;

    String newUsername = null;
    String newFullName = null;
    String newEmail = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        db = new DatabaseHelper(getContext());

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navFullName = (TextView) headerView.findViewById(R.id.navFullName);
        String fullName = navFullName.getText().toString();
        TextView email = (TextView) headerView.findViewById(R.id.navEmail);
        String mail = email.getText().toString();
        String username = db.getUsername(mail);

        TextView tvFullName = root.findViewById(R.id.tvFullName);
        tvFullName.setText(fullName);

        TextView tvUsername = root.findViewById(R.id.tvUsername);
        tvUsername.setText(username);

        etUsername = root.findViewById(R.id.etUsername);
        etUsername.setText(username);

        etFullName = root.findViewById(R.id.etFullname);
        etFullName.setText(fullName);

        etEmail = root.findViewById(R.id.etEmail);
        etEmail.setText(mail);

        User loggedInUser = db.getUserInfo(username);

        TextView valActivities = root.findViewById(R.id.valActivities);
        Integer noUserActivities = db.getNumberUserActivities(db.getUserId(loggedInUser.getUsername()));
        valActivities.setText(noUserActivities.toString());

        TextView valProjects = root.findViewById(R.id.valProjects);
        Integer totalProjects = db.getTotalNumberProjects();
        valProjects.setText(totalProjects.toString());

        Button btnUpdate = root.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUsername = etUsername.getText().toString();
                newFullName = etFullName.getText().toString();
                newEmail = etEmail.getText().toString();

                if (!validateEmail(newEmail) | !validateFullName(newFullName) | !validateUsername(newUsername)) {
                    Toast.makeText(getContext(), "Invalid input info!", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.updateUser(newUsername, newFullName, newEmail);
                    tvFullName.setText(newFullName);
                    tvUsername.setText(newUsername);

                    navFullName.setText(newFullName);
                    email.setText(newEmail);
                }

            }
        });

        RelativeLayout cardUserActivities = root.findViewById(R.id.cardUserActivities);
        cardUserActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileFragment.this.getContext(), ProfileActivitiesActivity.class);
                intent.putExtra("username", loggedInUser.getUsername());
                startActivity(intent);
            }
        });

        return root;
    }

    private boolean validateUsername(String user) {
        if(user.isEmpty()){
            etUsername.setError("Field can't be empty");
            return false;
        }
        else if(user.length() < 5) {
            etUsername.setError("Please enter at least 5 characters");
            return false;
        }
        else {
            etUsername.setError(null);
            return true;
        }
    }

    private boolean validateFullName(String name) {
        if(name.isEmpty()){
            etFullName.setError("Field can't be empty");
            return false;
        }
        else if(name.length() < 5) {
            etFullName.setError("Please enter at least 5 characters");
            return false;
        }
        else {
            etFullName.setError(null);
            return true;
        }
    }


    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            etEmail.setError("Field can't be empty");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            return false;
        }
        else {
            etEmail.setError(null);
            return true;
        }
    }
}