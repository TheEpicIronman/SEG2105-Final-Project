package com.uottawa.gcc;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PersonalProfile extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextSocial;
    private Button updateButton;
    private Button backButton;
    private DatabaseHelper dbHelper;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", 0);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextSocial = findViewById(R.id.editTextSocial);

        updateButton = findViewById(R.id.personalProfileUpdate);
        backButton = findViewById(R.id.personalProfileBack);

        loadUserProfile();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadUserProfile() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(userID)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int nameCol = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
            String name = cursor.getString(nameCol);
            int phoneCol = cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE_NUMBER);
            String phone = cursor.getString(phoneCol);
            int socialCol = cursor.getColumnIndex(DatabaseHelper.COLUMN_SOCIAL_MEDIA_URL);
            String social = cursor.getString(socialCol);

            editTextName.setText(name);
            editTextPhone.setText(phone);
            editTextSocial.setText(social);
            cursor.close();
        }
    }

    private void updateProfile() {
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String social = editTextSocial.getText().toString();

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
            return;
        }

        int rowsAffected = dbHelper.updateProfile(userID, name, phone, social);
        if (rowsAffected > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        // Regex for validating US phone number format (e.g., 123-456-7890)
        String regex = "^\\d{3}-\\d{3}-\\d{4}$";
        return phone.matches(regex);
    }

}
