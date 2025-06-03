import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NoeudInterface extends Remote {
    Image calculer(SceneInterface scene, int x, int y, int l, int h) throws RemoteException;
}