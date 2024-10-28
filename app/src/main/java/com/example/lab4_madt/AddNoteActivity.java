package com.example.lab4_madt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class AddNoteActivity extends AppCompatActivity {
    private EditText noteNameEditText;
    private EditText noteContentEditText;
    private static final String TAG = "AddNoteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addnote), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noteNameEditText = findViewById(R.id.noteName);
        noteContentEditText = findViewById(R.id.editTextNote);
        Button saveNoteButton = findViewById(R.id.buttonSaveNote);

        // Set click listener for save button
        saveNoteButton.setOnClickListener(v -> saveNoteToJsonFile());
    }
    private void saveNoteToJsonFile() {
        try {
            String noteName = noteNameEditText.getText().toString();
            String noteContent = noteContentEditText.getText().toString();

            // Load existing notes from JSON file
            JSONArray notesArray = loadNotesFromJsonFile();

            // Generate a new unique ID based on the current number of notes
            int noteId = notesArray.length();

            // Create a new JSONObject for the note
            JSONObject newNote = new JSONObject();
            newNote.put("name", noteName);
            newNote.put("id", noteId);
            newNote.put("content", noteContent);

            // Add the new note to the array
            notesArray.put(newNote);

            // Save updated notes array back to JSON file
            FileOutputStream fos = openFileOutput("notes.json", MODE_PRIVATE);
            fos.write(notesArray.toString().getBytes(StandardCharsets.UTF_8));
            fos.close();

            Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error saving note to JSON file", e);
        }

    }

    private JSONArray loadNotesFromJsonFile() {
        try {
            FileInputStream fis = openFileInput("notes.json");
            byte[] data = new byte[fis.available()];

            // Read data and store the number of bytes read
            int bytesRead = fis.read(data);

            // Check if bytes were actually read
            if (bytesRead > 0) {
                String json = new String(data, 0, bytesRead, StandardCharsets.UTF_8);
                return new JSONArray(json);
            }

            fis.close();
        } catch (Exception e) {
            Log.e(TAG, "Error loading notes from JSON file", e);
        }

        return new JSONArray(); // Return an empty JSONArray if an error occurs or no data was read
    }

}

