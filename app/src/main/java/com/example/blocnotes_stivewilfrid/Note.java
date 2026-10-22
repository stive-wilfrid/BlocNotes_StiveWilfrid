package com.example.blocnotes_stivewilfrid;

/**
 * Représente une note du bloc-notes : un nom, un texte et la date
 * de sa dernière modification.
 *
 * @author Stive Wilfrid
 */
public class Note {

    /** Nombre maximal de caractères affichés dans l'aperçu de la note. */
    public static final int LONGUEUR_APERCU = 30;

    /** Identifiant unique de la note (moment de sa création, en millisecondes). */
    private final long id;

    /** Nom (titre) de la note. */
    private String titre;

    /** Texte complet de la note. */
    private String contenu;

    /** Date de la dernière modification, en millisecondes depuis 1970. */
    private long dateModification;

    /**
     * Construit une note.
     *
     * @param id               identifiant unique de la note
     * @param titre            nom de la note
     * @param contenu          texte de la note
     * @param dateModification date de la dernière modification (millisecondes)
     */
    public Note(long id, String titre, String contenu, long dateModification) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.dateModification = dateModification;
    }

    /**
     * @return l'identifiant unique de la note
     */
    public long getId() {
        return id;
    }

    /**
     * @return le nom de la note
     */
    public String getTitre() {
        return titre;
    }

    /**
     * @param titre le nouveau nom de la note
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * @return le texte complet de la note
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * @param contenu le nouveau texte de la note
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    /**
     * @return la date de la dernière modification (millisecondes)
     */
    public long getDateModification() {
        return dateModification;
    }

    /**
     * @param dateModification la nouvelle date de modification (millisecondes)
     */
    public void setDateModification(long dateModification) {
        this.dateModification = dateModification;
    }

    /**
     * Retourne un aperçu de la note : sa première ligne, limitée à
     * {@link #LONGUEUR_APERCU} caractères.
     *
     * @return l'aperçu de la note
     */
    public String getApercu() {
        String premiereLigne = contenu.split("\n", 2)[0];
        if (premiereLigne.length() > LONGUEUR_APERCU) {
            return premiereLigne.substring(0, LONGUEUR_APERCU) + "…";
        }
        return premiereLigne;
    }
}

