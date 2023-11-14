package com.uottawa.gcc;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private Button registerButton;
    private Spinner roleSpinner;
    private DatabaseHelper dbHelper;  // DB Helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);  // Initialize DB Helper

        // Initialize UI elements
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);
        registerButton = findViewById(R.id.registerButtonHome);
        roleSpinner = findViewById(R.id.roleSpinner);

        // Initialize the ArrayAdapter for the Spinner with roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles_array, // Add a string array resource for roles in your strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registerNewUser();
            }
        });
    }

    private void registerNewUser(){
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = passwordConfirmEditText.getText().toString();
        String role = roleSpinner.getSelectedItem().toString();

        // Check if inputs are valid
        boolean validation = isValidInput(username, email, password, confirmPassword, role);
        if(validation){
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, username);
            values.put(DatabaseHelper.COLUMN_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_PASSWORD, password);
            values.put(DatabaseHelper.COLUMN_ROLE, role);

            // The insert command says that the return is -1 if its not valid for some reason, so
            // we will check for that, though im not sure what would cause a faulty insert.
            long insertedRowId = db.insert(DatabaseHelper.TABLE_USERS, null, values);

            if(insertedRowId == -1){
                Toast.makeText(RegisterActivity.this, "Error registering user", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private boolean isValidInput(String username, String email, String password, String confirmPassword, String role){
        // Check if password and confirmPassword match
        if(!password.equals(confirmPassword)){
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(role.equals("Administrator")){
            Toast.makeText(RegisterActivity.this, "You are not allowed to sign up as admin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // This is regex taken from howtodoitinjava.com since the validation for a official email is as shown below.
        // Reference: https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
        String regex = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){
            Toast.makeText(RegisterActivity.this, "Email is not valid!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if username or email already exists in the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_EMAIL };
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ? OR " + DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { username, email };

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, projection, selection, selectionArgs, null, null, null);
        boolean userExists = (cursor.getCount() > 0);
        cursor.close();

        if (userExists) {
            Toast.makeText(RegisterActivity.this, "Username or email already exists", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
