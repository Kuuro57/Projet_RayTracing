import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
public interface ServiceDistributeur extends Remote {
    
    public void ajouterNoeud(String noeud) throws RemoteException, ServerNotActiveException;
    
    public String getNoeud() throws RemoteException, ServerNotActiveException;

}
