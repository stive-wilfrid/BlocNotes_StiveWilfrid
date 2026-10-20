package com.example.blocnotes_stivewilfrid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

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

        gestionnaire = new GestionnaireNotes(this);
        textAucuneNote = findViewById(R.id.text_aucune_note);

        //Toucher une note et l'ouvrer dans la page d'édition
        adaptateur = new NoteAdapter(note -> ouvrirEdition(note.getId()));
        RecyclerView liste = findViewById(R.id.recycler_view_notes);
        liste.setAdapter(adaptateur);

        // Le bouton + ouvre la page d'édition pour une nouvelle note
        findViewById(R.id.fab_add_note).setOnClickListener(v ->
                ouvrirEdition(EditionNoteActivity.AUCUNE_NOTE));
    }

}