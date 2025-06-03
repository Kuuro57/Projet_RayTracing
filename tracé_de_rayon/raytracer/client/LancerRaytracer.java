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

    public static String aide = "Raytracer : synthèse d'image par lancé de rayons (https://en.wikipedia.org/wiki/Ray_tracing_(graphics))\n\nUsage : java LancerRaytracer [ip-repository] [port-repository] [fichier-scène] [largeur] [hauteur] [nb-decoupe] [mode-exec]\n\tfichier-scène : la description de la scène (par défaut simple.txt)\n\tlargeur : largeur de l'image calculée (par défaut 512)\n\thauteur : hauteur de l'image calculée (par défaut 512)\n\tnb-decoupe : nombre de découpes de l'image (par défaut 8)\n\tmode-exec : mode d'exécution (par défaut 0, 0 = séquentiel, 1 = parallèle)\n";

    public static void main(String args[]) {

        // On récupère l'adresse du serveur et le port si ils sont dans la ligne de
        // commande
        // Sinon, on attribut des valeurs par défaut
        String serveur = "localhost";
        int port = 1099;

        // On récupère l'adresse de l'annuaire en paramètre
        if (args.length > 0) {
            serveur = args[0];
        }

        // On récupère le port de l'annuaire en paramètre
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }

        try {

            // Le fichier de description de la scène si pas fournie
            String fichier_description = "simple.txt";

            // Largeur et hauteur par défaut de l'image à reconstruire
            int largeur = 512, hauteur = 512;

            if (args.length > 1) {

                fichier_description = args[2];
                if (args.length > 2) {
                    largeur = Integer.parseInt(args[3]);
                    if (args.length > 3)
                        hauteur = Integer.parseInt(args[4]);
                }

            } else {
                System.out.println(aide);
            }

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
            if (args.length > 4) {
                div = Integer.parseInt(args[5]);
            }

            int mode_exec = 0;
            if (args.length > 5) {
                mode_exec = Integer.parseInt(args[6]);
            }

            // On récupère l'annuaire local
            Registry reg = LocateRegistry.getRegistry(serveur, port);

            // On exporte l'objet distant
            SceneInterface stub = (SceneInterface) UnicastRemoteObject.exportObject(scene, 0);

            // On récupère le service du distributeur de noeuds
            ServiceDistributeur distrib = (ServiceDistributeur) reg.lookup("distributeur");

            // On calcul l'image avec le noeud
            Instant debut = Instant.now();

            if (mode_exec == 0) {
                for (int i = 0; i < div; i++) {
                    for (int j = 0; j < div; j++) {
                        NoeudInterface noeud = (NoeudInterface) distrib.getNoeud();
                        Image image;
                        if (noeud == null) {
                            image = scene.compute(j * l / div, i * h / div, l / div, h / div);
                        } else {
                            image = noeud.calculer(scene, j * l / div, i * h / div, l / div, h / div);
                        }
                        disp.setImage(image, j * l / div, i * h / div);
                    }
                }
            } else {
                for (int i = 0; i < div; i++) {
                    for (int j = 0; j < div; j++) {
                        NoeudInterface noeud = (NoeudInterface) distrib.getNoeud();
                        ThreadCalcul thread = new ThreadCalcul(disp, noeud, scene, div, i, j, l, h);
                        thread.start();
                    }
                }
            }

            Instant fin = Instant.now();
            long duree = Duration.between(debut, fin).toMillis();
            System.out.println("Image calculée en :" + duree + " ms");

        } catch (RemoteException e) {
            System.err.println("Erreur lors de la création du serveur RMI : " + e.getMessage());
        } catch (NotBoundException e) {
            System.err.println("Erreur : " + e.getMessage());
        } catch (ServerNotActiveException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /**
     * Classe qui permet de séparer les calculs dans des threads différents
     */
    static class ThreadCalcul extends Thread {

        private Disp disp;
        private NoeudInterface noeud;
        private Scene scene;
        private int div;
        private int l;
        private int h;
        private int i;
        private int j;

        ThreadCalcul(Disp disp, NoeudInterface noeud, Scene scene, int div, int i, int j, int l, int h) {
            this.disp = disp;
            this.noeud = noeud;
            this.scene = scene;
            this.div = div;
            this.l = l;
            this.i = i;
            this.j = j;
            this.h = h;
        }

        @Override
        public void run() {

            try {
                Image image;
                if (this.noeud == null) {
                    // Si le noeud est nul, on utilise la scène pour calculer l'image
                    image = this.scene.compute(this.j * this.l / this.div, this.i * this.h / this.div,
                            this.l / this.div, this.h / this.div);
                } else
                    // Sinon, on utilise le noeud pour calculer l'image
                    image = this.noeud.calculer(this.scene, this.j * this.l / this.div, this.i * this.h / this.div,
                            this.l / this.div, this.h / this.div);
                // Image image = scene.compute(j*l/div, i*h/div, l/div, h/div);
                synchronized (disp) {
                    disp.setImage(image, this.j * this.l / this.div, this.i * this.h / this.div);
                }
            } catch (RemoteException e) {
                System.out.println("Erreur lors de la création du serveur RMI : " + e.getMessage());
            }

        }

    }

}
