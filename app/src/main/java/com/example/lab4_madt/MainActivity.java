package com.example.lab4_madt;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;



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

        notesListView = findViewById(R.id.listView); // Reference your ListView here

        loadNotes();

        Button addNoteButton = findViewById(R.id.addNotebutton);
        addNoteButton.setOnClickListener(v -> {
            // AddNoteActivity is invoked when the button is clicked
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });

    }
    private void loadNotes() {
        try {
            JSONArray notesArray = loadNotesFromJsonFile();
            String[] noteNames = new String[notesArray.length()];
            String[] noteContents = new String[notesArray.length()];

            for (int i = 0; i < notesArray.length(); i++) {
                JSONObject note = notesArray.getJSONObject(i);
                noteNames[i] = note.getString("name");
                noteContents[i] = note.getString("content");
            }

            // Set the adapter to display notes (No custom adapter)
            // You can use a simple layout in the ListView for this
            for (int i = 0; i < noteNames.length; i++) {
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(this);
                TextView content = new TextView(this);

                title.setText(noteNames[i]);
                content.setText(noteContents[i]);
                layout.addView(title);
                layout.addView(content);
                notesListView.addView(layout);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error loading notes", e);
        }
    }

    private JSONArray loadNotesFromJsonFile() {
        try {
            FileInputStream fis = openFileInput("notes.json");
            byte[] data = new byte[fis.available()];

            // read the bytes from the FileInputStream and store the number of bytes read
            int bytesRead = fis.read(data);

            // if number of bytes read is valid
            if (bytesRead > 0) {
                String json = new String(data, 0, bytesRead, StandardCharsets.UTF_8); // Use bytesRead to convert only the valid portion
                return new JSONArray(json);
            }

            fis.close();
        } catch (Exception e) {
            Log.e(TAG, "Error loading notes from JSON file", e);
        }
        return new JSONArray(); // return an empty array if an error occurs
    }

}