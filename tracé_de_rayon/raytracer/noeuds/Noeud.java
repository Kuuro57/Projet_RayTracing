import java.rmi.Remote;
import java.rmi.RemoteException;

public class Noeud implements NoeudInterface {
    
    public Image calculer(SceneInterface scene, double x, double y, double l, double h) throws RemoteException{
        return scene.compute(x,y,l,h);
    }
    
}