package com.sp.smarttaskmanagerv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class HomeFragment extends Fragment implements NoteAdapter.OnNoteActionListener {

    private RecyclerView rvNotes;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private FloatingActionButton fabAddTask;
    private DatabaseHelper dbHelper;

    private final ActivityResultLauncher<Intent> addNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    loadNotes(); // Refresh the list
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize database helper
        dbHelper = new DatabaseHelper(getContext());

        // Initialize RecyclerView
        rvNotes = view.findViewById(R.id.rvNotes);
        rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize note list and adapter
        noteList = dbHelper.getAllNotes();
        noteAdapter = new NoteAdapter(noteList, this);
        rvNotes.setAdapter(noteAdapter);

        // Initialize FloatingActionButton
        fabAddTask = view.findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> openAddNoteActivity());

        return view;
    }

    private void openAddNoteActivity() {
        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
        addNoteLauncher.launch(intent);
    }

    @Override
    public void onEditNote(Note note) {
        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
        intent.putExtra("NOTE_ID", note.getId());
        intent.putExtra("NOTE_TITLE", note.getTitle());
        intent.putExtra("NOTE_DESCRIPTION", note.getDescription());
        intent.putExtra("NOTE_CATEGORY", note.getCategory());
        intent.putExtra("NOTE_ADDITIONAL", note.getAdditionalNote());
        addNoteLauncher.launch(intent);
    }

    @Override
    public void onDeleteNote(Note note) {
        dbHelper.deleteNote(note.getId());
        loadNotes(); // Refresh the list
    }

    private void loadNotes() {
        noteList.clear();
        noteList.addAll(dbHelper.getAllNotes());
        noteAdapter.notifyDataSetChanged();
    }
}