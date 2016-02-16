package fr.lille1.iut.helloworld.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Client TCP de gestionnaire de tâches partagées
 */
public class ClientTCP {
  private Socket clientSocket = null;
  private PrintWriter envoi = null;
  private BufferedReader reception = null;
  private Scanner sc;

  /**
   * Initialise le scanner, la connexion et récupère le choix de l'utilisateur en interactif
   * @param   L'adresse du serveur
   * @param   Le port destination
   */
  public ClientTCP(String host, int port) {
    sc = new Scanner(System.in);
    Integer choice = menu();
    String message = requestBuilder(choice);

    try {
      clientSocket = new Socket(host, port);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    try {
      envoi = new PrintWriter(clientSocket.getOutputStream(), true);
      reception = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    String reponse = this.envoyer(message);
  }

  public String envoyer(String message) {
    envoi.println(message);

    try {
      return reception.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  /**
   * Affiche le menu
   */
  private void affichageMenu() {
    System.out.println("--- Client TCP : Système de gestion de tâches ---");
    System.out.println("--------------------- Menu ----------------------");

    System.out.println("1 - Déclaration de l'utilisateur");
    System.out.println("2 - Création d'une tâche");
    System.out.println("3 - Destruction d'une tâche");
    System.out.println("4 - Attribution d'une tâche");
    System.out.println("5 - Changement de l'état d'une tâche");
    System.out.println("6 - Liste des tâches d'un utilisateur");
    System.out.println("7 - Informations d'une tâche");
  }

  /**
   * Gère le choix dans le menu
   * @return Le choix de l'utilisateur dans le menu
   */
  private Integer menu() {
    affichageMenu();
    String rep;
    Integer choix = 0;

    do {
      System.out.print("Entrez votre choix :");
      rep = sc.nextLine();
      try {
        choix = Integer.parseInt(rep);
      }
      catch(Exception e) {}
    }while(!isValidChoice(choix));
    return choix;
  }

  /**
   * Vérifie la validité de l'entrée utilisateur
   * @param  choix Le choix de l'utilisateur à vérifier
   * @return       <true> si le choix est valide, <false> sinon
   */
  private boolean isValidChoice(Integer choix) {
    return ((choix > 0) && (choix < 8));
  }

  /**
   * Construit une requête selon le choix de l'utilisateur
   * @param  choix Le choix du menu
   * @return       La chaine à envoyer au serveur
   */
  private String requestBuilder(Integer choix) {
    if(choix == 1) return declareUser();
    else if(choix == 2) return createTask();
    else if(choix == 3) return destroyTask();
    else if(choix == 4) return attributeTask();
    else if(choix == 5) return changeTaskStatus();
    else if(choix == 6) return listUsersTasks();
    else if(choix == 7) return taskDescprition();
    else return "";
  }

  /**
   * Donne son nom d'utilisateur au serveur
   * @return Une chaine de type "CREATE:USER:<user>"
   */
  private String declareUser() {
    System.out.print("Votre nom d'utilisateur :");
    return "CREATE:USER:"+sc.nextLine();
  }

  /**
   * Créer une nouvelle tâche sur le serveur
   * @return Une chaine de type "CREATE:TASK:<description>"
   */
  private String createTask() {
    System.out.println("Description de votre tâche :");
    return "CREATE:TASK:"+sc.nextLine();
  }

  /**
   * Supprime une tâche sur le serveur
   * @return Une chaine du type "DELETE:TASK:<id>"
   */
  private String destroyTask() {
    System.out.print("ID de la tâche à supprimer :");
    return "DELETE:TASK:"+sc.nextLine();
  }

  /**
   * Attriue une tâche à un utilisateur
   * @return Une chaine du type "GIVE:<idTache>:TO:<utilisateur>"
   */
  private String attributeTask() {
    String tache, utilisateur;
    System.out.print("Tâche à attribuer :");
    tache = sc.nextLine();
    System.out.print("A quel utilisateur attribuer la tâche :");
    utilisateur = sc.nextLine();
    return "GIVE:"+tache+":TO:"+utilisateur;
  }

  /**
   * Changement de l'état d'une tâche
   * @return Une chaine du type "CHANGE:<idTache>:TO:<etat>"
   */
  private String changeTaskStatus() {
    String tache, etatString;
    Integer etat = 0;
    System.out.print("ID de la tâche à modifier :");
    tache = sc.nextLine();
    System.out.println("Nouvel état de la tâche :");
    System.out.println("1 - TODO");
    System.out.println("2 - DOING");
    System.out.println("3 - DONE");
    do{
      System.out.print("?");
      try {
        etat = Integer.parseInt(sc.nextLine());
      }
      catch(Exception e) {}
    }while(etat < 1 || etat > 3);

    if(etat == 1) etatString = "TODO";
    else if (etat == 2) etatString = "DOING";
    else etatString = "DONE";

    return "CHANGE:"+tache+":TO:"+etatString;
  }

  /**
   * Lister les tâches d'un utilisateur
   * @return Une chaine du type "GET:TASKS:<utilisateur>"
   */
  private String listUsersTasks() {
    System.out.print("Récupération des tâches de l'utilisateur :");
    return "GET:TASKS:"+sc.nextLine();
  }

  /**
   * Affiche les informations d'une tâche
   * @return Une chaine du type "GET:TASK:<idTache>"
   */
  private String taskDescprition() {
    System.out.print("Récupération des informations de la tâche :");
    return "GET:TASK:"+sc.nextLine();
  }

  public static void main(String[] args) {
    ClientTCP client = new ClientTCP(args[0], Integer.parseInt(args[1]));
  }
}
