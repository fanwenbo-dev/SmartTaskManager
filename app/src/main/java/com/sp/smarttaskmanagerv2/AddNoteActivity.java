package com.sp.smarttaskmanagerv2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.Geofence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddNoteActivity extends AppCompatActivity {

    private EditText etNoteTitle, etNoteDescription, etAdditionalNote;
    private Spinner spinnerCategory;
    private Button btnSaveNote, btnCancelNote, btnAddToCalendar, btnSpeechToText;
    private DatabaseHelper dbHelper;
    private long noteId = -1;
    private long calendarEventId = -1; // Store event ID for deletion

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    private GeofenceHelper geofenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Initialize views
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteDescription = findViewById(R.id.etNoteDescription);
        etAdditionalNote = findViewById(R.id.etAdditionalNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        btnCancelNote = findViewById(R.id.btnCancelNote);
        btnAddToCalendar = findViewById(R.id.btnAddToCalendar);
        btnSpeechToText = findViewById(R.id.btnSpeechToText);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize GeofenceHelper
        geofenceHelper = new GeofenceHelper(this);

        // Set up Spinner with categories
        List<String> categories = Arrays.asList("Work", "Personal", "Shopping", "Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Check if editing an existing note
        if (getIntent().hasExtra("NOTE_ID")) {
            noteId = getIntent().getLongExtra("NOTE_ID", -1);
            String title = getIntent().getStringExtra("NOTE_TITLE");
            String description = getIntent().getStringExtra("NOTE_DESCRIPTION");
            String category = getIntent().getStringExtra("NOTE_CATEGORY");
            String additionalNote = getIntent().getStringExtra("NOTE_ADDITIONAL");

            // Populate fields
            etNoteTitle.setText(title);
            etNoteDescription.setText(description);
            etAdditionalNote.setText(additionalNote);
            spinnerCategory.setSelection(getCategoryIndex(category));
        }

        // Handle button clicks
        btnSaveNote.setOnClickListener(v -> saveNote());
        btnCancelNote.setOnClickListener(v -> finish());
        btnAddToCalendar.setOnClickListener(v -> addEventToCalendar());

        // Initialize Speech-to-Text
        btnSpeechToText.setOnClickListener(v -> startSpeechToText());
        initializeSpeechRecognizer();

        // Request location permissions
        requestLocationPermissions();
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {}

                @Override
                public void onBeginningOfSpeech() {}

                @Override
                public void onRmsChanged(float rmsdB) {}

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {}

                @Override
                public void onError(int error) {
                    Toast.makeText(AddNoteActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        etNoteDescription.setText(matches.get(0)); // Set the recognized text to the description field
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {}

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });

            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        } else {
            Toast.makeText(this, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSpeechToText() {
        if (speechRecognizer != null) {
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    private int getCategoryIndex(String category) {
        List<String> categories = Arrays.asList("Work", "Personal", "Shopping", "Other");
        return categories.indexOf(category);
    }

    private void saveNote() {
        String title = etNoteTitle.getText().toString().trim();
        String description = etNoteDescription.getText().toString().trim();
        String additionalNote = etAdditionalNote.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty()) {
            if (title.isEmpty()) etNoteTitle.setError("Title is required");
            if (description.isEmpty()) etNoteDescription.setError("Description is required");
            return;
        }

        if (noteId == -1) {
            dbHelper.insertNote(title, description, category, additionalNote);
        } else {
            dbHelper.updateNote(noteId, title, description, category, additionalNote);
        }

        // Add geofence for the task
        addGeofenceForTask(title, 37.4219999, -122.0840575, 100); // Example location (Googleplex)

        setResult(RESULT_OK);
        finish();
    }

    private void addGeofenceForTask(String taskTitle, double latitude, double longitude, float radius) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            List<Geofence> geofences = new ArrayList<Geofence>();
            geofences.add(geofenceHelper.createGeofence(taskTitle, latitude, longitude, radius));
            geofenceHelper.addGeofences(geofences);
        } else {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
        }
    }

    // Add Event to Calendar
    private void addEventToCalendar() {
        if (!hasCalendarPermissions()) {
            requestCalendarPermissions();
            return;
        }

        String title = etNoteTitle.getText().toString();
        String description = etNoteDescription.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.HOUR_OF_DAY, 10);
        beginTime.set(Calendar.MINUTE, 0);

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, beginTime.getTimeInMillis() + 60 * 60 * 1000);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        if (uri != null) {
            calendarEventId = Long.parseLong(uri.getLastPathSegment());
            Toast.makeText(this, "Event added to calendar!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
        }
    }

    // Check Calendar Permissions
    private boolean hasCalendarPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    // Request Calendar Permissions
    private void requestCalendarPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR
        }, 100);
    }

    // Handle Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addEventToCalendar();
            } else {
                Toast.makeText(this, "Calendar permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}