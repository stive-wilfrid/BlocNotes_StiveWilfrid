package com.example.blocnotes_stivewilfrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adaptateur qui affiche les notes dans la liste de l'écran principal.
 * Chaque note utilise le layout item_note.xml.
 *
 * @author Stive Wilfrid
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    /**
     * Écouteur appelé lorsqu'une note de la liste est touchée.
     */
    public interface OnNoteClickListener {
        /**
         * @param note la note sélectionnée
         */
        void onNoteClick(Note note);
    }

    /** Format d'affichage de la date de dernière modification. */
    private final SimpleDateFormat formatDate =
            new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.CANADA_FRENCH);

    /** Notes affichées dans la liste. */
    private final List<Note> notes = new ArrayList<>();

    /** Écouteur des clics sur une note. */
    private final OnNoteClickListener ecouteur;

    /**
     * Construit l'adaptateur.
     *
     * @param ecouteur écouteur appelé lors d'un clic sur une note
     */
    public NoteAdapter(OnNoteClickListener ecouteur) {
        this.ecouteur = ecouteur;
    }

    /**
     * Remplace les notes affichées et rafraîchit la liste.
     *
     * @param nouvellesNotes les notes à afficher
     */
    public void mettreAJour(List<Note> nouvellesNotes) {
        notes.clear();
        notes.addAll(nouvellesNotes);
        notifyDataSetChanged();
    }

    /**
     * Crée la vue d'une ligne de la liste.
     *
     * @param parent   la liste qui contiendra la ligne
     * @param viewType type de vue (non utilisé)
     * @return le ViewHolder de la nouvelle ligne
     */
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vue = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(vue);
    }

    /**
     * Remplit une ligne avec les informations de la note à la position donnée.
     *
     * @param holder   la ligne à remplir
     * @param position position de la note dans la liste
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.textTitre.setText(note.getTitre());
        holder.textApercu.setText(note.getApercu());
        String date = formatDate.format(new Date(note.getDateModification()));
        holder.textDate.setText(holder.itemView.getContext()
                .getString(R.string.date_modification, date));
        holder.itemView.setOnClickListener(v -> ecouteur.onNoteClick(note));
    }

    /**
     * @return le nombre de notes affichées
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Conserve les références aux vues d'une ligne de la liste.
     */
    static class NoteViewHolder extends RecyclerView.ViewHolder {

        /** Affiche le nom de la note. */
        final TextView textTitre;

        /** Affiche l'aperçu de la note. */
        final TextView textApercu;

        /** Affiche la date de dernière modification. */
        final TextView textDate;

        /**
         * @param vue la vue d'une ligne (item_note.xml)
         */
        NoteViewHolder(@NonNull View vue) {
            super(vue);
            textTitre = vue.findViewById(R.id.text_view_note_title);
            textApercu = vue.findViewById(R.id.text_view_note_preview);
            textDate = vue.findViewById(R.id.text_view_note_date);
        }
    }
}