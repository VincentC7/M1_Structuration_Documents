package fr.ul.miage.bibliuniv.database.model;

import org.bson.types.ObjectId;

import java.util.Objects;

public class FormationUtilisateur {

    private ObjectId formation;
    private String anneeD,anneeF;

    public FormationUtilisateur(ObjectId formation, String anneeD, String anneeF) {
        this.formation = formation;
        this.anneeD = anneeD;
        this.anneeF = anneeF;
    }

    public FormationUtilisateur(ObjectId formation, String anneeD) {
        this.formation = formation;
        this.anneeD = anneeD;
    }

    public void ajoutDate(String date){
        var newDate = Integer.parseInt(date);
        var anneeDInt = Integer.parseInt(anneeD);
        var anneeFInt = Integer.parseInt(anneeF);
        if (newDate < anneeDInt)
            anneeD = String.valueOf(newDate);
        if (newDate > anneeFInt)
            anneeF = String.valueOf(newDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormationUtilisateur that = (FormationUtilisateur) o;
        return Objects.equals(formation, that.formation) && Objects.equals(anneeD, that.anneeD) && Objects.equals(anneeF, that.anneeF);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formation, anneeD, anneeF);
    }

    public ObjectId getFormation() {
        return formation;
    }

    public String getAnneeD() {
        return anneeD;
    }

    public String getAnneeF() {
        return anneeF;
    }

    @Override
    public String toString() {
        return "FormationUtilisateur{" +
                "formation=" + formation +
                ", anneeD='" + anneeD + '\'' +
                ", anneeF='" + anneeF + '\'' +
                '}';
    }
}
