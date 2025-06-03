import java.rmi.Remote;

public interface SceneInterface extends Remote {
    /**
     * Computes the image for the specified region of the scene.
     *
     * @param x0 the x-coordinate of the top-left corner of the region
     * @param y0 the y-coordinate of the top-left corner of the region
     * @param w  the width of the region
     * @param h  the height of the region
     * @return an Image object representing the computed image
     */
    Image compute(int x0, int y0, int w, int h) throws java.rmi.RemoteException;
}
