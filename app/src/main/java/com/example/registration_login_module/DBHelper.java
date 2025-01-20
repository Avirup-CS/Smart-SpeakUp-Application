package com.example.registration_login_module;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    // Database and Table information
    private static final String DATABASE_NAME = "userdatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "users";

    // Column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PLACE = "place";
    public static final String COLUMN_PASSWORD = "password";

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table SQL query
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PLACE + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_PLACE + " TEXT");
        }
    }*/

    // Method to add a new record
    public boolean addRecord(String name, String phone, String email, String place, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PLACE, place);
        values.put(COLUMN_PASSWORD, password);

        long result = -1;
        try {
            result = db.insert(TABLE_NAME, null, values);
            Log.d("DBHelper", "Inserted Record ID: " + result + ", Place: " + place);
        } catch (Exception e) {
            Log.e("DBHelper", "Error inserting data: " + e.getMessage());
        } finally {
            db.close(); // Ensure database is closed
        }
        return result != -1;
    }

    // Method to retrieve a user by email
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);
    }

    // Method to retrieve a user by ID
    public Cursor getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PHONE, COLUMN_PLACE};
        return db.query(TABLE_NAME, columns, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // Method to update a user record
    public int updateRecord(int id, String name, String phone, String email, String place, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PLACE, place);
        if (password != null) {
            values.put(COLUMN_PASSWORD, password);
        }

        int rowsUpdated = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close(); // Ensure database is closed
        return rowsUpdated;
    }

    // Method to delete a user record
    public void deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close(); // Ensure database is closed
    }

    // Method to retrieve all records
    public Cursor getRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Method to retrieve user by email and phone number
    public Cursor getUserByEmailAndPhone(String email, String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_NAME, COLUMN_PASSWORD}; // Select username and password
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PHONE + " = ?"; // WHERE clause
        String[] selectionArgs = {email, phone}; // Arguments for the WHERE clause

        // Execute the query and return the result
        return db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

}
