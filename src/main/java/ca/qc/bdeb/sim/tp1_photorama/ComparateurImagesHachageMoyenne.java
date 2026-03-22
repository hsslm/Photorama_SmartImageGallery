package ca.qc.bdeb.sim.tp1_photorama;

import java.io.IOException;

public class ComparateurImagesHachageMoyenne extends ComparateurImagesHachage {

    public ComparateurImagesHachageMoyenne() {
    }

    public ComparateurImagesHachageMoyenne(int tolerance) {
        super(tolerance);
    }

    public void setTolerance(int tolerance) {
        super.setTolerance(tolerance);
    }

    @Override
    public boolean imagesSimilaires(String chemin1, String chemin2) throws IOException {


        //1. Redimensionner les images en 8x8
        int[][] imageReduite1 = GestionnaireImages.toPixels(GestionnaireImages.redimensionner(GestionnaireImages.lireImage(chemin1), 8, 8));
        int[][] imageReduite2 = GestionnaireImages.toPixels(GestionnaireImages.redimensionner(GestionnaireImages.lireImage(chemin2), 8, 8));


        //2. Moyenne et hachage

        imageHachage1 = hachage(imageReduite1);
        imageHachage2 = hachage(imageReduite2);

        //3. Comparaison des 2 (Si nombreDeCasesDiff est plus grand que la tolérance, ils ne sont pas similaire)

        return doublons(imageHachage1, imageHachage2);


    }


    @Override
    public int[][] hachage(int[][] tab) {

        //A. Faire la moyenne des pixels du tableau
        double sommeTotal = 0;
        double nombreTotalPixels = 0;

        for (int[] ligne : tab) {
            for (int pixel : ligne) {

                nombreTotalPixels++;
                sommeTotal += pixel;

            }
        }


        double moyenne = sommeTotal / nombreTotalPixels;


        //B. Hachage (si pixel <= moyenne --> pixel = 0, sinon pixel = 1)
        int[][] hachage = new int[8][8];


        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {

                if (tab[i][j] <= moyenne) {
                    hachage[i][j] = 0;
                } else {
                    hachage[i][j] = 1;
                }

            }

        }
        return hachage;
    }
}
