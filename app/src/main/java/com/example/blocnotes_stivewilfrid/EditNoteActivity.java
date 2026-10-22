package com.example.blocnotes_stivewilfrid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

/**
 * Activité d'édition d'une note.
 * Permet de modifier le titre et le contenu d'une note,
 * d'annuler / refaire, d'insérer la position GPS et d'enregistrer.
 *
 * @author Stive Wilfrid
 */
public class EditNoteActivity extends AppCompatActivity {

    /** Clé de l'Intent qui contient l'identifiant de la note à ouvrir. */
    public static final String EXTRA_ID_NOTE = "id_note";

    /** Valeur indiquant qu'il s'agit d'une nouvelle note. */
    public static final long AUCUNE_NOTE = -1;

    /** Code de la demande de permission de localisation. */
    private static final int CODE_PERMISSION_POSITION = 1;

    /** Gère la sauvegarde des notes. */
    private GestionnaireNotes gestionnaire;

    /** Identifiant de la note éditée, ou {@link #AUCUNE_NOTE} si elle n'est pas encore enregistrée. */
    private long idNote = AUCUNE_NOTE;

    /** Champ du nom de la note. */
    private EditText editTitre;

    /** Champ du contenu de la note. */
    private EditText editContenu;

    /** Bouton « Annuler ». */
    private Button boutonAnnuler;

    /** Bouton « Refaire ». */
    private Button boutonRefaire;

    /** Versions précédentes du contenu, pour annuler (undo). */
    private final Deque<String> pileAnnuler = new ArrayDeque<>();

    /** Versions annulées du contenu, pour refaire (redo). */
    private final Deque<String> pileRefaire = new ArrayDeque<>();

    /** Contenu avant la modification en cours (rempli par le TextWatcher). */
    private String contenuAvant = "";

    /** Vrai pendant que le programme change lui-même le texte (annuler/refaire/chargement). */
    private boolean changementParProgramme = false;

    /**
     * Initialise l'écran d'édition, charge la note demandée ou le texte
     * importé, et branche les boutons.
     *
     * @param savedInstanceState état sauvegardé de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        gestionnaire = new GestionnaireNotes(this);
        editTitre = findViewById(R.id.edit_text_title);
        editContenu = findViewById(R.id.edit_text_content);
        boutonAnnuler = findViewById(R.id.btn_undo);
        boutonRefaire = findViewById(R.id.btn_redo);

        //Barre du haut : fleche de retour et bouton <<Partager>>
        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.menu_edit);
        toolbar.setOnMenuItemClickListener(item ->{
            if(item.getItemId() == R.id.action_partager) {
                partagerNote();
                return true;
            }
            return false;
        });

        if (savedInstanceState != null) {
            // Après une rotation, on garde la note en cours (les champs se restaurent seuls)
            idNote = savedInstanceState.getLong(EXTRA_ID_NOTE, AUCUNE_NOTE);

        }else if (Intent.ACTION_SEND.equals(getIntent().getAction())){
            importerTexte(getIntent());
        }else{
            chargerNote(getIntent().getLongExtra(EXTRA_ID_NOTE, AUCUNE_NOTE));
        }
        toolbar.setTitle(idNote == AUCUNE_NOTE ?R.string.nouvelle_note_titre : R.string.titre_edition);

        editContenu.addTextChangedListener(new SuiviModifications());
        boutonAnnuler.setOnClickListener(v -> annuler());
        boutonRefaire.setOnClickListener(v -> refaire());
        findViewById(R.id.btn_gps).setOnClickListener(v ->demanderPosition());
        findViewById(R.id.btn_save).setOnClickListener(v -> enregistrer());
        mettreAJourBoutons();
    }

    /**
     * Conserve l'identifiant de la note lors d'une rotation de l'écran.
     *
     * @param outState état à sauvegarder
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_ID_NOTE, idNote);
    }

    /**
     * Afficher la note demandée dans les champs.
     *
     * @param id identifiant de la note, ou {@link #AUCUNE_NOTE} pour une nouvelle note
     */
    private void chargerNote(long id){
        Note note = gestionnaire.trouverNote(id);
        if(note != null){
            idNote = note.getId();
            editTitre.setText(note.getTitre());
            editContenu.setText(note.getContenu());
        }
    }

    /**
     * Crée une nouvelle note à partir du texte partagé par une autre
     * application ( ou par cette application) et l'enregistre
     *
     * @param intention l'Intent ACTION_SEND reçu
     */
    private void importerTexte(Intent intention) {
        String texte = intention.getStringExtra(Intent.EXTRA_TEXT);
        String sujet = intention.getStringExtra(Intent.EXTRA_SUBJECT);
        if (texte == null) {
            texte = "";
        }
        if(sujet == null || sujet.trim().isEmpty()){
            sujet = getString(R.string.titre_note_importee);
        }
        editTitre.setText(sujet);
        editContenu.setText(texte);
        enregistrer();
        Toast.makeText(this, R.string.texte_importe, Toast.LENGTH_SHORT).show();
    }

