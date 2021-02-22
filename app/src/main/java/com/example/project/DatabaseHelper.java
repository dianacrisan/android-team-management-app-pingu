package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.DatePicker;

import com.example.project.entities.Activity;
import com.example.project.entities.Project;
import com.example.project.entities.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.sql.Types.NULL;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="androidProject.db";

    public static final String TABLE_USERS = "users";
    public static final String COL_USERS_ID = "ID";
    public static final String COL_USERS_USERNAME = "username";
    public static final String COL_USERS_FULLNAME = "fullName";
    public static final String COL_USERS_PASSWORD = "password";
    public static final String COL_USERS_EMAIL = "email";

    public static final String TABLE_PROJECTS = "projects";
    public static final String COL_PROJECTS_ID = "ID";
    public static final String COL_PROJECTS_NAME = "projectName";
    public static final String COL_PROJECTS_DEADLINE = "deadline";

    public static final String TABLE_ACTIVITIES = "activities";
    public static final String COL_ACTIVITIES_ID = "ID";
    public static final String COL_ACTIVITIES_NAME = "activityName";
    public static final String COL_ACTIVITIES_IS_DONE = "isDone";
    public static final String COL_ACTIVITIES_USER_ID = "userId";
    public static final String COL_ACTIVITIES_DIFFICULTY = "difficulty";
    public static final String COL_ACTIVITIES_PROJECT_ID = "projectId";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE users (ID INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, fullName TEXT, password TEXT, email TEXT UNIQUE)");
        sqLiteDatabase.execSQL("CREATE TABLE projects (ID INTEGER PRIMARY KEY AUTOINCREMENT, projectName TEXT, deadline TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE activities (ID INTEGER PRIMARY KEY AUTOINCREMENT, activityName TEXT, isDone INTEGER CHECK (isDone in (0,1)), userId INTEGER, difficulty INTEGER, projectId INTEGER, FOREIGN KEY (projectId) REFERENCES projects (ID), FOREIGN KEY (userId) REFERENCES users (ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        onCreate(sqLiteDatabase);
    }

    //TABLE USERS
    public long addUser(String user, String fullName, String password, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user);
        contentValues.put("fullName", fullName);
        contentValues.put("password", password);
        contentValues.put("email", email);
        long res = db.insert("users",null, contentValues);
        db.close();
        return res;
    }

    public boolean checkUser(String username, String password){
        String[] columns = { COL_USERS_ID };
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_USERS_USERNAME + "=?" + " and " + COL_USERS_PASSWORD + "=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if(count > 0)
            return true;
        else
            return false;
    }

    public boolean checkIfUserExists(Long userId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE ID = " + userId, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    public User getUserInfo(String username){
        User user = new User();
        String[] columns = {COL_USERS_USERNAME, COL_USERS_FULLNAME, COL_USERS_PASSWORD, COL_USERS_EMAIL};
        SQLiteDatabase db = getReadableDatabase();
        String select = COL_USERS_USERNAME + "=?";
        String[] selectArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, select, selectArgs, null, null, null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setFullName(cursor.getString(cursor.getColumnIndex("fullName")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        }
        cursor.close();
        db.close();
        return user;
    }

    public Long getUserId(String username) {
        String ID = null;
        Long userId = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USERS + " where username = ? ", new String[]{username});
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                ID = cursor.getString(cursor.getColumnIndex(COL_USERS_ID));
                userId = Long.parseLong(ID);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return userId;
    }

    public String getUsersFullNameFromId(Long userId) {
        String name = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from users where ID = " + userId,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                name = cursor.getString(cursor.getColumnIndex(COL_USERS_FULLNAME));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return name;
    }

    public String getUsername(String email) {
        String username = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USERS + " where email = ? ", new String[]{email});
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                username = cursor.getString(cursor.getColumnIndex(COL_USERS_USERNAME));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return username;
    }

    public void updateUser(String username, String fullName, String email) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERS_USERNAME, username);
        contentValues.put(COL_USERS_FULLNAME, fullName);
        contentValues.put(COL_USERS_EMAIL, email);
        db.update(TABLE_USERS, contentValues, "username = ? AND fullName = ? AND email = ?", new String[] {username, fullName, email});
        db.close();
    }

    public List<User> getUsers(){
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from users",null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Long ID = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_USERS_ID)));
                String fullName = cursor.getString(cursor.getColumnIndex(COL_USERS_FULLNAME));
                String username = cursor.getString(cursor.getColumnIndex(COL_USERS_USERNAME));
                String email = cursor.getString(cursor.getColumnIndex(COL_USERS_EMAIL));
                User user = new User(username, fullName, email);
                users.add(user);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return users;
    }

    //TABLE PROJECTS
    public long addProject(String projectName, String deadline){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("projectName", projectName);
        contentValues.put("deadline", deadline);

        long res = db.insert("projects",null, contentValues);
        db.close();
        return res;
    }

    public List<Project> getProjects(){
        List<Project> projects = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from projects",null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Long ID = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_PROJECTS_ID)));
                String name = cursor.getString(cursor.getColumnIndex(COL_PROJECTS_NAME));
                String stringDeadline = cursor.getString(cursor.getColumnIndex(COL_PROJECTS_DEADLINE));
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date date = null;
                try {
                    date = format.parse(stringDeadline);
                    System.out.println(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Project p = new Project(ID, name, date);
                projects.add(p);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return projects;
    }

    public Long findIdLastInsertedProject() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM projects",null);
        res.moveToLast();
        Long l = new Long(res.getInt(0));
        return l;
    }

    public void updateProject(Long p, String name, String deadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("projectName", name);
        contentValues.put("deadline", deadline);
        db.update(TABLE_PROJECTS, contentValues, "ID = ?", new String[] {String.valueOf(p)});
//        db.execSQL("UPDATE projects SET projectName = " + name + ", deadline = " + deadline + " WHERE projectId = " + p);
        db.close();
    }

    public void deleteProject(Long projectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM activities WHERE projectId = " + projectId);
        db.execSQL("DELETE FROM projects WHERE ID = " + projectId);
        db.close();
    }

    public Integer getTotalNumberProjects() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from projects",null);
        Integer no = cursor.getCount();
        cursor.close();
        db.close();
        return no;
    }

    public String getProjectNameFromId(Long projectId) {
        String name = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from projects where ID = " + projectId,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                name = cursor.getString(cursor.getColumnIndex(COL_PROJECTS_NAME));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return name;
    }

    //TABLE ACTIVITIES
    public long addActivity (String activityName, Integer isDone, Long userId, Integer difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ACTIVITIES_NAME, activityName);
        contentValues.put(COL_ACTIVITIES_IS_DONE, isDone);
        contentValues.put(COL_ACTIVITIES_USER_ID, userId);
        contentValues.put(COL_ACTIVITIES_DIFFICULTY, difficulty);
        //projectId will be updated afterwards
        long res = db.insert("activities",null, contentValues);
        db.close();
        return res;
    }

    public void updateActivitiesWithNullProjectId (Long p) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE activities SET projectId = " + p + " WHERE projectId is NULL");
        db.close();
    }

    public List<Activity> getActivities(){
        List<Activity> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from activities",null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Long ID = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_ID)));
                String name = cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_NAME));
                Long userId = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_USER_ID)));
                Integer difficulty = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_DIFFICULTY)));
                Boolean isDone = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_IS_DONE)));
                Activity activity = new Activity(ID, name, userId, isDone, difficulty);
                activities.add(activity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return activities;
    }

    public List<Activity> getActivitiesFromProjectId(Long projectId){
        List<Activity> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from activities where projectId = " + projectId,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Long ID = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_ID)));
                String name = cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_NAME));
                Long userId = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_USER_ID)));
                Integer difficulty = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_DIFFICULTY)));
                Boolean isDone = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_IS_DONE)));
                Activity activity = new Activity(ID, name, userId, isDone, difficulty);
                activities.add(activity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return activities;
    }

    public List<Activity> getActivitiesFromUserID(Long userID){
        List<Activity> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from activities where userId = " + userID,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Long ID = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_ID)));
                String name = cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_NAME));
                Long userId = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_USER_ID)));
                Integer difficulty = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_DIFFICULTY)));
                Boolean isDone = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_IS_DONE)));
                Long projectId = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_PROJECT_ID)));
                Activity activity = new Activity(ID, name, userId, isDone, difficulty, projectId);
                activities.add(activity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return activities;
    }

    public Long findIdLastInsertedActivity() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM activities",null);
        res.moveToLast();
        Long l = new Long(res.getInt(0));
        return l;
    }

    public void deleteActivitiesWithNullProjectId() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM activities WHERE projectId is NULL");
        //db.delete(TABLE_ACTIVITIES, "projectId = ?", new String[] {"NULL"});
        db.close();
    }

    public List<Activity> getProjectActivities(Long projectId){
        List<Activity> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from activities where projectId = " + projectId,null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Long ID = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_ID)));
                String name = cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_NAME));
                Long userId = Long.parseLong(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_USER_ID)));
                Integer difficulty = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_DIFFICULTY)));
                Boolean isDone = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_IS_DONE)));
                Activity activity = new Activity(ID, name, userId, isDone, difficulty);
                activities.add(activity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return activities;
    }

    public void checkFinishedOnActivity(String activityName, String userId, Integer difficulty, Integer check){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ACTIVITIES_IS_DONE, check);
        db.update(TABLE_ACTIVITIES, contentValues, "activityName = ? AND userId = ? AND difficulty = ?", new String[] {activityName, userId, String.valueOf(difficulty)});
        db.close();
    }

    public Integer getNumberUserActivities(Long ID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from activities where userId = " + ID,null);
        Integer no = cursor.getCount();
        cursor.close();
        db.close();
        return no;
    }

    public void updateFinishedActivity(Long activityId, Integer check) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE activities SET isDone = " + check + " WHERE ID = " + activityId);
        db.close();
    }

    public Integer getIsDone(Long ID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from activities where ID = " + ID,null);
        Integer isDone = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                isDone = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_IS_DONE)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return isDone;
    }

    public Integer getDifficulty(Long ID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from activities where ID = " + ID,null);
        Integer dif = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                dif = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_ACTIVITIES_DIFFICULTY)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return dif;
    }

    public void updateDifficultyActivity(Long ID, Integer difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE activities SET difficulty = " + difficulty + " WHERE ID = " + ID);
        db.close();
    }
}