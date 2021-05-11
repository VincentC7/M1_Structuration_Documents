package fr.ul.miage.bibliuniv.database.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Commentaires extends ModelTable{

    private ObjectId auteur,oeuvre;
    private String commentaire;
    private int note;
    private LocalDateTime publication;

    public Commentaires(ObjectId auteur, ObjectId oeuvre, String commentaire, int note, LocalDateTime publication) {
        this.auteur = auteur;
        this.oeuvre = oeuvre;
        this.commentaire = commentaire;
        this.note = note;
        this.publication = publication;
    }

    public Commentaires(Document d){
        this._id = d.getObjectId("_id");
        this.auteur = d.getObjectId("auteur");
        this.oeuvre = d.getObjectId("oeuvre");
        this.commentaire = d.getString("commentaire");
        this.note = d.getInteger("note");
        this.publication = d.getDate("publication").toInstant()
                .atZone(ZoneId.of("GMT"))
                .toLocalDateTime();
    }

    public ObjectId getAuteur() {
        return auteur;
    }

    public ObjectId getOeuvre() {
        return oeuvre;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public int getNote() {
        return note;
    }

    public LocalDateTime getPublication() {
        return publication;
    }
}
