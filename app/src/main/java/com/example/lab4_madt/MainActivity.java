package com.example.lab4_madt;

import static android.content.ContentValues.TAG;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView notesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//notes view
        notesListView = findViewById(R.id.listView);

        loadNotes();
        // click listener for add note button
        Button addNoteButton = findViewById(R.id.addNotebutton);
        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });
//long-click listener for delete
        notesListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // show delete confirmation dialog
            showDeleteDialog(position);
            return true;
        });
    }


    private void loadNotes() {
        try {
            JSONArray notesArray = loadNotesFromJsonFile();
            List<Note> notes = new ArrayList<>();

            for (int i = 0; i < notesArray.length(); i++) {
                JSONObject noteJson = notesArray.getJSONObject(i);
                String name = noteJson.getString("name");
                String content = noteJson.getString("content");
                notes.add(new Note(name, content)); // add each Note instance to the list
            }


            NoteAdapter adapter = new NoteAdapter(this, notes);
            notesListView.setAdapter(adapter);

        } catch (Exception e) {
            Log.e("MainActivity", "Error loading notes", e);
        }
    }
//json
    private JSONArray loadNotesFromJsonFile() {
        try {
            FileInputStream fis = openFileInput("notes.json");
            byte[] data = new byte[fis.available()];
            int bytesRead = fis.read(data);

            if (bytesRead > 0) {
                String json = new String(data, 0, bytesRead, StandardCharsets.UTF_8);
                return new JSONArray(json);
            }

            fis.close();
        } catch (Exception e) {
            Log.e(TAG, "Error loading notes from JSON file", e);
        }
        return new JSONArray();
    }

    // custom adapter for Note class
    public static class NoteAdapter extends ArrayAdapter<Note> {
        public NoteAdapter(Context context, List<Note> notes) {
            super(context, 0, notes);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Note note = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_item, parent, false);
            }

            TextView titleTextView = convertView.findViewById(R.id.note_title);
            TextView contentTextView = convertView.findViewById(R.id.note_content);

            assert note != null;
            titleTextView.setText(note.getName());
            contentTextView.setText(note.getContent());

            return convertView;
        }
    }
    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes", (dialog, which) -> deleteNoteAtPosition(position))
                .setNegativeButton("No", null)
                .show();
    }

    // method to delete note from JSON and update ListView
    private void deleteNoteAtPosition(int position) {
        try {
            JSONArray notesArray = loadNotesFromJsonFile();

            // remove the note at the specified position from the array
            notesArray.remove(position);

            // updated array to json
            FileOutputStream fos = openFileOutput("notes.json", MODE_PRIVATE);
            fos.write(notesArray.toString().getBytes(StandardCharsets.UTF_8));
            fos.close();

            Toast.makeText(this, "Note has been deleted", Toast.LENGTH_SHORT).show();

            // refresh the ListView
            loadNotes();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting note from JSON file", e);
        }
    }
}
