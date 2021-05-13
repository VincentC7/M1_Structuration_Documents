package fr.ul.miage.bibliuniv.database.DAO;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import fr.ul.miage.bibliuniv.database.model.Oeuvres;
import fr.ul.miage.bibliuniv.database.model.Utilisateurs;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.text.Utilities;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;

public class OeuvresDAO extends DAO<Oeuvres> {

    public OeuvresDAO(){super("Oeuvres");}

    @Override
    public Oeuvres find(ObjectId id) {
        Document d = connect.find(eq(id)).first();
        return (d == null) ? null
                : new Oeuvres(d);
    }


    public Oeuvres findByTitreEtDate(String titre, LocalDate date) {
        Document d = connect.find(and(eq("titre",titre),eq("publication",date))).first();
        return (d == null) ? null
                : new Oeuvres(d);
    }

    public List<Oeuvres> findByUtilisateur(Utilisateurs u){
        ObjectId user_id = u.get_id();
        ArrayList<ObjectId> format_id = u.getFormations().stream().map(f -> f.getFormation()).collect(Collectors
                .toCollection(ArrayList::new));
        ObjectId univ_id = u.getUniversite();
        Utilisateurs.ROLE role = u.getRole();
        System.out.println(u.getRole().name());
        ArrayList<Document> documents = connect.find(
                or(
                        in("formations",format_id),
                        in("universites",univ_id),
                        in("auteurs",user_id),
                        in("roles",role.name())
                )
        )
                .into(new ArrayList<>());
        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(Oeuvres::new).collect(Collectors.toList());
    }


    public int getNote(Oeuvres o){
        Document d = connect.aggregate(Arrays.asList(
                match(eq(o.get_id())),
                lookup("Commentaires","_id","oeuvre","commentaires"),
                unwind("$commentaires"),
                group("$_id"
                        ,Accumulators.avg("avgnote","$commentaires.note"))
                )
        ).first();
        return (d == null) ? -1 : d.getInteger("avgnote");
    }

    public List<Oeuvres> findByUtilisateurTOP10Note(Utilisateurs u){
        ObjectId user_id = u.get_id();
        ArrayList<ObjectId> format_id = u.getFormations().stream().map(f -> f.getFormation()).collect(Collectors
                .toCollection(ArrayList::new));
        ObjectId univ_id = u.getUniversite();
        Utilisateurs.ROLE role = u.getRole();
        ArrayList<Document> documents = connect.aggregate(Arrays.asList(
                    match(or(
                            in("formations",format_id),
                            in("universites",univ_id),
                            in("auteurs",user_id),
                            in("roles",role.name())
                    )),
                    lookup("Commentaires","_id","oeuvre","commentaires"),
                    unwind("$commentaires"),
                    group("$_id"
                            ,Accumulators.first("titre","$titre")
                            ,Accumulators.first("theme","$theme")
                            ,Accumulators.avg("avgnote","$commentaires.note")),
                    sort(Sorts.descending("avgnote")),
                    limit(10)

                )
        ).into(new ArrayList<>());
        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(d -> find(d.getObjectId("_id"))).collect(Collectors.toList());
    }

    public List<Oeuvres> findByTitre(String titre,Utilisateurs u){
        ObjectId user_id = u.get_id();
        ArrayList<ObjectId> format_id = u.getFormations().stream().map(f -> f.getFormation()).collect(Collectors
                .toCollection(ArrayList::new));
        ObjectId univ_id = u.getUniversite();
        Utilisateurs.ROLE role = u.getRole();
        ArrayList<Document> documents = connect.find(and(regex("titre",titre,"i"),or(
                in("formations",format_id),
                in("universites",univ_id),
                in("auteurs",user_id),
                in("roles",role.name())
        ))).into(new ArrayList<>());
        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(Oeuvres::new).collect(Collectors.toList());
    }

    public List<Oeuvres>findByKeyword(String contenu,Utilisateurs u){
        ObjectId user_id = u.get_id();
        ArrayList<ObjectId> format_id = u.getFormations().stream().map(f -> f.getFormation()).collect(Collectors
                .toCollection(ArrayList::new));
        ObjectId univ_id = u.getUniversite();
        Utilisateurs.ROLE role = u.getRole();
        ArrayList<Document> documents =  connect.find(and(regex("contenu",contenu,"i"),or(
                in("formations",format_id),
                in("universites",univ_id),
                in("auteurs",user_id),
                in("roles",role.name())
        ))).into(new ArrayList<>());
        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(Oeuvres::new).collect(Collectors.toList());
    }

    public List<Oeuvres> findByTheme(String theme,Utilisateurs u){
        ObjectId user_id = u.get_id();
        ArrayList<ObjectId> format_id = u.getFormations().stream().map(f -> f.getFormation()).collect(Collectors
                .toCollection(ArrayList::new));
        ObjectId univ_id = u.getUniversite();
        Utilisateurs.ROLE role = u.getRole();
        ArrayList<Document> documents = connect.find(and(regex("theme",theme,"i"),or(
                in("formations",format_id),
                in("universites",univ_id),
                in("auteurs",user_id),
                in("roles",role.name())
                ))).into(new ArrayList<>());
        return (documents.isEmpty()) ? Collections.emptyList()
            : documents.stream().map(Oeuvres::new).collect(Collectors.toList());
    }

    public List<Oeuvres>findByUtilisateurLastComment(Utilisateurs u){
        ObjectId user_id = u.get_id();
        ArrayList<ObjectId> format_id = u.getFormations().stream().map(f -> f.getFormation()).collect(Collectors
                .toCollection(ArrayList::new));
        ObjectId univ_id = u.getUniversite();
        Utilisateurs.ROLE role = u.getRole();
        ArrayList<Document> documents = connect.aggregate(Arrays.asList(
                match(or(
                        in("formations",format_id),
                        in("universites",univ_id),
                        in("auteurs",user_id),
                        in("roles",role.name())
                )),
                lookup("Commentaires","_id","oeuvre","commentaires"),
                unwind("$commentaires"),
                group("$_id"
                        ,Accumulators.first("titre","$titre")
                        ,Accumulators.first("theme","$theme")
                        ,Accumulators.max("publication","$commentaires.publication")),
                sort(Sorts.descending("publication")),
                limit(40)

                )
        ).into(new ArrayList<>());

        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(d -> find(d.getObjectId("_id"))).collect(Collectors.toList());
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

    public static void main(String[] args) {
        UtilisateursDAO utilisateursDAO = new UtilisateursDAO();
        Utilisateurs u = utilisateursDAO.findByLogin("McdonnellC");
        OeuvresDAO dao = new OeuvresDAO();
        for(Oeuvres o : dao.findByUtilisateur(u))//dao.findByUtilisateur(u) )
            System.out.println(o);
        //dao.findByTheme("rapport");
        //dao.findByKeyword("point");
        //dao.findByTitre("os");
        /*dao.findByUtilisateurTOP10Note(u);
        dao.findByUtilisateurLastComment(u).size();*/
    }



}


