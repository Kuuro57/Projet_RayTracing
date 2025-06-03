import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
// import java.rmi.registry.LocateRegistry;
// import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * 
 */
public class LancerDistributeurNoeuds {
    public static void main(String[] args) {
        
        
        // on récupère l'adresse du serveur et le port si ils sont dans la ligne de commande
        // si non, on attribut des valeurs par défaut
        String serveur = "localhost";
        int port = 1099;

        if (args.length>0){
            serveur=args[0]; // on récupère l'adresse de l'annuaire en paramètre
        }
        if (args.length>1){
            port = Integer.parseInt(args[1]); // on récupère le port de l'annuaire en paramètre
        }

            try {

                /* Création du distributeur de noeuds */
                DistributeurNoeuds distrib = new DistributeurNoeuds();

                /* Exporter le distributeur */
                ServiceDistributeur rd = (ServiceDistributeur) UnicastRemoteObject.exportObject(distrib, 0);

                /* Récupération de l'annuaire */
                Registry reg = LocateRegistry.getRegistry(port);

                /* Ajout du distributeur à l'annuaire */
                reg.rebind("distributeur", rd);

            }
            catch (RemoteException e) {}


            System.out.println("Service opérationnel !");


    }
}
