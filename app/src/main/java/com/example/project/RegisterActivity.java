package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText mTextUsername;
    EditText mTextFullName;
    EditText mTextPassword;
    EditText mTextEmail;
    EditText mTextCnfPassword;
    Button mButtonRegister;
    TextView mTextViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        mTextUsername = (EditText)findViewById(R.id.edittext_username);
        mTextFullName = (EditText)findViewById(R.id.edittext_fullname);
        mTextEmail = (EditText)findViewById(R.id.edittext_email);
        mTextPassword = (EditText)findViewById(R.id.edittext_password);
        mTextCnfPassword = (EditText)findViewById(R.id.edittext_cnf_password);
        mButtonRegister = (Button)findViewById(R.id.button_register);
        mTextViewLogin = (TextView)findViewById(R.id.textview_login);
        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(LoginIntent);
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mTextUsername.getText().toString().trim();
                String fullName = mTextFullName.getText().toString().trim();
                String email = mTextEmail.getText().toString().trim();
                String pwd = mTextPassword.getText().toString().trim();
                String cnf_pwd = mTextCnfPassword.getText().toString().trim();

                if (!validateEmail(email) | !validateFullName(fullName) | !validateUsername(user) | !validatePassword(pwd, cnf_pwd, user, email, fullName)) {
                    Toast.makeText(RegisterActivity.this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateUsername(String user) {
        if(user.isEmpty()){
            mTextUsername.setError("Field can't be empty");
            return false;
        }
        else if(user.length() < 5) {
            mTextUsername.setError("Please enter at least 5 characters");
            return false;
        }
        else {
            mTextUsername.setError(null);
            return true;
        }
    }

    private boolean validateFullName(String name) {
        if(name.isEmpty()){
            mTextFullName.setError("Field can't be empty");
            return false;
        }
        else if(name.length() < 5) {
            mTextFullName.setError("Please enter at least 5 characters");
            return false;
        }
        else {
            mTextFullName.setError(null);
            return true;
        }
    }


    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            mTextEmail.setError("Field can't be empty");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mTextEmail.setError("Please enter a valid email address");
            return false;
        }
        else {
            mTextEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String pwd, String cnf_pwd, String user, String email, String fullName) {
        if(pwd.isEmpty()) {
            mTextPassword.setError("Field can't be empty");
            return false;
        }
        else if(!pwd.equals(cnf_pwd)){
            mTextCnfPassword.setError("Passwords not matching");
            return false;
            //Toast.makeText(RegisterActivity.this,"Password is not matching",Toast.LENGTH_SHORT).show();
        }
        else if(pwd.equals(cnf_pwd)) {
            long val = db.addUser(user, fullName, pwd, email);
            if(val > 0) {
                Toast.makeText(RegisterActivity.this,"You have registered", Toast.LENGTH_SHORT).show();
                Intent moveToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(moveToLogin);
                return true;
            }
            else {
                Toast.makeText(RegisterActivity.this,"Registration Error", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            mTextPassword.setError(null);
            return true;
        }
    }

}