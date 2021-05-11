package fr.ul.miage.bibliuniv.database.DAO;

import fr.ul.miage.bibliuniv.database.model.Commentaires;
import fr.ul.miage.bibliuniv.database.model.Utilisateurs;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class CommentairesDAO extends DAO<Commentaires> {

    public CommentairesDAO() {
        super("Commentaires");
    }

    @Override
    public Commentaires find(ObjectId id) {
        Document d = connect.find(eq(id)).first();
        return (d == null) ? null
                : new Commentaires(d);
    }

    @Override
    public Commentaires create(Commentaires obj) {
        var d = new Document("auteur", obj.getAuteur())
                .append("oeuvre",obj.getOeuvre())
                .append("commentaire",obj.getCommentaire())
                .append("note",obj.getNote())
                .append("publication",obj.getPublication());

        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();
        return (insert.wasAcknowledged()) ? find(id) :  null;
    }

    @Override
    public Commentaires update(Commentaires obj) {
        return null;
    }
}
