package com.example.blocnotes_stivewilfrid;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Activité principale : affiche la liste des notes
 *
 * @auteur STIVE
 */

public class MainActivity extends AppCompatActivity {
    /**
     * Initialise l'écran principal et ses composts
     * @param savedInstanceState
     */

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
        // Ouvre l'écran d'édition pour créer une nouvelle note
        findViewById(R.id.fab_add_note).setOnClickListener(v ->
                startActivity(new Intent(this, EditNoteActivity.class)));
    }
}