package fr.ul.miage.bibliuniv.database.DAO;

import fr.ul.miage.bibliuniv.database.model.Commentaires;
import fr.ul.miage.bibliuniv.database.model.Oeuvres;
import fr.ul.miage.bibliuniv.database.model.Utilisateurs;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Commentaires> findByOeuvre(Oeuvres o) {
        ArrayList<Document> documents = connect.find(eq("oeuvre",o.get_id())).into(new ArrayList<>());

        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(Commentaires::new).collect(Collectors.toList());
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

    public static void main(String[] args) {
        UtilisateursDAO utilisateursDAO = new UtilisateursDAO();
        CommentairesDAO commentairesDAO = new CommentairesDAO();
        Utilisateurs u = utilisateursDAO.findByLogin("BowdenH");
        //Utilisateurs u = utilisateursDAO.findByLogin("KimS");
        OeuvresDAO dao = new OeuvresDAO();
        dao.findByUtilisateur(u);

        int i = 0;
        for(Oeuvres o : dao.findByUtilisateur(u)){
            Commentaires c = new Commentaires(u.get_id(),o.get_id(),"xD",i%10);
            commentairesDAO.create(c);
            System.out.println(i++);
            System.out.println(o);
        }

    }
}
