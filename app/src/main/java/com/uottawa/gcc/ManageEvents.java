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
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageEvents extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Button addEventsButton;
    private Button backButton;
    private List<Integer> eventIds = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<String> eventDetailsList = new ArrayList<>();
    private SearchView searchView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        dbHelper = new DatabaseHelper(this);

        addEventsButton = findViewById(R.id.addEventsButton);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", 0);

        // Get user info
        User user = getUserDetails(userID);

        if (user != null) {
            if ("Administrator".equals(user.getRole()) || "Organizer".equals(user.getRole())) {

                addEventsButton.setVisibility(View.VISIBLE);
                Log.d("DEBUG", "I am admin!");
                addEventsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Navigate to AddEventActivity
                        Log.d("DEBUG", "I was triggered!");
                        Intent intent = new Intent(ManageEvents.this, AddEventActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                addEventsButton.setVisibility(View.GONE);
            }
        }
        backButton = findViewById(R.id.backButtonEventList);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });

        populateEventsListView();
        setupSearchView();
    }

    private void populateEventsListView() {
        eventDetailsList.clear();
        eventIds.clear();

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
                int columnIndexDetails = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_NAME);
                String details = cursor.getString(columnIndexDetails);
                int columnIndexDate = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_DATE);
                String date = cursor.getString(columnIndexDate);

                // Combine event details into a single string for display
                String eventDetails = "ID: " + id + ", Type: " + type + ", Name: " + details + ", Date: " + date;
                eventDetailsList.add(eventDetails);

                eventIds.add(id);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Throw data into list for search and adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventDetailsList);
        ListView listView = findViewById(R.id.eventsListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the ID of the clicked item
                int eventId = eventIds.get(position);

                // Start a new activity and pass the ID
                Intent intent = new Intent(getApplicationContext(), EventPage.class);
                intent.putExtra("EVENT_ID", eventId);
                startActivity(intent);
            }
        });
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

    private void setupSearchView() {
        searchView = findViewById(R.id.searchView);

        // Simple search https://developer.android.com/reference/android/widget/SearchView.OnQueryTextListener#public-methods_1
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }


}
