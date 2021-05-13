package fr.ul.miage.bibliuniv.database;

import fr.ul.miage.bibliuniv.database.DAO.FormationsDAO;
import fr.ul.miage.bibliuniv.database.DAO.OeuvresDAO;
import fr.ul.miage.bibliuniv.database.DAO.UniversitesDAO;
import fr.ul.miage.bibliuniv.database.DAO.UtilisateursDAO;
import fr.ul.miage.bibliuniv.database.model.*;
import org.bson.types.ObjectId;

import javax.management.relation.Role;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UtilDatabase {

    public static void readFile(){
        try (Stream<Path> paths = Files.walk(Paths.get("import"))) {
            paths.filter(f -> f.toString().endsWith(".txt")).map(Path::toFile)
                    .forEach(f -> {importFiles(f);f.delete();});
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public static void importFiles(File f){
        HashMap<String,String[]> importFile;
        importFile = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while((line=reader.readLine())!=null){
                String[] split = line.split(":");
                if(split[0].equalsIgnoreCase("contenu"))
                    break;
                importFile.put(split[0],split[1].split(","));
            }
            StringBuilder contenu = new StringBuilder();
            while((line=reader.readLine())!=null){
                contenu.append(line);
            }
            importFile.put("Contenu",new String[]{contenu.toString()});
            createDatabase(importFile);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void createDatabase(HashMap<String,String[]> oeuvres){
        ArrayList<Universites> universites = createDatabaseUniv(oeuvres.get("Universites"));
        ArrayList<Formations> formations = createDatabaseFormations(oeuvres.get("Formations"),
                universites.stream().map(universites1 -> universites1.get_id())
                        .collect(Collectors.toCollection(ArrayList::new)));
        Utilisateurs.ROLE roleMax= Utilisateurs.ROLE.ETUDIANT;
        ArrayList<Utilisateurs.ROLE> roles = new ArrayList<>();
        for(String s : oeuvres.get("Roles")) {
            Utilisateurs.ROLE inter = Utilisateurs.ROLE.valueOf(s.trim().toUpperCase(Locale.ROOT));
            roles.add(inter);
            if(inter.getValeur() > roleMax.getValeur())
                roleMax = inter;
        }
        ArrayList<Utilisateurs> utilisateurs = createDatabaseUtilisateurs(oeuvres.get("Auteurs"),
                universites.get(0).get_id(),
                roleMax,
                formations,
                oeuvres.get("Publication")[0].trim().split("-")[0]);
        createDatabaseOeuvres(oeuvres,universites,utilisateurs,formations,roles);



    }

    public static ArrayList<Universites> createDatabaseUniv(String [] univs){
        UniversitesDAO universitesDAO = new UniversitesDAO();
        ArrayList<Universites> universites = new ArrayList<>();
        Universites univ;
        for (String uni: univs) {
            uni = uni.trim();
            univ = universitesDAO.findByNom(uni);
            if(univ == null)
                univ = universitesDAO.create(new Universites(uni));
            universites.add(univ);

        }
        return universites;
    }

    public static ArrayList<Formations> createDatabaseFormations(String [] formations, ArrayList<ObjectId> univs){
        FormationsDAO formationsDAO = new FormationsDAO();
        ArrayList<Formations> formats = new ArrayList<>();
        Formations f;
        for (String formation: formations) {
            formation = formation.trim();
            f = formationsDAO.findByNom(formation);
            if(f == null)
                f = formationsDAO.create(new Formations(formation));
            for (ObjectId id : univs){
                if (!(f.universitesPresent(id)))
                    f.addUniversities(id);
            }
            formationsDAO.update(f);
            formats.add(f);

        }
        return formats;
    }

    public static ArrayList<Utilisateurs> createDatabaseUtilisateurs(String [] utils, ObjectId id, Utilisateurs.ROLE r,ArrayList<Formations> formations,String date){
        UtilisateursDAO utilisateursDAO = new UtilisateursDAO();
        ArrayList<Utilisateurs> utilisateurs = new ArrayList<>();
        Utilisateurs u;
        String[] personne;
        String prenom,nom;
        for(String util : utils){
            util = util.trim();
            personne = util.split(" ");
            prenom = personne[0];
            nom = personne[1];
            u = utilisateursDAO.findByNomEtPrenom(nom,prenom);
            if(u == null)
                u = utilisateursDAO.create(new Utilisateurs(id,r,nom,prenom));
            else {
                u.setRole(r);
                if (u.getFormations().stream().map(user -> Integer.parseInt(user.getAnneeF())).max(Integer::compareTo).get() < Integer.parseInt(date))
                    u.setUniversite(id);
            }
            Formations formation = null;
            for (Formations f : formations){
                for(int i = 0; i< u.getFormations().size();i++){
                    if (u.getFormations().get(i).getFormation().equals(f.get_id())){
                        u.getFormations().get(i).ajoutDate(date);
                        formation = f;
                        break;
                    }
                }
                if(formation == null)
                    u.ajoutFormation(f.get_id(),date,date);
            }
            utilisateurs.add(utilisateursDAO.update(u));
        }
        return utilisateurs;

    }

    public static void createDatabaseOeuvres(HashMap<String,String[]> oeuvre, ArrayList<Universites> universites, ArrayList<Utilisateurs> utilisateurs , ArrayList<Formations> formations, ArrayList<Utilisateurs.ROLE> roles){
        OeuvresDAO oeuvresDAO = new OeuvresDAO();
        String[] date =  oeuvre.get("Publication")[0].trim().split("-");
        LocalDate d = LocalDate.of(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
        var doc = oeuvresDAO.findByTitreEtDate(oeuvre.get("Titre")[0].trim(),d);
        if(doc == null) {
            HashSet<ObjectId> auteurs = utilisateurs.stream().map(u -> u.get_id()).collect(Collectors.toCollection(HashSet::new));
            HashSet<ObjectId> univs = universites.stream().map(u -> u.get_id()).collect(Collectors.toCollection(HashSet::new));
            HashSet<ObjectId> formats = formations.stream().map(f -> f.get_id()).collect(Collectors.toCollection(HashSet::new));
            HashSet<Utilisateurs.ROLE> role = new HashSet<>(roles);

            Oeuvres o = new Oeuvres(oeuvre.get("Titre")[0].trim(),
                    oeuvre.get("Theme")[0].trim(),
                    oeuvre.get("Contenu")[0].trim(),
                    auteurs,
                    formats,
                    univs,
                    role,
                    d,
                    Integer.parseInt(oeuvre.get("Pages")[0].trim())
            );
            oeuvresDAO.create(o);
        }

    }

    public static void main(String[] args) {
        readFile();
    }
}
