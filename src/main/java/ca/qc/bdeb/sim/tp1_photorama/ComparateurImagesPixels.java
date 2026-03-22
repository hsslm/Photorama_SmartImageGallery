package ca.qc.bdeb.sim.tp1_photorama;

import java.io.IOException;


public class ComparateurImagesPixels extends ComparateurImages {


    private double seuilMaxDiff;
    private double pourcentageMaxDiff;

    public ComparateurImagesPixels() {
    }

    public ComparateurImagesPixels(double seuilMaxDiff, double pourcentageMaxDiff) {

        this.seuilMaxDiff = seuilMaxDiff;
        this.pourcentageMaxDiff = pourcentageMaxDiff;
    }

    public void setSeuilMaxDiff(double seuilMaxDiff) {
        this.seuilMaxDiff = seuilMaxDiff;

    }

    public double getSeuilMaxDiff() {
        return seuilMaxDiff;
    }

    public double getPourcentageMaxDiff() {
        return pourcentageMaxDiff;
    }

    public void setPourcentageMaxDiff(double pourcentageMaxDiff) {
        this.pourcentageMaxDiff = pourcentageMaxDiff;
    }


    @Override
    public boolean imagesSimilaires(String chemin1, String chemin2) throws IOException {

        //1.Convertir les 2 images en pixels
        int[][] tabEnPixels1 = GestionnaireImages.toPixels(GestionnaireImages.lireImage(chemin1));
        int[][] tabEnPixels2 = GestionnaireImages.toPixels(GestionnaireImages.lireImage(chemin2));


        //Si leur taille n'est pas pareil, elles ne sont pas similaires
        if (tabEnPixels1.length == tabEnPixels2.length && tabEnPixels1[0].length == tabEnPixels2[0].length) {

            double nombreDePixelsDiff = 0;
            double totalPixels = 0;

            //boucle comparant les 2 tableaux et trouvant les pixels différents
            for (int i = 0; i < tabEnPixels1.length; i++) {
                for (int j = 0; j < tabEnPixels1[i].length; j++) {

                    totalPixels++;
                    if ((Math.abs(tabEnPixels1[i][j] - tabEnPixels2[i][j])) > seuilMaxDiff) {
                        nombreDePixelsDiff++;
                    }

                }
            }


            //trouver le pourcentage de différence des pixels
            double pourcentageDiff = (nombreDePixelsDiff * 100) / totalPixels;

            //si le pourcentage max est respecté ce sont des doublons (supposé donner true)
            return pourcentageDiff <= pourcentageMaxDiff;


        } else {
            return false;
        }

    }
}
