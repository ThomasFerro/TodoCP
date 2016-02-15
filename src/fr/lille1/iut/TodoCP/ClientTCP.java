package fr.lille1.iut.helloworld.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ClientTCP {
  private Socket clientSocket = null;
  private PrintWriter envoi = null;
  private BufferedReader reception = null;
  private Scanner sc;

  public ClientTCP(String host, int port) {
    sc = new Scanner(System.in);
    Integer choice = menu();
    String message = requestBuilder(choice);
    // try {
    //   clientSocket = new Socket(host, port);
    // } catch (UnknownHostException e) {
    //   e.printStackTrace();
    //   System.exit(1);
    // } catch (IOException e) {
    //   e.printStackTrace();
    //   System.exit(1);
    // }
    //
    // try {
    //   envoi = new PrintWriter(clientSocket.getOutputStream(), true);
    //   reception = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    // } catch (IOException e) {
    //   e.printStackTrace();
    //   System.exit(1);
    // }
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
  public static void main(String[] args) {
    ClientTCP client = new ClientTCP(args[0], Integer.parseInt(args[1]));

    // String reponse = client.envoyer("Hello");
    // System.out.println(reponse);
  }

  private void affichageMenu() {
    System.out.println("--- Client TCP : Système de gestion de tâches ---");
    System.out.println("--------------------- Menu ----------------------");

    System.out.println("1 - Déclaration d'un utilisateur");
    System.out.println("2 - Création d'une tâche");
    System.out.println("3 - Destruction d'une tâche");
    System.out.println("4 - Attribution d'une tâche");
    System.out.println("5 - Changement de l'état d'une tâche");
    System.out.println("6 - Liste des tâches d'un utilisateur");
    System.out.println("7 - Informations d'une tâche");
  }

  private Integer menu() {
    affichageMenu();
    String rep;
    Integer choix;

    do {
      System.out.print("Entrez votre choix :");
      rep = sc.nextLine();
      choix = Integer.parseInt(rep);
    }while(!isValidChoice(choix));
    return choix;
  }

  private boolean isValidChoice(Integer choix) {
    return ((choix > 0) && (choix < 8));
  }

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

  private String declareUser() {
    System.out.println("declareUser à implementer");
    return "";
  }

  private String createTask() {
    System.out.println("createTask à implementer");
    return "";
  }

  private String destroyTask() {
    System.out.println("destroyTask à implementer");
    return "";
  }

  private String attributeTask() {
    System.out.println("attributeTask à implementer");
    return "";
  }

  private String changeTaskStatus() {
    System.out.println("changeTaskStatus à implementer");
    return "";
  }

  private String listUsersTasks() {
    System.out.println("listUsersTasks à implementer");
    return "";
  }

  private String taskDescprition() {
    System.out.println("taskDescprition à implementer");
    return "";
  }

}
