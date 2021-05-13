package fr.ul.miage.bibliuniv.controllers;

import javafx.scene.layout.HBox;
import org.bson.types.ObjectId;

public class HBox_Oeuvre extends HBox {

    private ObjectId oeuvre;

    public HBox_Oeuvre(ObjectId oeuvre_id){
        oeuvre = oeuvre_id;
    }

    public ObjectId getOeuvre() {
        return oeuvre;
    }
}
