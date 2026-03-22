package ca.qc.bdeb.sim.tp1_photorama;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainCmd {
    public static void main(String[] args) {
        try {
            Gallerie gallerie = new Gallerie();

            //1. Détection de doublons avec l’Algo 1
            System.out.println("=== 1. Test des différences de pixels ===");

            afficherResulatsPixels(new ComparateurImagesPixels(5, 20), "debogage/pixels1.png", "debogage/pixels2.png");
            afficherResulatsPixels(new ComparateurImagesPixels(5, 20), "debogage/pixels1.png", "debogage/pixels3.png");
            afficherResulatsPixels(new ComparateurImagesPixels(5, 40), "debogage/pixels1.png", "debogage/pixels3.png");
            afficherResulatsPixels(new ComparateurImagesPixels(5, 20), "debogage/pixels1.png", "debogage/pixels4.png");
            afficherResulatsPixels(new ComparateurImagesPixels(128, 49), "debogage/pixels1.png", "debogage/pixels4.png");
            afficherResulatsPixels(new ComparateurImagesPixels(128, 50), "debogage/pixels1.png", "debogage/pixels4.png");
            afficherResulatsPixels(new ComparateurImagesPixels(128, 51), "debogage/pixels1.png", "debogage/pixels4.png");
            //2. Liste textuelle des doublons trouvés par hachage(algo 1 et 2)
            System.out.println("=== 2. Affichage des valeurs de hachage ===");
            //hachage avec la moyennne
            hachageDebogage("debogage/moyenne-test1.png", new ComparateurImagesHachageMoyenne(), 8, 8);
            hachageDebogage("debogage/moyenne-test2.png", new ComparateurImagesHachageMoyenne(), 8, 8);
            hachageDebogage("debogage/moyenne-test3.png", new ComparateurImagesHachageMoyenne(), 8, 8);
            //hachage avec les différences
            hachageDebogage("debogage/diff-test1.png", new ComparateurImagesHachageDifference(), 8, 9);
            hachageDebogage("debogage/diff-test2.png", new ComparateurImagesHachageDifference(), 8, 9);
            hachageDebogage("debogage/diff-test3.png", new ComparateurImagesHachageDifference(), 8, 9);

            //Au lieu de donner tous les paramètres, créer un constructeur
            System.out.println("=== 3. Analyse de la qualité des différents algos pour airbnb-petit ===");
            System.out.println("Algo: Comparateur Pixels  (seuil différences=8, pourcentage différences max=0.2)");
            afficherDoublons(new ComparateurImagesPixels(8, 20), gallerie);
            System.out.println("Algo: Comparateur Pixels  (seuil différences=20, pourcentage différences max=0.5)");
            afficherDoublons(new ComparateurImagesPixels(20, 50), gallerie);
            System.out.println("Algo: Comparateur Hachage Moyenne (cases différentes max=8)");
            afficherDoublons(new ComparateurImagesHachageMoyenne(8), gallerie);
            System.out.println("Algo: Comparateur Hachage Moyenne (cases différentes max=16)");
            afficherDoublons(new ComparateurImagesHachageMoyenne(16), gallerie);
            System.out.println("Algo: Comparateur Hachage Différences (cases différentes max=8)");
            afficherDoublons(new ComparateurImagesHachageDifference(8), gallerie);
            System.out.println("Algo: Comparateur Hachage Différences (cases différentes max=16)");
            afficherDoublons(new ComparateurImagesHachageDifference(16), gallerie);
        } catch (IOException e) {
            System.out.println("Erreur lors du choix d'images ");
            Platform.exit();
        }
    }

    public static void afficherDoublons(ComparateurImages comp, Gallerie gallerie) throws IOException {

        gallerie.setComparateur(comp);
        ArrayList<ArrayList<String>> groupeDoublons = gallerie.groupeDoublons(gallerie.listeImagesChemin("airbnb-petit"));

        for (int i = 0; i < groupeDoublons.size(); i++) {
            System.out.print("[" + (i + 1) + "]");
            for (String nomFichier : groupeDoublons.get(i)) {
                //Extrait juste le nom de l'image
                String nom = new File(nomFichier).getName();
                System.out.print(nom + " ");
            }
            System.out.println();
        }
    }

    public static void afficherResulatsPixels(ComparateurImagesPixels compPixels, String pixels1, String pixels2) throws IOException {
        String similaire;
        if (compPixels.imagesSimilaires(pixels1, pixels2)) {
            similaire = "SIMILAIRE";
        } else {
            similaire = "DIFFÉRENT";
        }
        System.out.println("seuil=" + compPixels.getSeuilMaxDiff() + " ,max pourcent=" + compPixels.getPourcentageMaxDiff() / 100 + ": " + pixels1 + " et " + pixels2 + " " + similaire);
    }

    public static void hachageDebogage(String lien, ComparateurImagesHachage compHachage, int taille1, int taille2) throws IOException {
        int[][] imageReduite = GestionnaireImages.toPixels(GestionnaireImages.redimensionner(GestionnaireImages.lireImage(lien), taille1, taille2));
        int[][] imageHachage = compHachage.hachage(imageReduite);

        System.out.println(lien);
        for (int[] ints : imageHachage) {
            for (int j = 0; j < imageHachage[0].length; j++) {

                if (ints[j] == 1) {
                    System.out.print("1");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
