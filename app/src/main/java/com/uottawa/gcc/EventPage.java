package com.uottawa.gcc;

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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EventPage extends AppCompatActivity {
    private Button editEventButton;
    private Button deleteEventButton;
    private Button backButton;
    private DatabaseHelper dbHelper;
    private boolean isEditMode = false;
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

            String type = cursor.getString(columnIndexType);
            String name = cursor.getString(columnIndexName);
            String description = cursor.getString(columnIndexDescription);
            String date = cursor.getString(columnIndexDate);
            String ageRange = cursor.getString(columnIndexAgeRange);
            String difficulty = cursor.getString(columnIndexDifficulty);
            String location = cursor.getString(columnIndexLocation);

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
                } else {
                    Toast.makeText(EventPage.this, "Error deleting event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });
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

//    private void setSpinnerSelection(Spinner spinner, String value) {
//        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
//        int position = adapter.getPosition(value);
//        spinner.setSelection(position);
//    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            int position = adapter.getPosition(value);

            // Check if the value exists in the spinner's adapter
            if (position >= 0) {
                spinner.setSelection(position);
            } else {
                // Handle the case where the value is not in the adapter.
                // You might want to set the spinner to a default position or leave it unchanged.
                Log.d("EventPage", "Value '" + value + "' not found in spinner.");
            }
        } else {
            // Handle null value case here
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
}
