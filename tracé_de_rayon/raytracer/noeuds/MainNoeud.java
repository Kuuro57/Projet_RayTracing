import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.NotBoundException;
import java.rmi.AccessException;

public class MainNoeud {

    public static void main(String[] args) {
        // on récupère l'adresse du serveur et le port si ils sont dans la ligne de
        // commande
        // si non, on attribut des valeurs par défaut
        String serveur = "localhost";
        int port = 1099;

        if (args.length > 0) {
            serveur = args[0]; // on récupère l'adresse de l'annuaire en paramètre
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]); // on récupère le port de l'annuaire en paramètre
        }

        try {

            System.out.println("Hey !");

            /* Création du noeud */
            Noeud noeud = new Noeud();

            /* Exporter le noeud */
            NoeudInterface rd = (NoeudInterface) UnicastRemoteObject.exportObject(noeud, 0);

            /* Récupération de l'annuaire */
            Registry reg = LocateRegistry.getRegistry(serveur, port);

            // On récupère le distributeur de noeuds
            ServiceDistributeur distributeur = (ServiceDistributeur) reg.lookup("distributeur");

            // On ajoute le noeud au distributeur
            distributeur.ajouterNoeud(rd);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }
}
