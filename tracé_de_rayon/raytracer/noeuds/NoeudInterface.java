import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NoeudInterface extends Remote{
    Image calculer(SceneInterface scene, double x, double y, double l, double h);
}