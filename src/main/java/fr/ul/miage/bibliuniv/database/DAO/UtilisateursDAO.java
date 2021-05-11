package fr.ul.miage.bibliuniv.database.DAO;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import fr.ul.miage.bibliuniv.database.model.FormationUtilisateur;
import fr.ul.miage.bibliuniv.database.model.Utilisateurs;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;

public class UtilisateursDAO extends DAO<Utilisateurs> {

    public UtilisateursDAO() {
        super("Utilisateurs");
    }

    @Override
    public Utilisateurs find(ObjectId id) {
        Document d = connect.find(eq(id)).first();
        return (d == null) ? null
                : new Utilisateurs(d);
    }

    public Utilisateurs findByNomEtPrenom(String nom,String prenom) {
        Document d = connect.find(and(eq("nom",nom),eq("prenom",prenom))).first();
        return (d == null) ? null
                : new Utilisateurs(d);
    }

    public Utilisateurs findByLogin(String login) {
        Document d = connect.find(eq("login",login)).first();
        return (d == null) ? null
                : new Utilisateurs(d);
    }

    public int countLogin(String login){
        Document d= connect.aggregate(Arrays.asList(

                // Java equivalent of the $match stage
                Aggregates.match(Filters.regex("login", login)),
                Aggregates.count()

        )).first();
        return (d == null) ? 0 : d.getInteger("count");
    }

    @Override
    public Utilisateurs create(Utilisateurs obj) {
        List<Document> l = new ArrayList<>();
        for(var fu : obj.getFormations()){
            l.add(
                    new Document("formation",fu.getFormation())
                            .append("anneeD",fu.getAnneeD())
                            .append("anneeF",fu.getAnneeF())
            );
        }

        var d = new Document("login", obj.getLogin())
                .append("nom",obj.getNom())
                .append("prenom",obj.getPrenom())
                .append("role",obj.getRole().name())
                .append("universite",obj.getUniversite())
                .append("formations",l);

        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();
        return (insert.wasAcknowledged()) ? find(id) :  null;
    }

    @Override
    public Utilisateurs update(Utilisateurs obj) {
        Utilisateurs u = this.find(obj.get_id());
        var oldUtilisateurs = u.getFormations();
        var newUtilisateurs = obj.getFormations();
        ArrayList<FormationUtilisateur>  newHasmap = newUtilisateurs.stream().filter(e -> !oldUtilisateurs.contains(e))
                .collect(Collectors.toCollection(ArrayList::new));
        if(!newHasmap.isEmpty()){
            ArrayList<Document> fus = new ArrayList<>();
            for( FormationUtilisateur fu : obj.getFormations()){
                fus.add(new Document("formation",fu.getFormation())
                        .append("anneeD",fu.getAnneeD())
                        .append("anneeF",fu.getAnneeF()));
            }
            connect.updateOne(eq(obj.get_id()), set("formations", fus));
        }
        if (!(obj.getRole().equals(u.getRole())))
            connect.updateOne(eq(obj.get_id()), set("role",obj.getRole().name()));

        return this.find(obj.get_id());
    }
}