    /**
     * Enregistre la note. Une nouvelle note reçoit un identifiant
     * lors de son premier enregistrement.
     */
    private void enregistrer(){
        String titre = editTitre.getText().toString().trim();
        if (titre.isEmpty()){
            titre = getString(R.string.sans_titre);
        }
        String contenu = editContenu.getText().toString();

        Note note = gestionnaire.trouverNote(idNote);
        if (note == null) {
            idNote = System.currentTimeMillis();
            note = new Note(idNote, titre, contenu, idNote);
        }else{
            note.setTitre(titre);
            note.setContenu(contenu);
        }

        if(gestionnaire.enregistrerNote(note)){
            Toast.makeText(this, R.string.note_enregistree, Toast.LENGTH_SHORT).show();
            Toolbar toolbar = findViewById(R.id.toolbar_edit);
            toolbar.setTitle(R.string.titre_edition);
        }else{
            Toast.makeText(this, R.string.erreur_enregistrement, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Annuler la dernière modification du contenu (undo).
     */
    private void annuler() {
        if (!pileAnnuler.isEmpty()) {
            pileRefaire.push(editContenu.getText().toString());
            remplacerContenu(pileAnnuler.pop());
        }
    }

    /**
     * Refait la dernière modification annulée (redo).
     */
    private void refaire() {
        if (!pileRefaire.isEmpty()) {
            pileAnnuler.push(editContenu.getText().toString());
            remplacerContenu(pileRefaire.pop());
        }
    }

    /**
     * Remplace le contenu sans que ce changement soit enregistré
     * dans l'historique annuler/refaire.
     *
     * @param texte le nouveau contenu
     */
    private void remplacerContenu(String texte) {
        changementParProgramme = true;
        editContenu.setText(texte);
        editContenu.setSelection(texte.length());
        changementParProgramme = false;
        mettreAJourBoutons();
    }

    /**
     * Active ou désactive les boutons Annuler et Refaire selon
     * l'historique disponible.
     */
    private void mettreAJourBoutons() {
        boutonAnnuler.setEnabled(!pileAnnuler.isEmpty());
        boutonRefaire.setEnabled(!pileRefaire.isEmpty());
    }

    /**
     * Vérifie la permission de localisation, la demande au besoin,
     * puis lance la recherche de la position.
     */
    private void demanderPosition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            rechercherPosition();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    CODE_PERMISSION_POSITION);
        }
    }

    /**
     * Reçoit la réponse de l'utilisateur à la demande de permission.
     *
     * @param requestCode  code de la demande
     * @param permissions  permissions demandées
     * @param grantResults réponses de l'utilisateur
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSION_POSITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                rechercherPosition();
            } else {
                Toast.makeText(this, R.string.permission_refusee, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Demande la position actuelle au service de localisation Android
     * (GPS/GNSS, ou réseau si le GPS est désactivé).
     */
    @SuppressLint("MissingPermission") // la permission est vérifiée dans demanderPosition()
    private void rechercherPosition() {
        LocationManager gestionPosition = (LocationManager) getSystemService(LOCATION_SERVICE);
        String fournisseur;
        if (gestionPosition.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            fournisseur = LocationManager.GPS_PROVIDER;
        } else if (gestionPosition.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fournisseur = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, R.string.localisation_desactivee, Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, R.string.recherche_position, Toast.LENGTH_SHORT).show();
        Location derniere = gestionPosition.getLastKnownLocation(fournisseur);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            gestionPosition.getCurrentLocation(fournisseur, null, getMainExecutor(), position -> {
                if (position != null) {
                    insererPosition(position);
                } else if (derniere != null) {
                    insererPosition(derniere);
                } else {
                    Toast.makeText(this, R.string.position_introuvable, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            gestionPosition.requestSingleUpdate(fournisseur, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location position) {
                    insererPosition(position);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // Non utilisé (requis sur les anciennes versions d'Android)
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    // Non utilisé
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    // Non utilisé
                }
            }, Looper.getMainLooper());
        }
    }

    /**
     * Insère la latitude et la longitude à la position du curseur
     * dans le contenu de la note.
     *
     * @param position la position obtenue
     */
    private void insererPosition(Location position) {
        String texte = String.format(Locale.US, "Lat : %.6f, Long : %.6f",
                position.getLatitude(), position.getLongitude());
        int curseur = Math.max(editContenu.getSelectionStart(), 0);
        editContenu.getText().insert(curseur, texte);
    }

    /**
     * Partage la note avec une autre application (ou avec cette
     * application, ce qui crée une nouvelle note).
     */
    private void partagerNote() {
        Intent envoi = new Intent(Intent.ACTION_SEND);
        envoi.setType("text/plain");
        envoi.putExtra(Intent.EXTRA_SUBJECT, editTitre.getText().toString());
        envoi.putExtra(Intent.EXTRA_TEXT, editContenu.getText().toString());
        startActivity(Intent.createChooser(envoi, getString(R.string.partager)));
    }

    /**
     * Enregistre chaque modification du contenu dans l'historique
     * annuler/refaire.
     */
    private class SuiviModifications implements TextWatcher {

        /**
         * Mémorise le contenu avant la modification.
         *
         * @param s     texte avant la modification
         * @param start position du début de la modification
         * @param count nombre de caractères remplacés
         * @param after nombre de nouveaux caractères
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            contenuAvant = s.toString();
        }

        /**
         * Non utilisé.
         *
         * @param s      texte modifié
         * @param start  position du début de la modification
         * @param before nombre de caractères remplacés
         * @param count  nombre de nouveaux caractères
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Rien à faire ici
        }

        /**
         * Ajoute l'ancien contenu à l'historique, sauf si le changement
         * vient du programme lui-même.
         *
         * @param s texte après la modification
         */
        @Override
        public void afterTextChanged(Editable s) {
            if (!changementParProgramme && !contenuAvant.equals(s.toString())) {
                pileAnnuler.push(contenuAvant);
                pileRefaire.clear();
                mettreAJourBoutons();
            }
        }
    }
}