# Bloc-Notes

Éditeur de texte de style bloc-notes pour appareil mobile Android.

## Créateur

**Stive Wilfrid** — Cégep de Drummondville

## Licence

Ce projet est distribué sous la licence **MIT**.

Copyright (c) 2026 Stive Wilfrid

Permission est accordée, gratuitement, à toute personne obtenant une copie de ce
logiciel et des fichiers de documentation associés, de l'utiliser, le copier, le
modifier, le fusionner, le publier, le distribuer, le sous-licencier et/ou le
vendre, à condition que l'avis de copyright ci-dessus et le présent avis de
permission soient inclus dans toutes les copies ou parties importantes du logiciel.

LE LOGICIEL EST FOURNI « TEL QUEL », SANS GARANTIE D'AUCUNE SORTE, EXPLICITE OU
IMPLICITE.

## Description

Bloc-Notes est une application Android écrite en Java avec les librairies Android
officielles. Elle permet de :

- **Voir la liste des notes** à l'ouverture de l'application, de la plus récemment
  modifiée à la plus ancienne. Chaque note affiche son nom, un aperçu (première
  ligne, 30 caractères au maximum) et la date de sa dernière modification.
- **Créer une nouvelle note** avec le bouton **+**.
- **Modifier une note** en la touchant dans la liste, puis l'**enregistrer**.
- **Annuler** les dernières modifications (undo) et **refaire** les modifications
  annulées (redo).
- **Insérer la position actuelle** (latitude et longitude) dans le texte avec le
  bouton **+ GPS**, grâce aux services de localisation GPS/GNSS d'Android.
- **Importer un texte** d'une autre application pour créer une nouvelle note :
  dans l'autre application, choisir **Partager**, puis **Bloc-Notes**.
- **Partager une note** avec une autre application (ou avec Bloc-Notes, ce qui
  crée une copie de la note).

Les notes sont sauvegardées dans un fichier JSON du stockage interne de
l'application. Elles sont donc conservées après la fermeture de l'application.

## Structure du projet

| Fichier | Rôle |
|---|---|
| `MainActivity.java` | Écran principal : liste des notes et bouton de création |
| `EditNoteActivity.java` | Page d'édition : enregistrer, annuler/refaire, GPS, partage et importation |
| `Note.java` | Modèle d'une note (nom, contenu, date de modification) |
| `GestionnaireNotes.java` | Sauvegarde et chargement des notes (fichier JSON) |
| `NoteAdapter.java` | Affichage des notes dans la liste (RecyclerView) |
| `res/layout/` | Interfaces : `activity_main.xml`, `activity_edit_note.xml`, `item_note.xml` |

## Compilation et exécution

### Prérequis

- **Android Studio** (version récente)
- Le **SDK Android** installé avec Android Studio
- Un appareil Android **7.0 (API 24) ou plus récent**, réel ou émulé

### Étapes

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/nkeuvardy-ai/BlocNotes_StiveWilfrid.git
   ```
2. Ouvrir Android Studio, puis **File → Open** et choisir le dossier
   `BlocNotes_StiveWilfrid`.
3. Attendre la fin de la synchronisation Gradle (barre de progression en bas).
4. Choisir l'appareil dans la liste en haut :
    - **Téléphone réel** : activer les *Options pour les développeurs* et le
      *Débogage USB*, puis brancher le téléphone ;
    - **Émulateur** : en créer un avec **Device Manager**.
5. Cliquer sur **Run ▶** (ou **Maj + F10**).

### Utiliser le GPS

- La première fois que vous appuyez sur **+ GPS**, l'application demande la
  permission d'accéder à la position : choisir **Autoriser**.
- La localisation du téléphone doit être activée.
- Sur un **émulateur**, définir une position dans **Extended Controls (⋯) →
  Location**, puis cliquer sur **Set Location**.