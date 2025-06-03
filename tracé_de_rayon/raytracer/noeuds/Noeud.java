import java.rmi.Remote;
import java.rmi.RemoteException;

public class Noeud implements NoeudInterface {

    public Image calculer(SceneInterface scene, int x, int y, int l, int h) throws RemoteException {
        return scene.compute(x, y, l, h);
    }

}