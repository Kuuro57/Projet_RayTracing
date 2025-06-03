import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;

/**
 * Classe qui représente un distributeur de noeuds de calcul
 */
public class DistributeurNoeuds implements ServiceDistributeur {

    // Attributs
    private int incr = 0; // Itérateur
    ArrayList<NoeudInterface> listeNoeuds; // Liste de noeuds qui contient les IPs de chaque machine utilisable

    /**
     * Constructeur
     */
    public DistributeurNoeuds() {
        this.listeNoeuds = new ArrayList<NoeudInterface>();
    }

    /**
     * Méthode qui ajoute un noeud à la liste de noeuds
     * 
     * @param noeud Une adresse IP
     */
    public void ajouterNoeud(NoeudInterface noeud) throws RemoteException, ServerNotActiveException {
        this.listeNoeuds.add(noeud);
        System.out.println("Noeud ajouté ! (nombre de noeuds : " + this.listeNoeuds.size() + ")");
    }

    /**
     * Méthode qui supprime un noeud de la liste de noeuds
     */
    public void supprimerNoeud(NoeudInterface noeud) {
        // On vérifie la position du noeud dans la liste pour modifier l'itérateur
        if (this.listeNoeuds.indexOf(noeud) < incr) {
            incr -= 1; // On décrémente l'itérateur si le noeud supprimé est avant l'itérateur
        }
        this.listeNoeuds.remove(noeud);
    }

    /**
     * Méthode qui renvooie un noeud pour effectuer un calcul
     */
    public NoeudInterface getNoeud() throws RemoteException, ServerNotActiveException {
        incr += 1;
        if (incr >= this.listeNoeuds.size()) {
            incr = 0;
        }

        if (this.listeNoeuds.size() == 0) {
            return null;
            // traitement dans raytracing de ce cas de figure
        }

        NoeudInterface noeud = this.listeNoeuds.get(incr);

        return noeud;

    }

}
