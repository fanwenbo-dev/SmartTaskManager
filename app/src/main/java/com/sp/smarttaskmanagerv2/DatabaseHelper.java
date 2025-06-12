package com.sp.smarttaskmanagerv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.location.Geofence;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_ADDITIONAL_NOTE = "additional_note";

    // Create table query
    private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_CATEGORY + " TEXT,"
            + COLUMN_ADDITIONAL_NOTE + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // Insert a new note
    public long insertNote(String title, String description, String category, String additionalNote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_ADDITIONAL_NOTE, additionalNote);
        return db.insert(TABLE_NOTES, null, values);
    }

    // Fetch all notes
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);

        if (cursor.moveToFirst()) {
            do {
                // Safely get column indices
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                int additionalNoteIndex = cursor.getColumnIndex(COLUMN_ADDITIONAL_NOTE);

                // Ensure all columns exist
                if (idIndex >= 0 && titleIndex >= 0 && descriptionIndex >= 0 && categoryIndex >= 0 && additionalNoteIndex >= 0) {
                    Note note = new Note(
                            cursor.getLong(idIndex),
                            cursor.getString(titleIndex),
                            cursor.getString(descriptionIndex),
                            cursor.getString(categoryIndex),
                            cursor.getString(additionalNoteIndex)
                    );
                    notes.add(note);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    // Update a note
    public int updateNote(long id, String title, String description, String category, String additionalNote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_ADDITIONAL_NOTE, additionalNote);
        return db.update(TABLE_NOTES, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
    // Add this to your existing DatabaseHelper class
    public long insertGeofence(String title, double latitude, double longitude, float radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("radius", radius);
        return db.insert("geofences", null, values);
    }

    public List<Geofence> getAllGeofences() {
        List<Geofence> geofences = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM geofences", null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                float radius = cursor.getFloat(cursor.getColumnIndex("radius"));
                geofences.add(new Geofence.Builder()
                        .setRequestId(title)
                        .setCircularRegion(latitude, longitude, radius)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            } while (cursor.moveToNext());
        }
        cursor.close();
        return geofences;
    }

    // Delete a note
    public void deleteNote(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
}