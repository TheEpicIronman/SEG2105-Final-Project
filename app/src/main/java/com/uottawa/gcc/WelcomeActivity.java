package com.uottawa.gcc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    private TextView welcomeTextView;
    private TextView roleTextView;
    private DatabaseHelper dbHelper;
    private Button manageEventsButton;
    private Button logOutButton;
    private Button goToClubs;
    private Button editProfileButton;
    private List<Integer> eventIds = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        dbHelper = new DatabaseHelper(this);


        // Initialize UI elements
        welcomeTextView = findViewById(R.id.welcomeTextView);
        roleTextView = findViewById(R.id.roleTextView);
        manageEventsButton = findViewById(R.id.manageEventsButton);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", 0);

        // Get user info
        User user = getUserDetails(userID);

        if (user != null) {
            welcomeTextView.setText("Welcome " + user.getUsername() + "!");
            roleTextView.setText("You are logged in as a \"" + user.getRole() + "\".");

            manageEventsButton.setVisibility(View.VISIBLE);
            manageEventsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Navigate to AddEventActivity
                    Intent intent = new Intent(WelcomeActivity.this, ManageEvents.class);
                    startActivity(intent);
                    finish();
                }
            });

            logOutButton = findViewById(R.id.logOutButton);
            logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            goToClubs = findViewById(R.id.goToClubs);
            goToClubs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WelcomeActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            editProfileButton = findViewById(R.id.editProfileButton);
            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WelcomeActivity.this, PersonalProfile.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private User getUserDetails(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_ROLE
        };

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, projection, selection, selectionArgs, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            int columnIndexID = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int id = cursor.getInt(columnIndexID);
            int columnIndexUsername = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
            String username = cursor.getString(columnIndexUsername);
            int columnIndexEmail = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
            String email = cursor.getString(columnIndexEmail);
            int columnIndexRole = cursor.getColumnIndex(DatabaseHelper.COLUMN_ROLE);
            String role = cursor.getString(columnIndexRole);
            cursor.close();
            return new User(id, username, email, role);
        }
        return null;
    }

}
