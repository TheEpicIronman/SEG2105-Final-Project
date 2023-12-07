package com.uottawa.gcc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{
    // DB info
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cyclingClubDatabase";

    // Table name
    public static final String TABLE_USERS = "users";
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_REGISTRATIONS = "registrations";

    // Users column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_SOCIAL_MEDIA_URL = "social_media_url";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_NAME = "name";


    // Events column names
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EVENT_TYPE = "event_type";
    public static final String COLUMN_EVENT_NAME = "event_name";
    public static final String COLUMN_EVENT_DESCRIPTION = "event_description";
    public static final String COLUMN_EVENT_DATE = "event_date";
    public static final String COLUMN_AGE_RANGE = "age_range";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_LOCATION = "location";

    // Registration column names

    public static final String COLUMN_REGISTRATION_ID = "registration_id";
    public static final String COLUMN_REGISTER_EVENT_ID = "event_id";
    public static final String COLUMN_REGISTER_USER_ID = "user_id";

    // Create table query
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT NOT NULL,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_ROLE + " TEXT NOT NULL,"
            + COLUMN_SOCIAL_MEDIA_URL + " TEXT,"
            + COLUMN_PHONE_NUMBER + " TEXT,"
            + COLUMN_NAME + " TEXT"
            + ")";
    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "("
            + COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER NOT NULL," // User ID of the person who created the event
            + COLUMN_EVENT_TYPE + " TEXT NOT NULL,"
            + COLUMN_EVENT_NAME + " TEXT NOT NULL,"
            + COLUMN_EVENT_DESCRIPTION + " TEXT NOT NULL,"
            + COLUMN_EVENT_DATE + " TEXT NOT NULL,"
            + COLUMN_AGE_RANGE + " TEXT NOT NULL,"
            + COLUMN_DIFFICULTY + " TEXT NOT NULL,"
            + COLUMN_LOCATION + " TEXT NOT NULL"
            + ")";

    private static final String CREATE_TABLE_REGISTRATIONS = "CREATE TABLE " + TABLE_REGISTRATIONS + "("
            + COLUMN_REGISTRATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_REGISTER_USER_ID + " INTEGER NOT NULL,"
            + COLUMN_REGISTER_EVENT_ID + " INTEGER NOT NULL" + ")";


    // As this is a subclass of the SQLiteOpenHelper, it follows the same properties which will
    // create a new database if there isnt one using the onCreate and if there is a database it will
    // just pass that through
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_EVENTS);
        db.execSQL(CREATE_TABLE_REGISTRATIONS);

        // Insert initial data
        insertInitialData(db);
    }

    // Method to insert initial data
    private void insertInitialData(SQLiteDatabase db) {
        // Administrator account
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_EMAIL, "admin@example.com");
        adminValues.put(COLUMN_PASSWORD, "admin");
        adminValues.put(COLUMN_ROLE, "Administrator");
        db.insert(TABLE_USERS, null, adminValues);

        // Regular user/participant account
        ContentValues userParticipantValues = new ContentValues();
        userParticipantValues.put(COLUMN_USERNAME, "user");
        userParticipantValues.put(COLUMN_EMAIL, "userparticipant@example.com");
        userParticipantValues.put(COLUMN_PASSWORD, "user");
        userParticipantValues.put(COLUMN_ROLE, "Participant");
        db.insert(TABLE_USERS, null, userParticipantValues);

        // Regular user/organizer account
        ContentValues userOrganizerValues = new ContentValues();
        userOrganizerValues.put(COLUMN_USERNAME, "org");
        userOrganizerValues.put(COLUMN_EMAIL, "userorganizer@example.com");
        userOrganizerValues.put(COLUMN_PASSWORD, "org");
        userOrganizerValues.put(COLUMN_ROLE, "Organizer");
        db.insert(TABLE_USERS, null, userOrganizerValues);

        // uO accounts
        ContentValues uOadminValues = new ContentValues();
        uOadminValues.put(COLUMN_USERNAME, "gccadmin");
        uOadminValues.put(COLUMN_EMAIL, "admin@uottawa.com");
        uOadminValues.put(COLUMN_PASSWORD, "GCCRocks!");
        uOadminValues.put(COLUMN_ROLE, "Administrator");
        db.insert(TABLE_USERS, null, uOadminValues);

        ContentValues uOuserOrganizerValues = new ContentValues();
        uOuserOrganizerValues.put(COLUMN_USERNAME, "cyclingaddict");
        uOuserOrganizerValues.put(COLUMN_EMAIL, "userorganizer@uottawa.com");
        uOuserOrganizerValues.put(COLUMN_PASSWORD, "cyclingIsLife!");
        uOuserOrganizerValues.put(COLUMN_ROLE, "Organizer");
        db.insert(TABLE_USERS, null, uOuserOrganizerValues);



        ContentValues eventValues = new ContentValues();
        eventValues.put(COLUMN_USER_ID, 0);
        eventValues.put(COLUMN_EVENT_TYPE, "Time Trial");
        eventValues.put(COLUMN_EVENT_NAME, "Hiking Adventure");
        eventValues.put(COLUMN_EVENT_DESCRIPTION, "Join us for an exhilarating hiking experience in the mountains!");
        eventValues.put(COLUMN_EVENT_DATE, "2023-12-15"); // date in YYYY-MM-DD
        eventValues.put(COLUMN_AGE_RANGE, "25-29");
        eventValues.put(COLUMN_DIFFICULTY, "Intermediate");
        eventValues.put(COLUMN_LOCATION, "Blue Ridge Mountains");
        db.insert(TABLE_EVENTS, null, eventValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRATIONS);
        onCreate(db);
    }

    // Method to add an event to the database
    public long insertEvent(int userID, String eventType, String eventName, String eventDescription, String eventDate, String ageRange, String difficulty, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userID);
        values.put(COLUMN_EVENT_TYPE, eventType);
        values.put(COLUMN_EVENT_NAME, eventName);
        values.put(COLUMN_EVENT_DESCRIPTION, eventDescription);
        values.put(COLUMN_EVENT_DATE, eventDate);
        values.put(COLUMN_AGE_RANGE, ageRange);
        values.put(COLUMN_DIFFICULTY, difficulty);
        values.put(COLUMN_LOCATION, location);
        return db.insert(TABLE_EVENTS, null, values);
    }

    // Method to edit an existing event in the database
    public int updateEvent(int eventID, int userID, String eventType, String eventName, String eventDescription, String eventDate, String ageRange, String difficulty, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userID);
        values.put(COLUMN_EVENT_TYPE, eventType);
        values.put(COLUMN_EVENT_NAME, eventName);
        values.put(COLUMN_EVENT_DESCRIPTION, eventDescription);
        values.put(COLUMN_EVENT_DATE, eventDate);
        values.put(COLUMN_AGE_RANGE, ageRange);
        values.put(COLUMN_DIFFICULTY, difficulty);
        values.put(COLUMN_LOCATION, location);
        return db.update(TABLE_EVENTS, values, COLUMN_EVENT_ID + " = ?", new String[] { String.valueOf(eventID) });
    }

    // Method to delete an event from the database
    public int deleteEvent(int eventID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENTS, COLUMN_EVENT_ID + " = ?", new String[] { String.valueOf(eventID) });
    }

    public long addRegistration(int userID, int eventID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userID);
        values.put(COLUMN_EVENT_ID, eventID);
        return db.insert(TABLE_REGISTRATIONS, null, values);
    }

    public int deleteRegistration(int userID, int eventID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_REGISTRATIONS, COLUMN_USER_ID + " = ? AND " + COLUMN_EVENT_ID + " = ?",
                new String[]{String.valueOf(userID), String.valueOf(eventID)});
    }

    public boolean isUserRegisteredForEvent(int userID, int eventID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_REGISTRATION_ID};
        String selection = COLUMN_USER_ID + " = ? AND " + COLUMN_EVENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userID), String.valueOf(eventID)};
        Cursor cursor = db.query(TABLE_REGISTRATIONS, columns, selection, selectionArgs, null, null, null);
        boolean isRegistered = cursor.getCount() > 0;
        cursor.close();
        return isRegistered;
    }

    public List<String> getRegisteredUsersForEvent(int eventID) {
        List<String> registeredUsers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Relational query
        String query = "SELECT u." + COLUMN_USERNAME +
                " FROM " + TABLE_USERS + " u INNER JOIN " + TABLE_REGISTRATIONS + " r " +
                "ON u." + COLUMN_ID + " = r." + COLUMN_USER_ID +
                " WHERE r." + COLUMN_EVENT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(eventID)});

        if (cursor.moveToFirst()) {
            do {
                int registeredUsersColumn = cursor.getColumnIndex(COLUMN_USERNAME);
                registeredUsers.add(cursor.getString(registeredUsersColumn));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return registeredUsers;
    }

    public int updateProfile(int userID, String name, String phone, String social) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE_NUMBER, phone);
        values.put(COLUMN_SOCIAL_MEDIA_URL, social);

        return db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(userID)});
    }

}
