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

        // Le fichier de description de la scène si pas fournie
        String fichier_description = "simple.txt";

        // largeur et hauteur par défaut de l'image à reconstruire
        int largeur = 512, hauteur = 512;

        if (args.length > 0) {
            fichier_description = args[0];
            if (args.length > 1) {
                largeur = Integer.parseInt(args[1]);
                if (args.length > 2)
                    hauteur = Integer.parseInt(args[2]);
            }
        } else {
            System.out.println(aide);
        }

        // création d'une fenêtre
        Disp disp = new Disp("Raytracer", largeur, hauteur);

        // Initialisation d'une scène depuis le modèle
        Scene scene = new Scene(fichier_description, largeur, hauteur);

        // Calcul de l'image de la scène les paramètres :
        // - x0 et y0 : correspondant au coin haut à gauche
        // - l et h : hauteur et largeur de l'image calculée
        // Ici on calcule toute l'image (0,0) -> (largeur, hauteur)

        int x0 = 0, y0 = 0;
        int l = largeur, h = hauteur;

        // Chronométrage du temps de calcul
        Instant debut = Instant.now();
        System.out.println("Calcul de l'image :\n - Coordonnées : " + x0 + "," + y0
                + "\n - Taille " + largeur + "x" + hauteur);
        Image image = scene.compute(x0, y0, l / 2, h / 2);
        Image image2 = scene.compute(l / 2, h / 2, l / 2, l / 2);
        Instant fin = Instant.now();

        long duree = Duration.between(debut, fin).toMillis();

        System.out.println("Image calculée en :" + duree + " ms");

        // Affichage de l'image calculée
        disp.setImage(image, x0, y0);
        disp.setImage(image2, l / 2, h / 2);

        // On créer un objet distant avec la scene
        try {
            // On récupère l'annuaire local
            Registry registry = LocateRegistry.getRegistry(1099);

            // On exporte l'objet distant
            SceneInterface stub = (SceneInterface) UnicastRemoteObject.exportObject(scene, 0);

            // On récupère le service du distributeur de noeuds

            // On récupère un noeud avec le service

            // On calcul l'image avec le noeud

        } catch (RemoteException e) {
            System.err.println("Erreur lors de la création du serveur RMI : " + e.getMessage());
        }
    }
}
