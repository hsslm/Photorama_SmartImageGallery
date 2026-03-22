package ca.qc.bdeb.sim.tp1_photorama;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Gallerie {

    private ComparateurImages comparateur = new ComparateurImagesPixels();
    private String dossier;

    public void setDossier(String dossier) {
        this.dossier = dossier;
    }

    public ComparateurImages getComparateur() {
        return comparateur;
    }

    public void setComparateur(ComparateurImages comparateur) {
        this.comparateur = comparateur;
    }

    /// Méthode pour lister tous les fichiers image dans un dossier donné
    public ArrayList<String> listeImagesChemin(String dossier) throws IOException {

        var imagesChemins = new ArrayList<String>();
        var repertoire = new File(dossier);

        //Vérifie si le dossier est valide
        if (!repertoire.isDirectory() || repertoire.listFiles() == null) {
            throw new FileNotFoundException(dossier + " n'est pas un répertoire ou est inaccessible");
        }

        //Parcours tous les fichiers du dossier
        for (var fichier : repertoire.listFiles()) {
            var nomFichier = fichier.getName().toLowerCase();

            //Vérifie si le fichier est une image (jpg ou png)
            if ((nomFichier.endsWith(".jpg") || nomFichier.endsWith(".png")) && fichier.isFile()) {
                GestionnaireImages.lireImage(fichier.getAbsolutePath());
                imagesChemins.add(fichier.getAbsolutePath());
            }
        }

        Collections.sort(imagesChemins);

        if (imagesChemins.isEmpty()) {
            throw new IOException();
        }

        return imagesChemins;

    }

    /// Méthode pour regrouper les doublons d'images en fonction de leur similarité
    public ArrayList<ArrayList<String>> groupeDoublons(ArrayList<String> listeCheminsImages) throws IOException {

        ArrayList<ArrayList<String>> tousLesGroupes = new ArrayList<>();

        Collections.sort(listeCheminsImages);

        while (!listeCheminsImages.isEmpty()) {
            //Sélectionne la première image comme référence
            String imageDeReference = listeCheminsImages.get(0);
            ArrayList<String> groupeSimilaire = new ArrayList<>();
            //Liste pour stocker les images à supprimer de la liste principale
            ArrayList<String> imagesAEnlever = new ArrayList<>();

            groupeSimilaire.add(imageDeReference);
            /*
            *J’avais un problème : chaque doublon était mis dans une nouvelle liste séparée
                *J'ai demandé à chatGPT
                *Suggestion reçue : créer une liste temporaire "imagesAEnlever" pour stocker les doublons trouvés,
                puis les retirer de la liste principale après la boucle.
             */
            imagesAEnlever.add(imageDeReference);

            //Compare l'image de référence avec toutes les autres images de la liste
            for (int i = 1; i < listeCheminsImages.size(); i++) {
                String imageAComparer = listeCheminsImages.get(i);
                boolean estSimilaire = comparateur.imagesSimilaires(imageDeReference, imageAComparer);
                if (estSimilaire) {
                    groupeSimilaire.add(imageAComparer);
                    imagesAEnlever.add(imageAComparer);
                }
            }

            listeCheminsImages.removeAll(imagesAEnlever);
            tousLesGroupes.add(groupeSimilaire);
        }

        return tousLesGroupes;
    }
}

