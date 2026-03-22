module ca.qc.bdeb.sim.tp1_photorama {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    requires jdk.jfr;


    opens ca.qc.bdeb.sim.tp1_photorama to javafx.fxml;
    exports ca.qc.bdeb.sim.tp1_photorama;
}