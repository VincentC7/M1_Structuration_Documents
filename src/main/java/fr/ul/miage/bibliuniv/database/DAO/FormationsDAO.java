package fr.ul.miage.bibliuniv.database.DAO;

import fr.ul.miage.bibliuniv.database.model.Formations;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class FormationsDAO extends DAO<Formations>{

    FormationsDAO() {
        super("Formations");
    }

    @Override
    public Formations find(ObjectId id) {
        Document d = connect.find(eq(id)).first();
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
        return null;
    }
}
