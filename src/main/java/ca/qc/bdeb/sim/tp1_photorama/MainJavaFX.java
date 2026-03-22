package ca.qc.bdeb.sim.tp1_photorama;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainJavaFX extends Application {

    private Gallerie gallerie = new Gallerie();

    private ImageView polaroid;
    //Tolérance par défaut
    private String tolerance = "Faible";
    private boolean dossierValide;
    private ImageView imagePrincipale;

    @Override
    public void start(Stage stage) {

        var root = new BorderPane();
        root.setStyle("-fx-background-color: pink");
        var scene = new Scene(root, 900, 600);

        //Quitter avec la touche Esc
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) Platform.exit();
        });

        //S'assurer des paramètres par défaut
        appliquerTolerance("Faible", 20, 10, 10);

        ///**Côté gauche**
        var coteGauche = new VBox(20);
        coteGauche.setPadding(new Insets(30));
        coteGauche.setPrefWidth(250);

        //Logo + titre principal
        var photorama = creerTexte("Photorama", 30);
        var logo = new ImageView(new Image("logo.png"));
        logo.setFitHeight(40);
        logo.setFitWidth(40);
        logo.setPreserveRatio(true);
        var titreEtLogo = new HBox(10, logo, photorama);

        //Bloc des interactions utilisateur
        var interactionsUtilisateur = new VBox(5);

        //3 choix d'algo possible(pixels,hachage moyen,hachage diff)
        var choixAlgo = new ChoiceBox<String>();
        choixAlgo.setPrefWidth(150);
        choixAlgo.getItems().addAll("Pixels", "Hachage (Moyenne)", "Hachage (Différences)");
        //Tolérance par défaut
        choixAlgo.setValue("Pixels");
        choixAlgo.setOnAction(e -> choixAlgoChangement(choixAlgo.getValue()));

        //2 choix de tolérance possible (faible ou élévé)
        var faible = new RadioButton("Faible");
        var eleve = new RadioButton("Élevée");
        var groupe = new ToggleGroup();
        faible.setToggleGroup(groupe);
        eleve.setToggleGroup(groupe);
        //Choix par défault
        faible.setSelected(true);
        faible.setOnAction(e -> appliquerTolerance("Faible", 20, 10, 10));
        eleve.setOnAction(e -> appliquerTolerance("Élevée", 30, 40, 15));
        var choixTolerance = new HBox(15, faible, eleve);

        //Processus du boutton "Ouvrir dossier" qui provoque la création de l'interface de droite
        var ouvrirDossier = new Button("Ouvrir un dossier");
        ouvrirDossier.setOnAction(e -> gererOuvertureDossier(stage, root));

        interactionsUtilisateur.getChildren().addAll(creerTexte("Ouvrir une gallerie:", 20), choixAlgo, creerTexte("Tolérance aux différences:", 15), choixTolerance, ouvrirDossier);
        coteGauche.getChildren().addAll(titreEtLogo, interactionsUtilisateur);

        ///**Côté droit avant utilisation du boutton "Ouvrir dossier" (coté droit par défaut)**
        var colonnePolaroid = new VBox();
        colonnePolaroid.setAlignment(Pos.TOP_CENTER);
        colonnePolaroid.setPadding(new Insets(30, 150, 150, 0));
        colonnePolaroid.setPrefWidth(350);

        polaroid = new ImageView(new Image("polaroid.png"));
        polaroid.setFitWidth(300);
        polaroid.setFitHeight(300);
        polaroid.setPreserveRatio(true);
        colonnePolaroid.getChildren().add(polaroid);

        imagePrincipale = creerImagePrincipale();

        root.setLeft(coteGauche);
        root.setRight(colonnePolaroid);

        stage.setTitle("Photorama");
        stage.getIcons().add(new Image("logo.png"));
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Crée un texte avec les paramètres choisis
     */
    public Text creerTexte(String contenu, int taille) {
        var texte = new Text(contenu);
        texte.setFont(Font.font("Arial", taille));
        return texte;
    }

    /**
     * Applique la tolérance choisie
     */
    public void appliquerTolerance(String type, int seuil, double pourcent, int cases) {
        tolerance = type;
        if (gallerie.getComparateur() instanceof ComparateurImagesPixels c) {
            c.setSeuilMaxDiff(seuil);
            c.setPourcentageMaxDiff(pourcent);
        } else if (gallerie.getComparateur() instanceof ComparateurImagesHachage c) {
            c.setTolerance(cases);
        }
    }

    /**
     * Affiche un message quand un dossier est choisi (vert pour valide, rouge pour erreur)
     */
    public void afficherMessage(BorderPane root, String texte, String couleurFond) {
        var message = new Text(texte);
        message.setFont(Font.font("Arial", 15));
        message.setFill(Color.BLACK);
        var fond = new StackPane(message);
        fond.setStyle("-fx-background-color: " + couleurFond + "; -fx-padding: 10;");
        fond.setPrefHeight(40);
        root.setBottom(fond);
    }

    /*** Change l’algorithme de comparaison selon le choix de l’utilisateur**/
    public void choixAlgoChangement(String choix) {
        switch (choix) {
            case "Pixels":
                gallerie.setComparateur(new ComparateurImagesPixels());
                break;
            case "Hachage (Moyenne)":
                gallerie.setComparateur(new ComparateurImagesHachageMoyenne());
                break;
            case "Hachage (Différences)":
                gallerie.setComparateur(new ComparateurImagesHachageDifference());
                break;
        }
        //Réappliquer la tolérance pour s'assurer (j'avais un bug avec la tolérance et c'était le seul moyen de le réglé)
        if (tolerance.equals("Faible")) {
            appliquerTolerance("Faible", 20, 10, 10);
        } else {
            appliquerTolerance("Élevée", 30, 40, 15);
        }
    }

    /**
     * Ouvre une boîte de dialogue pour sélectionner un dossier à* ouvrir dans l'application*
     *
     * @param stage Le stage qui contrôle la fenêtre de dialogue* (l' objet passé en paramètre à la fonction start())
     * @return Le chemin du dossier choisi, ou null si l'ouverture a été annulée
     */
    public String choisirDossier(Stage stage) {
        DirectoryChooser selecteur = new DirectoryChooser();
        selecteur.setTitle("Sélectionnez un dossier d'images");
        selecteur.setInitialDirectory(new File("."));
        File dossierChoisi = selecteur.showDialog(stage);
        if (dossierChoisi != null) {
            String chemin = dossierChoisi.getAbsolutePath();
            return chemin;
        }
        return null;
    }


    /**
     * Gestion de l’ouverture d’un dossier (incluant les exceptions)
     */
    public void gererOuvertureDossier(Stage stage, BorderPane root) {
        String chemin = choisirDossier(stage);
        //Deuxième vérification de la validité du dossier
        if (chemin == null) return;
        try {
            gallerie.setDossier(chemin);
            ArrayList<String> listeImagesChemin = gallerie.listeImagesChemin(chemin);

            //Calcule groupe pour connaitre valeur de N
            var groupesDoublons = gallerie.groupeDoublons(listeImagesChemin);
            //Remplacement du polaroi
            polaroid.setVisible(false);
            dossierValide = true;
            afficherMessage(root, "Dossier valide", "GREEN");
            construireInterfaceDroite(root, groupesDoublons);
        } catch (IOException e) {
            afficherMessage(root, "Erreur lors de l'ouverture de la gallerie d'images", "RED");
        }
    }

    /**
     * Construit la partie droite après chargement du dossier
     **/

    public void construireInterfaceDroite(BorderPane root, ArrayList<ArrayList<String>> groupesDoublons) throws IOException {
        //Si aucun dossier n’est valide, on ne construit rien
        var coteDroit = new VBox();
        if (!dossierValide) {
            afficherMessage(root, "Dossier non-valide", "RED");
        } else {
            var zoneImage = new StackPane(imagePrincipale);
            zoneImage.setAlignment(Pos.CENTER);
            zoneImage.setMinHeight(320);
            zoneImage.setStyle("-fx-background-color: white; -fx-border-color: fuchsia;");

            var mesPhotosN = creerTexte("Mes photos (" + groupesDoublons.size() + ")", 20);
            var miniaturesPrincipales = new HBox(5);
            miniaturesPrincipales.setPrefWidth(100);
            miniaturesPrincipales.setPrefHeight(100);
            miniaturesPrincipales.setAlignment(Pos.CENTER_LEFT);

            var defilementMiniatures = new ScrollPane(miniaturesPrincipales);
            defilementMiniatures.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            defilementMiniatures.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            defilementMiniatures.setPrefHeight(120);
            defilementMiniatures.setFitToHeight(true);

            var doublonsAffiches = new HBox(10);
            doublonsAffiches.setAlignment(Pos.CENTER_LEFT);
            doublonsAffiches.setPrefWidth(50);
            doublonsAffiches.setPrefHeight(80);
            //  doublonsAffiches.setPadding(new Insets());

            var defilementDoublons = new ScrollPane(doublonsAffiches);
            defilementDoublons.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            defilementDoublons.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            defilementDoublons.setFitToHeight(true);

            //Génèration des miniatures
            for (var groupe : groupesDoublons) {
                var conteneurMiniature = creerMiniatureGroupe(groupe, doublonsAffiches);
                miniaturesPrincipales.getChildren().add(conteneurMiniature);
            }


            //Affichage du premier groupe(par défaut)
            if (!groupesDoublons.isEmpty()) {
                afficherGroupeDoublons(groupesDoublons.get(0), doublonsAffiches);
                imagePrincipale.setImage(new Image("file:" + groupesDoublons.get(0).get(0)));
            }

            //Rafraîchit le côté droit
            coteDroit.getChildren().clear();
            coteDroit.setPadding(new Insets(20));
            coteDroit.getChildren().setAll(zoneImage, mesPhotosN, defilementMiniatures, defilementDoublons);
        }
        root.setRight(coteDroit);
    }
    /// Méthodes permettant d'alléger la création de l'intérface de droite

    /**
     * Crée une image principale générale
     **/
    public ImageView creerImagePrincipale() {
        var img = new ImageView();
        img.setPreserveRatio(true);
        img.setFitWidth(400);
        img.setFitHeight(300);
        return img;
    }

    /**
     * Crée une miniature cliquable représentant un groupe de doublons
     */
    public VBox creerMiniatureGroupe(ArrayList<String> groupe, HBox doublonsAffiches) {

        var miniature = creerImageView(groupe.get(0), 80, 80);
        var conteneur = new VBox(miniature);
        conteneur.setAlignment(Pos.CENTER);
        conteneur.setPadding(new Insets(5));

        miniature.setOnMouseClicked(e -> {
            gererClicMiniature(groupe, doublonsAffiches);
        });
        return conteneur;
    }

    public void gererClicMiniature(ArrayList<String> groupe, HBox doublonsAffiches) {
        doublonsAffiches.getChildren().clear();
        //Affiche la première image du groupe
        imagePrincipale.setImage(new Image("file:" + groupe.get(0)));
        //Affiche tous les doublons si il y en a (une seule image dans un groupe n'a pas de doublons)
        if (groupe.size() > 1) {
            afficherGroupeDoublons(groupe, doublonsAffiches);
        }
    }

    /**
     * Affiche tous les doublons d’un groupe sélectionné ( avec la souris)
     */
    public void afficherGroupeDoublons(ArrayList<String> groupe, HBox doublonsAffiches) {

        doublonsAffiches.getChildren().clear();
        for (var chemin : groupe) {
            var doublon = creerImageView(chemin, 60, 60);
            doublon.setOnMouseClicked(e -> {
                imagePrincipale.setImage(new Image("file:" + chemin));
            });
            var conteneur = new VBox(doublon);
            conteneur.setAlignment(Pos.CENTER);
            conteneur.setPadding(new Insets(5));
            doublonsAffiches.getChildren().add(conteneur);

        }
    }

    /**
     * Crée une ImageView générale
     **/
    public ImageView creerImageView(String chemin, int w, int h) {
        var img = new ImageView(new Image("file:" + chemin));
        img.setPreserveRatio(true);
        img.setFitWidth(w);
        img.setFitHeight(h);
        return img;
    }


    public static void main(String[] args) {
        launch();
    }
}

