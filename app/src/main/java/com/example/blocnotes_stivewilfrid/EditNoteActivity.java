package com.example.blocnotes_stivewilfrid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Activité d'édition d'une note.
 * Permet de modifier le titre et le contenu d'une note,
 * d'annuler / refaire, d'insérer la position GPS et d'enregistrer.
 *
 * @author Stive Wilfrid
 */
public class EditNoteActivity extends AppCompatActivity {

    /**
     * Initialise l'écran d'édition et affiche son layout.
     *
     * @param savedInstanceState état sauvegardé de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}