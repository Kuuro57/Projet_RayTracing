import java.time.Instant;
import java.time.Duration;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class LancerRaytracer {

    public static String aide = "Raytracer : synthèse d'image par lancé de rayons (https://en.wikipedia.org/wiki/Ray_tracing_(graphics))\n\nUsage : java LancerRaytracer [fichier-scène] [largeur] [hauteur]\n\tfichier-scène : la description de la scène (par défaut simple.txt)\n\tlargeur : largeur de l'image calculée (par défaut 512)\n\thauteur : hauteur de l'image calculée (par défaut 512)\n";

    public static void main(String args[]) {

        // On récupère l'adresse du serveur et le port si ils sont dans la ligne de commande
        // Sinon, on attribut des valeurs par défaut
        String serveur = "localhost";
        int port = 1099;

        // On récupère l'adresse de l'annuaire en paramètre
        if (args.length > 0){ serveur = args[0]; }

        // On récupère le port de l'annuaire en paramètre
        if (args.length > 1){ port = Integer.parseInt(args[1]); }

        
        try {

            // Le fichier de description de la scène si pas fournie
            String fichier_description = "simple.txt";

            // Largeur et hauteur par défaut de l'image à reconstruire
            int largeur = 512, hauteur = 512;

            if (args.length > 1) {

                fichier_description = args[2];
                if (args.length > 2) {
                    largeur = Integer.parseInt(args[3]);
                    if (args.length > 3) hauteur = Integer.parseInt(args[4]); 
                }

            } 
            else { System.out.println(aide); }


            // Création d'une fenêtre
            Disp disp = new Disp("Raytracer", largeur, hauteur);

            // Initialisation d'une scène depuis le modèle
            Scene scene = new Scene(fichier_description, largeur, hauteur);

            // Calcul de l'image de la scène les paramètres :
            // - x0 et y0 : correspondant au coin haut à gauche
            // - l et h : hauteur et largeur de l'image calculée
            // Ici on calcule toute l'image (0,0) -> (largeur, hauteur)

            int x0 = 0, y0 = 0;
            int l = largeur, h = hauteur;


            int div = 8;
            if(args.length > 4){ div = Integer.parseInt(args[5]); }


            // On récupère l'annuaire local
            Registry reg = LocateRegistry.getRegistry(port);

            // On exporte l'objet distant
            SceneInterface stub = (SceneInterface) UnicastRemoteObject.exportObject(scene, 0);

            // On récupère le service du distributeur de noeuds
            ServiceDistributeur distrib = (ServiceDistributeur) reg.lookup("distributeur");
            
            // On calcul l'image avec le noeud
            Instant debut = Instant.now();
            
            for(int i=0;i<div;i++){
                for(int j=0;j<div;j++){
                    NoeudInterface noeud = (NoeudInterface) distrib.getNoeud();
                    Image image = noeud.calculer(scene,j*l/div, i*h/div, l/div, h/div);
                    //Image image = scene.compute(j*l/div, i*h/div, l/div, h/div);
                    disp.setImage(image, j*l/div, i*h/div);
                }
            }

            Instant fin = Instant.now();
            long duree = Duration.between(debut, fin).toMillis();
            System.out.println("Image calculée en :" + duree + " ms");




        } 
        catch (RemoteException e) {
            System.err.println("Erreur lors de la création du serveur RMI : " + e.getMessage());
        }
        catch (NotBoundException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
        catch (ServerNotActiveException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
