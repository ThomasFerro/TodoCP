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
  private Integer choix;

  /**
   * Initialise le scanner, la connexion et récupère le choix de l'utilisateur en interactif
   * @param   L'adresse du serveur
   * @param   Le port destination
   */
  public ClientTCP(String host, int port) {
    sc = new Scanner(System.in);
    preparationTCP(host, port);
    do {
      choix = menu();
      String message = requestBuilder(choix);

      String reponse = this.envoyer(message);

      gestionReponse(reponse);
    }while(choix != 0);
  }

  private void preparationTCP(String host, int port) {
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
  }

  /**
   * Envoie un message et récupère la réponse du l'hôte connecté en TCP
   * @param  message Le message à envoyer
   * @return         La réponse du serveur ou <null> si il y a eu une erreur
   */
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
    System.out.println("0 - Quitter");
  }

  /**
   * Gère le choix dans le menu
   * @return Le choix de l'utilisateur dans le menu
   */
  private Integer menu() {
    affichageMenu();
    String rep;
    choix = 0;

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
    return ((choix >= 0) && (choix < 8));
  }

  /**
   * Construit une requête selon le choix de l'utilisateur
   * @param  choix Le choix du menu
   * @return       La chaine à envoyer au serveur
   */
  private String requestBuilder(Integer choix) {
    if(choix == 0) {
      try {
        clientSocket.close();
      }
      catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
      System.exit(0);
    }
    else if(choix == 1) return declareUser();
    else if(choix == 2) return createTask();
    else if(choix == 3) return destroyTask();
    else if(choix == 4) return attributeTask();
    else if(choix == 5) return changeTaskStatus();
    else if(choix == 6) return listUsersTasks();
    else if(choix == 7) return taskDescprition();
    return "";
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

  /**
   * Redirige la réponse dans la bonne requête
   * @param rep La réponse du serveur à la requête
   */
  private void gestionReponse(String rep) {
    if((rep == null) || rep.equals("")) System.out.println("Erreur");
    else if(choix == 1) declareUserResponse(rep);
    else if(choix == 2) createTaskResponse(rep);
    else if(choix == 3) destroyTaskResponse(rep);
    else if(choix == 4) attributeTaskResponse(rep);
    else if(choix == 5) changeTaskStatusResponse(rep);
    else if(choix == 6) listUsersTasksResponse(rep);
    else if(choix == 7) taskDescpritionResponse(rep);
    else System.out.println("Erreur");
  }

  /**
   * Gestion de la réponse à la création d'un nouvel utilisateur
   * @param rep La reponse du serveur
   */
  private void declareUserResponse(String rep) {
    // rep = "CREATE:USER:OK";
    if((!checkResponse(rep, 3)) || (!(rep.split(":"))[2].equals("OK")) && !(rep.split(":"))[2].equals("KO")) {
      System.out.println("Erreur de synthaxe dans la réponse.\n");
    }
    else {
      if((rep.split(":"))[2].equals("OK")) {
        System.out.println("Nom d'utilisateur ajouté avec succes.\n");
      }
      else {
        System.out.println("Echec à l'ajout du nom d'utilisateur.\n");
      }
    }
  }

  /**
   * Gestion de la réponse à la création d'une tâche
   * @param rep La reponse du serveur
   */
  private void createTaskResponse(String rep) {
    // rep = "CREATE:TASK:1154";
    if(!checkResponse(rep, 3)) {
      System.out.println("Erreur de synthaxe dans la réponse.\n");
    }
    else {
      if((rep.split(":"))[2].equals("KO")) {
        System.out.println("Erreur à la création de la tâche.\n");
      }
      else {
        System.out.println("Tâche créée avec succès, ID : "+ (rep.split(":"))[2] +".\n");
      }
    }
  }

  /**
   * Gestion de la réponse à la destruction d'une tâche
   * @param rep La reponse du serveur
   */
  private void destroyTaskResponse(String rep) {
    // rep = "DELETE:TASK:OK";
    if((!checkResponse(rep, 3)) || (!(rep.split(":"))[2].equals("OK")) && !(rep.split(":"))[2].equals("KO")) {
      System.out.println("Erreur de synthaxe dans la réponse.\n");
    }
    else {
      if((rep.split(":"))[2].equals("OK")) {
        System.out.println("Tâche supprimée avec succès.\n");
      }
      else {
        System.out.println("Echec à la suppression de la tâche.\n");
      }
    }
  }

  /**
   * Gestion de la réponse à l'attribution d'une tâche à un utilisateur
   * @param rep La reponse du serveur
   */
  private void attributeTaskResponse(String rep) {
    // rep = "GIVE:OK";
    if((!checkResponse(rep, 2)) || (!(rep.split(":"))[1].equals("OK")) && !(rep.split(":"))[1].equals("KO")) {
      System.out.println("Erreur de synthaxe dans la réponse.\n");
    }
    else {
      if((rep.split(":"))[1].equals("OK")) {
        System.out.println("Tâche attribuée avec succès.\n");
      }
      else {
        System.out.println("Echec à l'attribution de la tâche.\n");
      }
    }
  }

  /**
   * Gestion de la réponse au changement de statut d'une tâche
   * @param rep La reponse du serveur
   */
  private void changeTaskStatusResponse(String rep) {
    // rep = "CHANGE:OK";
    if((!checkResponse(rep, 2)) || (!(rep.split(":"))[1].equals("OK")) && !(rep.split(":"))[1].equals("KO")) {
      System.out.println("Erreur de synthaxe dans la réponse.\n");
    }
    else {
      if((rep.split(":"))[1].equals("OK")) {
        System.out.println("Etat de la tâche modifiée avec succès.\n");
      }
      else {
        System.out.println("Echec à la modification de l'état de la tâche.\n");
      }
    }
  }

  /**
   * Gestion de la réponse à la requête de listing des taches d'un utilisateur
   * @param rep La reponse du serveur
   */
  private void listUsersTasksResponse(String rep) {
    // rep = "GET:TASKS:1123:1574:2546";
    if(!checkResponse(rep, 3)) {
      System.out.println("Erreur de synthaxe dans la réponse.\n");
    }
    else {
      System.out.println("Liste des tâches de l'utilisateur :");
      String[] repArray = rep.split(":");
      for(int i = 2; i < repArray.length; i++) {
        System.out.println(repArray[i]);
      }
      System.out.print("\n");
    }
  }

  /**
   * Gestion de la réponse à la requête de description d'une tâche
   * @param rep La reponse du serveur
   */
  private void taskDescpritionResponse(String rep) {
    // rep = "GET:TASK:DOING:ferrot:fevrer:finir le serveur";
    if(!checkResponse(rep, 6)) {
      System.out.println("Erreur de synthaxe dans la réponse, la tâche n'existe peut-être pas.\n");
    }
    else {
      System.out.println("Description de la tache : " + (rep.split(":"))[5]);
      System.out.println("Etat de la tâche :        " + (rep.split(":"))[2]);
      System.out.println("Créateur de la tache :    " + (rep.split(":"))[3]);
      System.out.println("Executant de la tache :   " + (rep.split(":"))[4] + "\n");
    }
  }

  private boolean checkResponse(String rep, Integer champs) {
    return rep.split(":").length >= champs;
  }

  public static void main(String[] args) {
    ClientTCP client = new ClientTCP(args[0], Integer.parseInt(args[1]));
  }
}
