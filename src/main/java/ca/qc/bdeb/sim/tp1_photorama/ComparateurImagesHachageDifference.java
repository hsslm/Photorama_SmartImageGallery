package ca.qc.bdeb.sim.tp1_photorama;

import java.io.IOException;

public class ComparateurImagesHachageDifference extends ComparateurImagesHachage {

    public ComparateurImagesHachageDifference() {
    }

    public ComparateurImagesHachageDifference(int tolerance) {
        super(tolerance);
    }

    public void setTolerance(int tolerance) {
        super.setTolerance(tolerance);
    }

    @Override
    public boolean imagesSimilaires(String chemin1, String chemin2) throws IOException {

        //1. Redimensionner les images en 8x9
        int[][] imageReduite1 = GestionnaireImages.toPixels(GestionnaireImages.redimensionner(GestionnaireImages.lireImage(chemin1), 8, 9));
        int[][] imageReduite2 = GestionnaireImages.toPixels(GestionnaireImages.redimensionner(GestionnaireImages.lireImage(chemin2), 8, 9));

        //2. Hachage des images (si pixels <= que son voisin d'en bas --> pixel = 0, sinon pixel = 1)
        imageHachage1 = hachage(imageReduite1);
        imageHachage2 = hachage(imageReduite2);

        //3. Comparaison des 2 (Si nombreDeCasesDiff est plus grand que la tolérance, ils ne sont pas similaire)
        return doublons(imageHachage1, imageHachage2);
    }

    @Override
    public int[][] hachage(int[][] tab) {

        int[][] hachage = new int[8][8];

        for (int i = 0; i < tab.length - 1; i++) {
            for (int j = 0; j < tab[0].length; j++) {

                if (tab[i][j] <= tab[i + 1][j]) {
                    hachage[i][j] = 0;
                } else {
                    hachage[i][j] = 1;
                }

            }

        }

        return hachage;
    }
}
