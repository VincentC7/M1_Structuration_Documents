package fr.ul.miage.bibliuniv.controllers;

import fr.ul.miage.bibliuniv.Main;
import fr.ul.miage.bibliuniv.database.DAO.UtilisateursDAO;
import fr.ul.miage.bibliuniv.database.model.Utilisateurs;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    public TextField input_login;
    public Label login_error;

    public void login(ActionEvent actionEvent) throws IOException {
        String login = input_login.getText();
        UtilisateursDAO utilisateursDAO = new UtilisateursDAO();
        Utilisateurs current_user = utilisateursDAO.findByLogin(login);
        if (current_user == null){
            login_error.setVisible(true);
        }else{
            Main.setUtilisateur(current_user);
            Parent root = FXMLLoader.load(Main.class.getResource("vues/window.fxml"));
            Stage window = (Stage) input_login.getScene().getWindow();
            window.setScene(new Scene(root));
        }
    }
}
