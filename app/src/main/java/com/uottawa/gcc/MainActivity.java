package com.uottawa.gcc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
// FOR TESTING
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private DatabaseHelper dbHelper;  // DB Helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delete the existing database
        // COMMENT OUT THE BELOW WHEN WORKING ON REGISTRATION OTHERWISE IT WILL GET DELETED AND
        // REVERT TO THE ADMIN AND USER ACCOUNT ONLY
        this.deleteDatabase(DatabaseHelper.DATABASE_NAME);

        dbHelper = new DatabaseHelper(this);  // Initialize DB Helper

        // Initialize UI elements
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButtonHome);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Logging of username and password for debugging db
                Log.d("MainActivity", "Username: " + username + ", Password: " + password);

                // User auth
                if (authenticateUser(username, password)) {
                    // Transition to the welcome activity class
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Transition to the register class
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_PASSWORD,
                DatabaseHelper.COLUMN_ROLE
        };

        String selection = DatabaseHelper.COLUMN_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, projection, selection, selectionArgs, null, null, null);
        boolean userExists = (cursor.getCount() > 0);
        cursor.close();

        return userExists;
    }

}
