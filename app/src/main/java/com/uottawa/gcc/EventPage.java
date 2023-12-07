package com.uottawa.gcc;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EventPage extends AppCompatActivity {
    private Button editEventButton;
    private Button deleteEventButton;
    private Button backButton;
    private Button registerEvent;
    private Button checkRegistrations;
    private DatabaseHelper dbHelper;
    private boolean isEditMode = false;
    private int creatorID = 0;
    private Spinner eventTypeSpinner;
    private Spinner eventAgeSpinner;
    private Spinner difficultySpinner;
    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private EditText eventDateEditText;
    private EditText eventLocationEditText;
    private EditText eventAgeEditText;
    private EditText difficultyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventpage);

        int eventID = getIntent().getIntExtra("EVENT_ID", 0);
        Log.d("DEBUG", "Event ID: " + eventID);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", 0);
        User user = getUserDetails(userID);

        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventDescriptionEditText = findViewById(R.id.eventDescEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventTypeSpinner = findViewById(R.id.eventTypeSpinner);
        eventAgeSpinner = findViewById(R.id.eventAgeSpinner);
        difficultySpinner = findViewById(R.id.eventDifficultySpinner);
        setupSpinners();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_EVENTS + " WHERE " +
                DatabaseHelper.COLUMN_EVENT_ID + " = ?", new String[]{String.valueOf(eventID)});

        if (cursor.moveToFirst()) {
            int columnIndexType = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_TYPE);
            int columnIndexName = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_NAME);
            int columnIndexDescription = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_DESCRIPTION);
            int columnIndexDate = cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_DATE);
            int columnIndexAgeRange = cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE_RANGE);
            int columnIndexDifficulty = cursor.getColumnIndex(DatabaseHelper.COLUMN_DIFFICULTY);
            int columnIndexLocation = cursor.getColumnIndex(DatabaseHelper.COLUMN_LOCATION);
            int columnIndexCreatorID = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);

            String type = cursor.getString(columnIndexType);
            String name = cursor.getString(columnIndexName);
            String description = cursor.getString(columnIndexDescription);
            String date = cursor.getString(columnIndexDate);
            String ageRange = cursor.getString(columnIndexAgeRange);
            String difficulty = cursor.getString(columnIndexDifficulty);
            String location = cursor.getString(columnIndexLocation);
            creatorID = Integer.parseInt(cursor.getString(columnIndexCreatorID));

            setSpinnerSelection(eventTypeSpinner, type);
            setSpinnerSelection(eventAgeSpinner, ageRange);
            setSpinnerSelection(difficultySpinner, difficulty);

            eventNameEditText.setText(name);
            eventDescriptionEditText.setText(description);
            eventDateEditText.setText(date);
            eventLocationEditText.setText(location);

            eventTypeSpinner.setEnabled(false);
            eventAgeSpinner.setEnabled(false);
            difficultySpinner.setEnabled(false);

        }
        cursor.close();

        editEventButton = findViewById(R.id.editEventButton);
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEditMode(eventID);
            }
        });

        deleteEventButton = findViewById(R.id.deleteEventButton);
        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result = deleteEventFromDatabase(eventID);
                if (result > 0) {
                    Toast.makeText(EventPage.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ManageEvents.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(EventPage.this, "Error deleting event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton = findViewById(R.id.backButtonManage);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManageEvents.class);
                startActivity(intent);
            }
        });

        registerEvent = findViewById(R.id.registerEvent);
        registerEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegistrationToggle(eventID);
            }
        });

        checkRegistrations = findViewById(R.id.checkRegistrations);
        checkRegistrations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisteredUsersDialog(eventID);
            }
        });

        if (user != null) {
            if ("Administrator".equals(user.getRole()) || ("Organizer".equals(user.getRole()) && userID == creatorID)) {
                editEventButton.setVisibility(View.VISIBLE);
                deleteEventButton.setVisibility(View.VISIBLE);
                checkRegistrations.setVisibility(View.VISIBLE);
            }else{
                editEventButton.setVisibility(View.GONE);
                deleteEventButton.setVisibility(View.GONE);
                checkRegistrations.setVisibility(View.GONE);
            }
        }
    }

    private void toggleEditMode(int eventID) {
        isEditMode = !isEditMode;

        eventNameEditText.setEnabled(isEditMode);
        eventDescriptionEditText.setEnabled(isEditMode);
        eventDateEditText.setEnabled(isEditMode);
        eventLocationEditText.setEnabled(isEditMode);
        eventTypeSpinner.setEnabled(isEditMode);
        eventAgeSpinner.setEnabled(isEditMode);
        difficultySpinner.setEnabled(isEditMode);

        if (isEditMode) {
            editEventButton.setText("Save");
        } else {
            editEventButton.setText("Edit");
            saveEventDetails(eventID);
        }
    }

    private void saveEventDetails(int eventID) {
        String name = eventNameEditText.getText().toString();
        String description = eventDescriptionEditText.getText().toString();
        String date = eventDateEditText.getText().toString();
        String location = eventLocationEditText.getText().toString();
        String type = eventTypeSpinner.getSelectedItem().toString();
        String ageRange = eventAgeSpinner.getSelectedItem().toString();
        String difficulty = difficultySpinner.getSelectedItem().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", 0);

        dbHelper.updateEvent(eventID, userID, type, name, description, date, ageRange, difficulty, location);
        Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
    }

    private int deleteEventFromDatabase(int eventID) {
        return dbHelper.deleteEvent(eventID);
    }

    private void setupSpinners() {
        setupEventTypeSpinner();
        setupAgeRangeSpinner();
        setupDifficultySpinner();
    }

    private void setupAgeRangeSpinner() {
        List<String> ageRanges = new ArrayList<>();
        ageRanges.add("15-19");
        ageRanges.add("20-24");
        ageRanges.add("25-29");
        ageRanges.add("30-34");
        ageRanges.add("35-39");
        ageRanges.add("40+");

        ArrayAdapter<String> ageRangeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ageRanges);
        ageRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventAgeSpinner.setAdapter(ageRangeAdapter);
    }

    private void setupDifficultySpinner() {
        List<String> difficulties = new ArrayList<>();
        difficulties.add("Easy");
        difficulties.add("Intermediate");
        difficulties.add("Challenging");

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, difficulties);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            int position = adapter.getPosition(value);

            if (position >= 0) {
                spinner.setSelection(position);
            } else {
                Log.d("EventPage", "Value '" + value + "' not found in spinner.");
            }
        } else {
            Log.d("EventPage", "Attempted to set spinner with null value.");
        }
    }


    private void setupEventTypeSpinner() {
        List<String> eventTypes = new ArrayList<>();
        eventTypes.add("Time Trial");
        eventTypes.add("Hill Climb");
        eventTypes.add("Road Stage Race");

        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);
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

    private void handleRegistrationToggle(int eventID) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", 0);

        if (dbHelper.isUserRegisteredForEvent(userID, eventID)) {
            // User already registered, so unregister them
            int result = dbHelper.deleteRegistration(userID, eventID);
            if (result > 0) {
                Toast.makeText(this, "Unregistered from event successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error in unregistering from event", Toast.LENGTH_SHORT).show();
            }
        } else {
            // User not registered, so register them
            long result = dbHelper.addRegistration(userID, eventID);
            if (result > 0) {
                Toast.makeText(this, "Registered for event successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error in registering for event", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showRegisteredUsersDialog(int eventID) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_registered_users);
        ListView listView = dialog.findViewById(R.id.list_registered_users);

        List<String> registeredUsers = dbHelper.getRegisteredUsersForEvent(eventID);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registeredUsers);
        listView.setAdapter(adapter);

        dialog.show();
    }
}
