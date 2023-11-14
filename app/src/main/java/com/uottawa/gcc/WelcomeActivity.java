package com.uottawa.gcc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    private TextView welcomeTextView;
    private TextView roleTextView;
    private DatabaseHelper dbHelper;
    private Button addEventsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        welcomeTextView = findViewById(R.id.welcomeTextView);
        roleTextView = findViewById(R.id.roleTextView);
        addEventsButton = findViewById(R.id.addEventsButton);


        String username = getIntent().getStringExtra("username");

        // Get user info
        User user = getUserDetails(username);

        if (user != null) {
            welcomeTextView.setText("Welcome " + user.getUsername() + "!");
            roleTextView.setText("You are logged in as a \"" + user.getRole() + "\".");

            if ("administrator".equals(user.getRole())) {
                addEventsButton.setVisibility(View.VISIBLE);
                addEventsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Navigate to AddEventActivity
                        Intent intent = new Intent(WelcomeActivity.this, AddEventActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                addEventsButton.setVisibility(View.GONE);
            }
        }

        populateEventsListView();
    }

    private void populateEventsListView() {
        List<String> eventDetailsList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_EVENTS, null);

        if (cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            Log.d("DEBUG", "Column names: " + Arrays.toString(columnNames));
            do {
                int columnIndexID = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_ID);
                int id = cursor.getInt(columnIndexID);
                int columnIndexType = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_TYPE);
                String type = cursor.getString(columnIndexType);
                int columnIndexDetails = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_DETAILS);
                String details = cursor.getString(columnIndexDetails);
                int columnIndexLevel = cursor.getColumnIndex(DatabaseHelper.COLUMN_LEVEL);
                String level = cursor.getString(columnIndexLevel);

                // Combine event details into a single string for display
                String eventDetails = "ID: " + id + ", Type: " + type + ", Details: " + details + ", Level: " + level;
                eventDetailsList.add(eventDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventDetailsList);
        ListView listView = findViewById(R.id.eventsListView);
        listView.setAdapter(adapter);
    }


    private User getUserDetails(String username){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_ROLE
        };

        String selection = DatabaseHelper.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, projection, selection, selectionArgs, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            // Reference for the following issue: "Cursor#getColumnIndex is annotated to return
            // value >= -1, and Cursor#getString requires an index >= 0. That means that the
            // argument could be a value outside of the allowed range (-1), which is why lint is
            // flagging it". I am following the fix shown in the reference.
            // https://issuetracker.google.com/issues/202193843?pli=1
            // Instead of int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            int columnIndexID = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int id = cursor.getInt(columnIndexID);
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
