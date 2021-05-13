package fr.ul.miage.bibliuniv;

import fr.ul.miage.bibliuniv.database.DAO.UtilisateursDAO;
import fr.ul.miage.bibliuniv.database.UtilDatabase;
import fr.ul.miage.bibliuniv.database.model.Utilisateurs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static Utilisateurs utilisateur;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Structuration de documents : BibliUniv");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("vues/window.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(IOException e) {
            LOG.severe("L'application a rencontré des erreurs lors du lancement");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UtilDatabase.readFile();
        launch(args);
    }

    public static Utilisateurs getUtilisateur() {
        UtilisateursDAO utilisateursDAO = new UtilisateursDAO();
        Utilisateurs u = utilisateursDAO.findByLogin("McdonnellC");
        return u;
    }

    public static void setUtilisateur(Utilisateurs utilisateur) {
        Main.utilisateur = utilisateur;
    }
}
