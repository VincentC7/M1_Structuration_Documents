package fr.ul.miage.bibliuniv.database;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class BDDConnexion {

    private static BDDConnexion bdd_connexion;



    private MongoDatabase database;

    private BDDConnexion(){
        var mongoClient = MongoClients.create();
        database = mongoClient.getDatabase("structuration_document");

    }

    public static MongoDatabase getInstance(){
        if (bdd_connexion == null){
            bdd_connexion = new BDDConnexion();
        }
        return bdd_connexion.getDatabase();
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
