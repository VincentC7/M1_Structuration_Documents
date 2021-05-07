package fr.ul.miage.bibliuniv.database.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Formations extends ModelTable{

    private final static SecureRandom RANDOM = new SecureRandom();
    private final static int MAX_PLACES = 300;

    public enum NIVEAU{
        BAC2("Bac + 2"),
        BAC3("Bac + 3"),
        BAC5("Bac + 5");

        private final String text;

        /**
         * @param text
         */
        NIVEAU(final String text) {
            this.text = text;
        }

        public static NIVEAU rand(){
            int i = RANDOM.nextInt() % NIVEAU.values().length;
            return NIVEAU.values()[i];
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    String nom;
    NIVEAU niveau;
    Map<ObjectId,Integer> universites;

    public Formations(String nom){
        this.nom = nom;
        niveau = NIVEAU.rand();
        this.universites = new HashMap<>();
    }

    public Formations(Document d){
        this._id = d.getObjectId("_id");
        this.niveau = NIVEAU.valueOf(d.getString("niveau"));
        this.universites = new HashMap<>();
        var univs = d.getList("universites",Document.class);
        for(Document doc : univs){
            this.universites.put(doc.getObjectId("universite"),doc.getInteger("places"));
        }
    }

    public void addUniversities(ObjectId id){
        this.universites.put(id,RANDOM.nextInt()%MAX_PLACES);
    }


    public String getNom() {
        return nom;
    }

    public NIVEAU getNiveau() {
        return niveau;
    }

    public Map<ObjectId, Integer> getUniversites() {
        return universites;
    }
}
