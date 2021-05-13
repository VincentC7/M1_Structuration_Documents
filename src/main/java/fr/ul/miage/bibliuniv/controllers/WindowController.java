package fr.ul.miage.bibliuniv.controllers;

import fr.ul.miage.bibliuniv.Main;
import fr.ul.miage.bibliuniv.database.DAO.*;
import fr.ul.miage.bibliuniv.database.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.bson.types.ObjectId;

import java.util.HashSet;
import java.util.List;

public class WindowController {


    public TextField input_searchbar;
    public ComboBox<String> select_search_type;
    public VBox elements;

    public ScrollPane details_oeuvre;
    public VBox vbox_authors_list;
    public Label label_datepubli;
    public Label label_theme;
    public VBox vbox_formation_list;
    public VBox vbox_univ_list;
    public VBox vbox_role_list;
    public Text text_content;
    public VBox vbox_commentaires;
    public Label oeuvre_title;

    public TextArea text_new_comment;
    public Slider slider_note;
    public AnchorPane comment_zone;
    public Label label_already_coment;

    private ObjectId oeuvre_selected;

    @FXML
    public void initialize() {
        elements.setSpacing(7);
    }

    public void find(ActionEvent actionEvent) {
        String search = input_searchbar.getText();
        String search_type = (String) select_search_type.getValue();
        switch (search_type){
            case "Nom":
                find_by_name(search);
                break;
            case "Mots clés":
                find_by_keyword(search);
                break;
            case "Thème":
                find_by_theme(search);
                break;
        }
    }

    private void find_by_name(String search){
        OeuvresDAO oeuvresDAO = new OeuvresDAO();
        List<Oeuvres> oeuvres = oeuvresDAO.findByTitre(search, Main.getUtilisateur());
        display_elements(oeuvres);
    }

    private void find_by_keyword(String search){
        OeuvresDAO oeuvresDAO = new OeuvresDAO();
        List<Oeuvres> oeuvres = oeuvresDAO.findByKeyword(search, Main.getUtilisateur());
        display_elements(oeuvres);
    }

    private void find_by_theme(String search){
        OeuvresDAO oeuvresDAO = new OeuvresDAO();
        List<Oeuvres> oeuvres = oeuvresDAO.findByTheme(search, Main.getUtilisateur());
        display_elements(oeuvres);
    }

    public void display_best(ActionEvent actionEvent) {
        OeuvresDAO oeuvresDAO = new OeuvresDAO();
        List<Oeuvres> oeuvres = oeuvresDAO.findByUtilisateurTOP10Note(Main.getUtilisateur());
        display_elements(oeuvres);
    }

    public void display_recent_com(ActionEvent actionEvent) {
        OeuvresDAO oeuvresDAO = new OeuvresDAO();
        List<Oeuvres> oeuvres = oeuvresDAO.findByUtilisateurLastComment(Main.getUtilisateur());
        display_elements(oeuvres);
    }

    private void display_elements(List<Oeuvres> oeuvres){
        elements.getChildren().clear();
        oeuvre_title.setText("Aucune oeuvre sélectionnée");
        details_oeuvre.setVisible(false);
        oeuvre_selected = null;
        for (Oeuvres oeuvre : oeuvres) {
            HBox_Oeuvre element_pane = new HBox_Oeuvre(oeuvre.get_id());
            element_pane.setAlignment(Pos.CENTER_LEFT);
            element_pane.setStyle("-fx-background-color: #bdc3c7;");
            element_pane.setPrefHeight(60);

            Label title = new Label(oeuvre.getTitre());
            title.setFont(Font.font("",FontWeight.BOLD, 20));
            title.setPadding( new Insets(10,15,10,10));
            element_pane.getChildren().add(title);

            Label publication_date = new Label("publié le : " + oeuvre.getPublication().toString());
            publication_date.setFont(new Font( 15));
            publication_date.setPadding( new Insets(10,15,10,10));
            element_pane.getChildren().add(publication_date);

            StringBuilder authors_str = new StringBuilder("auteurs : ");
            UtilisateursDAO utilisateursDAO = new UtilisateursDAO();
            for (ObjectId auteur : oeuvre.getAuteurs()) {
                Utilisateurs utilisateurs = utilisateursDAO.find(auteur);
                authors_str.append(utilisateurs.getNom()).append(" ").append(utilisateurs.getPrenom()).append(", ");
            }
            Label authors = new Label(authors_str.substring(0, authors_str.length() - 2));
            authors.setFont(new Font( 15));
            authors.setPadding( new Insets(10,15,10,10));
            element_pane.getChildren().add(authors);

            double o_note = oeuvre.getNote();
            Label note = new Label("note moyenne : " + (o_note == -1 ? "non noté" : o_note+"/20"));
            note.setFont(new Font( 15));
            note.setPadding( new Insets(10,15,10,10));
            element_pane.getChildren().add(note);

            elements.getChildren().add(element_pane);
            bind_onclick(element_pane);
        }
    }

