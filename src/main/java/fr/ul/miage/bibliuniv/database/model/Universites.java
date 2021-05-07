package fr.ul.miage.bibliuniv.database.model;

import org.bson.Document;

public class Universites extends ModelTable{

    String nom;

    public Universites(String nom){
        this.nom = nom;
    }

    public Universites(Document d){
        this._id = d.getObjectId("_id");
        this.nom = d.getString("nom");
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
