import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

public interface ServiceDistributeur extends Remote {

    public void ajouterNoeud(NoeudInterface noeud) throws RemoteException, ServerNotActiveException;

    public NoeudInterface getNoeud() throws RemoteException, ServerNotActiveException;

}
