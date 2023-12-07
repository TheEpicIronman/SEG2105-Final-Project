package com.uottawa.gcc;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private Button addEventButton;
    private Button backButtonAdd;
    private DatabaseHelper dbHelper;
    private Spinner eventTypeSpinner;
    private Spinner eventAgeSpinner;
    private Spinner difficultySpinner;
    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private EditText eventDateEditText;
    private EditText eventLocationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        dbHelper = new DatabaseHelper(this);

        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventDescriptionEditText = findViewById(R.id.eventDescEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventTypeSpinner = findViewById(R.id.eventTypeSpinner);
        eventAgeSpinner = findViewById(R.id.eventAgeSpinner);
        difficultySpinner = findViewById(R.id.eventDifficultySpinner);

        setupSpinners();

        addEventButton = findViewById(R.id.completeEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent();
                Intent intent = new Intent(getApplicationContext(), ManageEvents.class);
                startActivity(intent);
            }
        });

        backButtonAdd = findViewById(R.id.backButtonAdd);
        backButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManageEvents.class);
                startActivity(intent);
            }
        });
    }

    private void addEvent() {
        String name = eventNameEditText.getText().toString();
        String description = eventDescriptionEditText.getText().toString();
        String date = eventDateEditText.getText().toString();
        String location = eventLocationEditText.getText().toString();
        String type = eventTypeSpinner.getSelectedItem().toString();
        String ageRange = eventAgeSpinner.getSelectedItem().toString();
        String difficulty = difficultySpinner.getSelectedItem().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", 0);

        long result = dbHelper.insertEvent(userID, type, name, description, date, ageRange, difficulty, location);

        if (result > 0) {
            Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error adding event", Toast.LENGTH_SHORT).show();
        }
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