import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

public class Noeud implements NoeudInterface {

    String ip;

    public Noeud() {
        try {
            // On récupère l'adresse IP de la machine
            this.ip = RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
            this.ip = "localhost"; // Valeur par défaut en cas d'erreur
        }
    }

    public Image calculer(SceneInterface scene, int x, int y, int l, int h) throws RemoteException {
        Image img = scene.compute(x, y, l, h);
        System.out.println("Image calculee par " + this.ip);
        return img;
    }

    @Override
    public String toString() {
        return "Adresse ip : " + ip;
    }

}