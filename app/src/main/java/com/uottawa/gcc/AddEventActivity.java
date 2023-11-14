package com.uottawa.gcc;

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

public class AddEventActivity extends AppCompatActivity {
    private Spinner eventTypeSpinner;
    private EditText eventDetailsEditText;
    private Spinner levelSpinner;
    private Button addEventButton;
    private Button editEventButton;
    private Button deleteEventButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        eventTypeSpinner = findViewById(R.id.eventTypeSpinner);
        eventDetailsEditText = findViewById(R.id.eventDetailsEditText);
        levelSpinner = findViewById(R.id.levelSpinner);
        addEventButton = findViewById(R.id.addEventButton);
        editEventButton = findViewById(R.id.editEventButton);
        deleteEventButton = findViewById(R.id.deleteEventButton);

        // Setup Spinners
        setupEventTypeSpinner();
        setupLevelSpinner();

        // Add event button click listener
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String eventType = eventTypeSpinner.getSelectedItem().toString();
                String eventDetails = eventDetailsEditText.getText().toString();
                String level = levelSpinner.getSelectedItem().toString();

                // Add event to the database
                long result = addEventToDatabase(eventType, eventDetails, level);

                if (result != -1) {
                    Toast.makeText(AddEventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddEventActivity.this, "Error adding event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Edit event button click listener
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String eventType = eventTypeSpinner.getSelectedItem().toString();
                String eventDetails = eventDetailsEditText.getText().toString();
                String level = levelSpinner.getSelectedItem().toString();

                // Edit event in the database
                int result = editEventInDatabase(Integer.parseInt(eventType), eventType, eventDetails, level);

                if (result > 0) {
                    Toast.makeText(AddEventActivity.this, "Event edited successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddEventActivity.this, "Error editing event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Delete event button click listener
        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String eventType = eventTypeSpinner.getSelectedItem().toString();

                // Delete event from the database
                int result = deleteEventFromDatabase(Integer.parseInt(eventType));

                if (result > 0) {
                    Toast.makeText(AddEventActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddEventActivity.this, "Error deleting event", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupEventTypeSpinner() {
        List<String> eventTypes = new ArrayList<>();
        // Add event types (modify as needed)
        eventTypes.add("Time Trial");
        eventTypes.add("Hill Climb");
        eventTypes.add("Road Stage Race");
        eventTypes.add("Other");
        // Add more event types...

        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);
    }

    private void setupLevelSpinner() {
        List<String> levels = new ArrayList<>();
        // Add levels (modify as needed)
        levels.add("Beginner");
        levels.add("Intermediate");
        levels.add("Advanced");
        // Add more levels...

        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);
    }

    private long addEventToDatabase(String eventType, String eventDetails, String level) {
        dbHelper = new DatabaseHelper(this);
        long result = dbHelper.insertEvent(eventType, eventDetails, level);

        if (result != -1) {
            Log.d("AddEventActivity", "Event added successfully. Row ID: " + result);
        } else {
            Log.e("AddEventActivity", "Error adding event");
        }

        return result;
    }

    private int editEventInDatabase(int eventID, String eventType, String eventDetails, String level) {
        dbHelper = new DatabaseHelper(this);
        return dbHelper.updateEvent(eventID, eventType, eventDetails, level);
    }

    private int deleteEventFromDatabase(int eventID) {
        dbHelper = new DatabaseHelper(this);
        return dbHelper.deleteEvent(eventID);
    }
}