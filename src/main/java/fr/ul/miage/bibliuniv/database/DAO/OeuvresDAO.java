package fr.ul.miage.bibliuniv.database.DAO;

import fr.ul.miage.bibliuniv.database.model.Oeuvres;
import fr.ul.miage.bibliuniv.database.model.Utilisateurs;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

public class OeuvresDAO extends DAO<Oeuvres> {

    public OeuvresDAO(){super("Oeuvres");}

    @Override
    public Oeuvres find(ObjectId id) {
        Document d = connect.find(eq(id)).first();
        return (d == null) ? null
                : new Oeuvres(d);
    }

    public List<Oeuvres> findByUtilisateur(Utilisateurs u){
        ObjectId user_id = u.get_id();
        ObjectId format_id = u.getFormations().stream().filter(f -> f.getAnneeF() == null).findFirst().get().getFormation();
        ObjectId univ_id = u.getUniversite();
        Utilisateurs.ROLE role = u.getRole();
        ArrayList<Document> documents = connect.find(
                or(
                        in("formations",format_id),
                        in("universites",univ_id),
                        in("auteurs",user_id),
                        in("role",role.name())
                )
        )
                .into(new ArrayList<>());
        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(Oeuvres::new).collect(Collectors.toList());
    }

    public List<Oeuvres> findByUtilisateurTOP10(Utilisateurs u){
        ObjectId user_id = u.get_id();
        ObjectId format_id = u.getFormations().stream().filter(f -> f.getAnneeF() == null).findFirst().get().getFormation();
        ObjectId univ_id = u.getUniversite();
        Utilisateurs.ROLE role = u.getRole();
        ArrayList<Document> documents = connect.find(
                or(
                        in("formations",format_id),
                        in("universites",univ_id),
                        in("auteurs",user_id),
                        in("role",role.name())
                )
        )
                .into(new ArrayList<>());
        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(Oeuvres::new).collect(Collectors.toList());
    }

    @Override
    public Oeuvres create(Oeuvres obj) {
        var d = new Document("titre",obj.getTitre())
                .append("pages",obj.getPages())
                .append("publication",obj.getPublication())
                .append("contenu", obj.getContenu())
                .append("theme",obj.getTheme())
                .append("formations",obj.getFormations())
                .append("universites",obj.getUniversites())
                .append("auteurs",obj.getAuteurs())
                .append("roles",obj.getRoles().stream().map(Utilisateurs.ROLE::name).collect(Collectors.toList()));
        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();
        return (insert.wasAcknowledged()) ? find(id) :  null;
    }

    @Override
    public Oeuvres update(Oeuvres obj) {
        return null;
    }
}
