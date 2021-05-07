package fr.ul.miage.bibliuniv.database.DAO;

import fr.ul.miage.bibliuniv.database.model.Universites;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class UniversitesDAO extends DAO<Universites> {

    UniversitesDAO() {
        super("Universites");
    }

    @Override
    public Universites find(ObjectId id) {
        Document d = connect.find(eq(id)).first();
        return (d == null) ? null
                : new Universites(d);
    }

    @Override
    public Universites create(Universites obj) {
        var d = new Document("nom", obj.getNom());

        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();
        return (insert.wasAcknowledged()) ? find(id) :  null;
    }

    @Override
    public Universites update(Universites obj) {
        return null;
    }
}
