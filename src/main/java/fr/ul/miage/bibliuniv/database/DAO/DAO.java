package fr.ul.miage.bibliuniv.database.DAO;

import com.mongodb.client.MongoCollection;
import fr.ul.miage.bibliuniv.database.BDDConnexion;
import fr.ul.miage.bibliuniv.database.model.ModelTable;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public abstract class DAO<T extends ModelTable> {

    final MongoCollection<Document> connect ;

    DAO(String table){
        connect = BDDConnexion.getInstance().getCollection(table);
    }

    /**
     * Permet de récupérer un objet via son ID
     * @param id
     * @return
     */
    public abstract T find(ObjectId id);

    /**
     * Permet de créer une entrée dans la base de données
     * par rapport à un objet
     * @param obj
     */
    public abstract T create(T obj);

    /**
     * Permet de mettre à jour les données d'une entrée dans la base
     * @param obj
     */
    public abstract T update(T obj);

    /**
     * Permet la suppression d'une entrée de la base
     * @param obj
     * @return boolean qui permet de savoir si l'objet a bien été supprimé
     */
    public boolean delete(T obj){
        return connect.findOneAndDelete(eq(obj.get_id())) != null;
    }
}
