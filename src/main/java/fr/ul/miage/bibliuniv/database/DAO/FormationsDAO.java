package fr.ul.miage.bibliuniv.database.DAO;

import fr.ul.miage.bibliuniv.database.model.Formations;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;

public class FormationsDAO extends DAO<Formations>{

    public FormationsDAO() {
        super("Formations");
    }

    @Override
    public Formations find(ObjectId id) {
        Document d = connect.find(eq(id)).first();
        return (d == null) ? null
                : new Formations(d);
    }


    public Formations findByNom(String nom) {
        Document d = connect.find(eq("nom",nom)).first();
        return (d == null) ? null
                : new Formations(d);
    }

    @Override
    public Formations create(Formations obj) {
        List<Document> l = new ArrayList<>();
        for(var univ : obj.getUniversites().entrySet()){
            l.add(
                    new Document("universite",univ.getKey())
                            .append("places",univ.getValue())
            );
        }

        var d = new Document("nom", obj.getNom())
                .append("niveau",obj.getNiveau().name())
                .append("universites",l);

        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();
        return (insert.wasAcknowledged()) ? find(id) :  null;
    }

    @Override
    public Formations update(Formations obj) {
        Formations f = this.find(obj.get_id());

        Map<ObjectId,Integer> oldFormations = f.getUniversites();
        Map<ObjectId,Integer> newFormations = obj.getUniversites();
        ArrayList<ObjectId>  newHasmap = newFormations.keySet().stream().filter(e -> !oldFormations.containsKey(e))
                .collect(Collectors.toCollection(ArrayList::new));
        if(!newHasmap.isEmpty()){
            for( ObjectId id : newHasmap){
                Document subdoc =new Document("universite",id)
                        .append("places",newFormations.get(id));
                    connect.updateOne(eq(obj.get_id()), push("universites", subdoc));
            }
        }
        return find(obj.get_id());
    }
}
