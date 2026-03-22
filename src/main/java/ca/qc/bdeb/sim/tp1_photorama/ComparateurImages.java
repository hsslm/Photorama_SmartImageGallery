package ca.qc.bdeb.sim.tp1_photorama;

import java.io.IOException;

public abstract class ComparateurImages {

    public abstract boolean imagesSimilaires(String chemin1, String chemin2) throws IOException;
}
