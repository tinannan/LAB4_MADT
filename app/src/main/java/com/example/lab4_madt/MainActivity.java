package com.example.lab4_madt;

import static android.content.ContentValues.TAG;

import android.content.Context;
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
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
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

        notesListView = findViewById(R.id.listView);

        loadNotes();

        Button addNoteButton = findViewById(R.id.addNotebutton);
        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
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
                notes.add(new Note(name, content)); // Add each Note instance to the list
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
    public class NoteAdapter extends ArrayAdapter<Note> {
        public NoteAdapter(Context context, List<Note> notes) {
            super(context, 0, notes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Note note = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_item, parent, false);
            }

            TextView titleTextView = convertView.findViewById(R.id.note_title);
            TextView contentTextView = convertView.findViewById(R.id.note_content);

            titleTextView.setText(note.getName());
            contentTextView.setText(note.getContent());

            return convertView;
        }
    }
}
