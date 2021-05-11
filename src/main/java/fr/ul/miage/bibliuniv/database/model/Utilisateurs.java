package fr.ul.miage.bibliuniv.database.model;

import fr.ul.miage.bibliuniv.database.DAO.UtilisateursDAO;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Utilisateurs extends ModelTable{

    public enum ROLE{
        ETUDIANT("Etudiant",0),
        PROFESSEUR("Professeur",1),
        CHERCHEUR("Chercheur",2),
        ADMINISTRATIF("Administratif",3);

        private final String text;
        private final int valeur;

        /**
         * @param text
         */
        ROLE(final String text,final int val) {

            this.text = text;
            this.valeur =val;
        }

        public int getValeur() {
            return valeur;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    private String login,nom,prenom;
    private ObjectId universite;
    private ROLE role;
    private List<FormationUtilisateur> formations;

    public Utilisateurs(Document d){
        this._id = d.getObjectId("_id");
        this.universite = d.getObjectId("universite");
        this.role = ROLE.valueOf(d.getString("role"));
        this.login = d.getString("login");
        this.nom = d.getString("nom");
        this.prenom = d.getString("prenom");
        this.formations = new ArrayList<>();
        var formationsUtilisateurs = d.getList("formations",Document.class);
        for(Document doc : formationsUtilisateurs){
            FormationUtilisateur fu = new FormationUtilisateur(
                    doc.getObjectId("formation"),
                    doc.getString("anneeD"),
                    doc.getString("anneeF"));
            this.formations.add(fu);
        }
    }

    public Utilisateurs(ObjectId univ,ROLE role,String nom,String prenom){
        this.universite = univ;
        this.role = role;

        this.nom = nom;
        this.prenom = prenom;
        this.login = generateLogin(nom,prenom);
        this.formations = new ArrayList<>();
    }

    public void ajoutFormation(ObjectId id,String anneeD,String anneeF){
        this.formations.add(new FormationUtilisateur(id,anneeD,anneeF));
    }

    private String generateLogin(String nom,String prenom){
        UtilisateursDAO dao = new UtilisateursDAO();
        int res = dao.countLogin(nom+prenom.charAt(0));
        if(res == 0)
            return nom+prenom.charAt(0);
        return nom+prenom.charAt(0)+res;
    }

    public String getLogin() {
        return login;
    }

    public String getNom() {
        return nom;
    }

    public void setRole(ROLE role) {
        if(role.getValeur() > this.role.getValeur())
            this.role = role;
    }

    public String getPrenom() {
        return prenom;
    }

    public ObjectId getUniversite() {
        return universite;
    }

    public void setUniversite(ObjectId universite) {
        this.universite = universite;
    }

    public ROLE getRole() {
        return role;
    }

    public List<FormationUtilisateur> getFormations() {
        return formations;
    }
}
