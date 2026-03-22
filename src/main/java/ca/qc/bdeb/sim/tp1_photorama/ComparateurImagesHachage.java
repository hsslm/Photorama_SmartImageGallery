package ca.qc.bdeb.sim.tp1_photorama;

import java.io.IOException;

public abstract class ComparateurImagesHachage extends ComparateurImages {

    protected int[][] imageHachage1;
    protected int[][] imageHachage2;

    protected int tolerance;

    public ComparateurImagesHachage() {
    }

    public ComparateurImagesHachage(int tolerance) {
        this.tolerance = tolerance;
    }

    public void setTolerance(int tolerance) {
        this.tolerance = tolerance;

    }

    public abstract boolean imagesSimilaires(String chemin1, String chemin2) throws IOException;

    public abstract int[][] hachage(int[][] tab);

    //Méthode commune entre les algo de hachage : Compte le nombre de cases différentes et le compare à la tolérance max
    public boolean doublons(int[][] imageHachage1, int[][] imageHachage2) {

        int nombresDeCasesDiff = 0;
        for (int i = 0; i < imageHachage1.length; i++) {
            for (int j = 0; j < imageHachage1[i].length; j++) {

                if (imageHachage1[i][j] != imageHachage2[i][j]) {
                    nombresDeCasesDiff++;
                }

            }
        }

        return nombresDeCasesDiff <= tolerance;

    }

}


