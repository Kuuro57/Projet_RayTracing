import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SceneInterface extends Remote {
    /**
     * Computes the image for the specified region of the scene.
     *
     * @param x the x-coordinate of the top-left corner of the region
     * @param y the y-coordinate of the top-left corner of the region
     * @param l  the width of the region
     * @param h  the height of the region
     * @return an Image object representing the computed image
     */
    Image compute(int x, int y, int l, int h) throws RemoteException;
}