    public void bind_onclick(Node element){
        element.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            details_oeuvre.setVisible(true);
            HBox_Oeuvre hBox_oeuvre = (HBox_Oeuvre) mouseEvent.getSource();
            oeuvre_selected = hBox_oeuvre.getOeuvre();
            OeuvresDAO oeuvresDAO = new OeuvresDAO();
            Oeuvres oeuvres = oeuvresDAO.find(hBox_oeuvre.getOeuvre());

            //Titre
            oeuvre_title.setText(oeuvres.getTitre());

            //Auteurs
            vbox_authors_list.getChildren().clear();
            HashSet<ObjectId> autheurs = oeuvres.getAuteurs();
            UtilisateursDAO utilisateursDAO = new UtilisateursDAO();
            for (ObjectId autheur_id : autheurs) {
                Utilisateurs autheur = utilisateursDAO.find(autheur_id);
                Label label = new Label(autheur.getNom()+ " " + autheur.getPrenom() + "    "+ autheur.getRole());
                vbox_authors_list.getChildren().add(label);
            }

            //Date publication
            label_datepubli.setText(oeuvres.getPublication().toString());

            //Theme
            label_theme.setText(oeuvres.getTheme());

            //Formation
            vbox_formation_list.getChildren().clear();
            HashSet<ObjectId> formations = oeuvres.getFormations();
            FormationsDAO formationsDAO = new FormationsDAO();
            for (ObjectId formation_id : formations) {
                Formations formation = formationsDAO.find(formation_id);
                vbox_formation_list.getChildren().add(new Label(formation.getNom()));
            }

            //Univ
            vbox_univ_list.getChildren().clear();
            HashSet<ObjectId> universites = oeuvres.getUniversites();
            UniversitesDAO universitesDAO = new UniversitesDAO();
            for (ObjectId universite_id : universites) {
                Universites universite = universitesDAO.find(universite_id);
                vbox_univ_list.getChildren().add(new Label(universite.getNom()));
            }

            //Roles
            vbox_role_list.getChildren().clear();
            HashSet<Utilisateurs.ROLE> roles = oeuvres.getRoles();
            for (Utilisateurs.ROLE role : roles) {
                vbox_role_list.getChildren().add(new Label(role.name()));
            }

            //Content
            text_content.setText(oeuvres.getContenu());
            text_content.setTextAlignment(TextAlignment.JUSTIFY);

            //Commentaires
            vbox_commentaires.getChildren().clear();
            CommentairesDAO commentairesDAO = new CommentairesDAO();
            List<Commentaires> commentaires = commentairesDAO.findByOeuvre(oeuvres);
            if (commentaires.isEmpty()){
                vbox_commentaires.getChildren().add(new Label("Aucun commentaires"));
                comment_zone.setVisible(true);
                label_already_coment.setVisible(false);
            }else{
                boolean already_comment = false;
                for (Commentaires commentaire : commentaires) {
                    display_commentaire(commentaire);
                    if (commentaire.getAuteur().equals(Main.getUtilisateur().get_id())) already_comment = true;
                }
                if (already_comment) {
                    comment_zone.setVisible(false);
                    label_already_coment.setVisible(true);
                } else {
                    comment_zone.setVisible(true);
                    label_already_coment.setVisible(false);
                }
            }
        });
    }

    public void comment(ActionEvent actionEvent) {
        if (oeuvre_selected == null) return;
        String commentaire = text_new_comment.getText();
        double note = slider_note.getValue();
        CommentairesDAO commentairesDAO = new CommentairesDAO();
        Commentaires commentaires = new Commentaires(Main.getUtilisateur().get_id(),oeuvre_selected,commentaire,(int) note);
        commentairesDAO.create(commentaires);
        if (vbox_commentaires.getChildren().get(0) instanceof Label) vbox_commentaires.getChildren().clear();
        display_commentaire(commentaires);
        comment_zone.setVisible(false);
        label_already_coment.setVisible(true);
    }

    private void display_commentaire(Commentaires commentaire){
        HBox hBox = new HBox();
        Label note = new Label("["+commentaire.getNote() + "/20]");
        Label com = new Label(commentaire.getCommentaire());
        com.setPrefWidth(210);
        com.setWrapText(true);
        hBox.getChildren().addAll(note,com);
        hBox.setSpacing(5);
        vbox_commentaires.getChildren().add(hBox);
    }
}
