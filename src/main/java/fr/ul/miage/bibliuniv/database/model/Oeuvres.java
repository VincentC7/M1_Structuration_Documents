package fr.ul.miage.bibliuniv.database.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;

public class Oeuvres extends ModelTable{


    private String titre,theme,contenu;
    private HashSet<ObjectId> auteurs,formations,universites;
    private HashSet<Utilisateurs.ROLE> roles;
    private LocalDate publication;
    private int pages;

    public Oeuvres(String titre, String theme, String contenu, HashSet<ObjectId> auteurs, HashSet<ObjectId> formations, HashSet<ObjectId> universites, HashSet<Utilisateurs.ROLE> roles, LocalDate publication, int pages) {
        this.titre = titre;
        this.theme = theme;
        this.contenu = contenu;
        this.auteurs = auteurs;
        this.formations = formations;
        this.universites = universites;
        this.roles = roles;
        this.publication = publication;
        this.pages = pages;
    }

    public Oeuvres(Document d){
        this.auteurs = new HashSet<>();
        this.formations = new HashSet<>();
        this.universites = new HashSet<>();
        this.roles = new HashSet<>();
        this._id = d.getObjectId("_id");
        this.titre = d.getString("titre");
        this.theme = d.getString("theme");
        this.contenu = d.getString("contenu");
        this.pages = d.getInteger("pages");
        this.publication = d.getDate("publication").toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        auteurs.addAll(d.getList("auteurs", ObjectId.class));
        formations.addAll(d.getList("formations", ObjectId.class));
        universites.addAll(d.getList("universites", ObjectId.class));
        for (String s : d.getList("roles", String.class))
            roles.add(Utilisateurs.ROLE.valueOf(s));


    }


    public String getTitre() {
        return titre;
    }

    public String getTheme() {
        return theme;
    }

    public String getContenu() {
        return contenu;
    }

    public HashSet<ObjectId> getAuteurs() {
        return auteurs;
    }

    public HashSet<ObjectId> getFormations() {
        return formations;
    }

    public HashSet<ObjectId> getUniversites() {
        return universites;
    }

    public HashSet<Utilisateurs.ROLE> getRoles() {
        return roles;
    }

    public LocalDate getPublication() {
        return publication;
    }

    public int getPages() {
        return pages;
    }
}
