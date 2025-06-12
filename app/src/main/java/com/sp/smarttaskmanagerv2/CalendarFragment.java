package com.sp.smarttaskmanagerv2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private Button btnDeleteSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize CalendarView
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setClickable(false); // Make the calendar non-interactive

        // Initialize Delete Button
        btnDeleteSelected = view.findViewById(R.id.btnDeleteSelected);
        btnDeleteSelected.setOnClickListener(v -> deleteSelectedEvents());

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(eventList, position -> {
            // Toggle selection
            btnDeleteSelected.setVisibility(eventAdapter.getSelectedEvents().isEmpty() ? View.GONE : View.VISIBLE);
        });
        recyclerView.setAdapter(eventAdapter);

        // Check for Calendar Permissions
        if (hasCalendarPermissions()) {
            loadCalendarEvents();
        } else {
            requestCalendarPermissions();
        }

        // Add sample events
        addSampleEvents();

        return view;
    }

    // Check Calendar Permissions
    private boolean hasCalendarPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    // Request Calendar Permissions
    private void requestCalendarPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST_CODE);
    }

    // Handle Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCalendarEvents();
            } else {
                Toast.makeText(getContext(), "Calendar permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Load Events from Calendar
    private void loadCalendarEvents() {
        eventList.clear();

        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] projection = {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART
        };

        String selection = CalendarContract.Events.DTSTART + " > ?";
        String[] selectionArgs = new String[]{String.valueOf(System.currentTimeMillis())}; // Only future events

        Cursor cursor = requireContext().getContentResolver().query(uri, projection, selection, selectionArgs, CalendarContract.Events.DTSTART + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long eventId = cursor.getLong(0);
                String title = cursor.getString(1);
                long startTime = cursor.getLong(2);

                eventList.add(new Event(eventId, title, startTime));
            }
            cursor.close();
        }

        eventAdapter.notifyDataSetChanged();
    }

    // Add sample events
    private void addSampleEvents() {
        eventList.add(new Event(1, "Team Meeting", System.currentTimeMillis() + 86400000)); // 1 day from now
        eventList.add(new Event(2, "Project Deadline", System.currentTimeMillis() + 172800000)); // 2 days from now
        eventList.add(new Event(3, "Client Call", System.currentTimeMillis() + 259200000)); // 3 days from now
        eventList.add(new Event(4, "Client Call", System.currentTimeMillis() + 259200000)); // 3 days from now

        eventAdapter.notifyDataSetChanged();
    }

    // Delete selected events
    private void deleteSelectedEvents() {
        Set<Long> selectedEvents = eventAdapter.getSelectedEvents();
        eventList.removeIf(event -> selectedEvents.contains(event.getId()));
        eventAdapter.notifyDataSetChanged();
        btnDeleteSelected.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Selected events deleted", Toast.LENGTH_SHORT).show();
    }
}