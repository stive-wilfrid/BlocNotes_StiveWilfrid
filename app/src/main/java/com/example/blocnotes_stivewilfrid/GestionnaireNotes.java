package com.example.blocnotes_stivewilfrid;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Sauvegarde et charge les de l'application.
 * Les notes sont conservées dans un fichier JSON situé dans le stockage
 * interne de l'application
 *
 * @auteur Stive Wilfrid
 */

public class GestionnaireNotes {
    /** Nom du fichier qui contient toutes les notes. */
    private static final String NOM_FICHIER = "notes.json";

    /** Clé JSON de l'identifiant d'une note. */
    private static final String CLE_ID = "id";

    /** Clé JSON du nom d'une note. */
    private static final String CLE_TITRE = "titre";

    /** Clé JSON du texte d'une note. */
    private static final String CLE_CONTENU = "contenu";

    /** Clé JSON de la date de modification d'une note. */
    private static final String CLE_DATE = "dateModification";

    /** Fichier où les notes sont sauvegardées. */
    private final File fichier;

    /**
     * Construit le gestionnaire de notes.
     *
     * @param contexte contexte de l'application (pour accéder au stockage interne)
     */
    public GestionnaireNotes(Context contexte) {
        fichier = new File(contexte.getFilesDir(), NOM_FICHIER);
    }

    /**
     * Charge toutes les notes, triées de la plus récemment modifiée
     * à la plus ancienne.
     *
     * @return la liste des notes (vide s'il n'y en a aucune)
     */
    public List<Note> chargerNotes() {
        List<Note> notes = new ArrayList<>();
        if (!fichier.exists()) {
            return notes;
        }
        try (DataInputStream entree = new DataInputStream(new FileInputStream(fichier))) {
            byte[] octets = new byte[(int) fichier.length()];
            entree.readFully(octets);
            String json = new String(octets, StandardCharsets.UTF_8);
            JSONArray tableau = new JSONArray(json);
            for (int i = 0; i < tableau.length(); i++) {
                JSONObject objet = tableau.getJSONObject(i);
                notes.add(new Note(
                        objet.getLong(CLE_ID),
                        objet.getString(CLE_TITRE),
                        objet.getString(CLE_CONTENU),
                        objet.getLong(CLE_DATE)));
            }
        } catch (IOException | JSONException e) {
            // Fichier illisible : on retourne les notes lues jusqu'ici.
        }
        notes.sort((a, b) -> Long.compare(b.getDateModification(), a.getDateModification()));
        return notes;
    }

    /**
     * Recherche une note à partir de son identifiant.
     *
     * @param id identifiant de la note recherchée
     * @return la note trouvée, ou null si elle n'existe pas
     */
    public Note trouverNote(long id) {
        for (Note note : chargerNotes()) {
            if (note.getId() == id) {
                return note;
            }
        }
        return null;
    }

    /**
     * Enregistre une note : remplace la note qui a le même identifiant,
     * ou l'ajoute si elle n'existe pas encore. La date de modification
     * est mise à l'heure actuelle.
     *
     * @param note la note à enregistrer
     * @return true si l'enregistrement a réussi
     */
    public boolean enregistrerNote(Note note) {
        note.setDateModification(System.currentTimeMillis());
        List<Note> notes = chargerNotes();
        boolean trouvee = false;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == note.getId()) {
                notes.set(i, note);
                trouvee = true;
                break;
            }
        }
        if (!trouvee) {
            notes.add(note);
        }
        return sauvegarder(notes);
    }

    /**
     * Écrit la liste complète des notes dans le fichier JSON.
     *
     * @param notes les notes à sauvegarder
     * @return true si l'écriture a réussi
     */
    private boolean sauvegarder(List<Note> notes) {
        try (FileOutputStream sortie = new FileOutputStream(fichier)) {
            JSONArray tableau = new JSONArray();
            for (Note note : notes) {
                JSONObject objet = new JSONObject();
                objet.put(CLE_ID, note.getId());
                objet.put(CLE_TITRE, note.getTitre());
                objet.put(CLE_CONTENU, note.getContenu());
                objet.put(CLE_DATE, note.getDateModification());
                tableau.put(objet);
            }
            sortie.write(tableau.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException | JSONException e) {
            return false;
        }
    }
}
