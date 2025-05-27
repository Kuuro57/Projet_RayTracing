import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
// import java.rmi.registry.LocateRegistry;
// import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerDistributeurNoeuds {
    public static void main(String[] args) {

            int port = 5555;

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
