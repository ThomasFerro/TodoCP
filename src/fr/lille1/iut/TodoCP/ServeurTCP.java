package fr.lille1.iut.helloworld.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.UUID;

public class ServeurTCP {
	private ServerSocket serveurSocket = null;
	private MonRunnable monRunnable = null;
	private List<Tache> taskList = null;

	public ServeurTCP(int port) {
		try {
			serveurSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		taskList = new ArrayList<Tache>();
	}

	public void miseEnService() {
		Socket unClient = null;

		while (true) {
			try {
				unClient = serveurSocket.accept();
				monRunnable = new MonRunnable(unClient);
				new Thread(monRunnable).start();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static void main(String[] args) {
		ServeurTCP serveur = new ServeurTCP(Integer.parseInt(args[0]));
		serveur.miseEnService();
	}

	public class MonRunnable implements Runnable {
	  private Socket unClient;
		private String user;

	  public MonRunnable(Socket unClient) {
	    this.unClient = unClient;
			this.user = null;
	  }

	  public void run() {
			PrintWriter envoi = null;
			BufferedReader reception = null;
			String requete = null;
			String response = null;
			do {
				try {
					envoi = new PrintWriter(unClient.getOutputStream(), true);
					reception = new BufferedReader(new InputStreamReader(unClient.getInputStream()));
					requete = reception.readLine();
					response = responseBuilder(requete);
					System.out.println(requete);
					System.out.println(response);
					envoi.println(response);
					// envoi.println(requete + " World !");
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}while(requete != null);
	  }

		/**
	   * Construit une réponse selon le type de requête
	   * @param  requete La requete du client
	   * @return      	 La réponse à envoyer au client
	   */
		private String responseBuilder(String requete) {
			if((requete.split(":"))[0].equals("CREATE") && (requete.split(":"))[1].equals("USER"))
				return declareUser((requete.split(":"))[2]);
			else if((requete.split(":"))[0].equals("CREATE") && (requete.split(":"))[1].equals("TASK"))
				return createTask((requete.split(":"))[2]);
			else if((requete.split(":"))[0].equals("DELETE") && (requete.split(":"))[1].equals("TASK"))
				return destroyTask(Integer.parseInt((requete.split(":"))[2]));
			else if((requete.split(":"))[0].equals("GIVE"))
				return attributeTask(Integer.parseInt((requete.split(":"))[1]), (requete.split(":"))[3]);
			else if((requete.split(":"))[0].equals("CHANGE"))
				return changeTaskStatus(Integer.parseInt((requete.split(":"))[1]), (requete.split(":"))[3]);
			else if((requete.split(":"))[0].equals("GET") && (requete.split(":"))[1].equals("TASKS"))
				return listUsersTasks((requete.split(":"))[2]);
			else if((requete.split(":"))[0].equals("GET") && (requete.split(":"))[1].equals("TASK"))
				return taskDescprition(Integer.parseInt((requete.split(":"))[2]));
			else return "Erreur lors de la requête";
		}

		/**
		 * Déclare un utilisateur
		 * @return Une chaine de type "CREATE:USER:'Statut'"
		 */
		private String declareUser(String name) {
			if (name.equals("")){
				return "CREATE:USER:KO";
			}
			this.user=name;
			return "CREATE:USER:OK";
	  }

		/**
		 * Créer une nouvelle tâche sur le serveur
     * @return Une chaine de type "CREATE:TASK:'id'" ou "CREATE:TASK:KO:'code'"
		 */
		private String createTask(String description) {
			if (user==null || description.equals("")){
				return "CREATE:TASK:KO";
			}
			int id = Math.abs(UUID.randomUUID().hashCode());
			Tache task = new Tache(id,user,description);
			taskList.add(task);
			return "CREATE:TASK:"+id;
		}

		/**
		 * Supprime une tâche du serveur
		 * @return Une chaine du type "DELETE:TASK:OK" ou "DELETE:TASK:KO"
		 */
		private String destroyTask(int id) {
			Iterator<Tache> itr = taskList.iterator();
			while (itr.hasNext()) {
				Tache task = itr.next();
				if (task.getId()==id) {
					taskList.remove(task);
					return "DELETE:TASK:OK";
				}
			}
			return "DELETE:TASK:KO";
		}

		/**
		 * Attribue une tâche à un utilisateur
		 * @return Une chaine du type "GIVE:'code'"
		 */
		private String attributeTask(int id, String name) {
			Iterator<Tache> itr = taskList.iterator();
			while (itr.hasNext()) {
				Tache task = itr.next();
				if (task.getId()==id) {
					task.setUser(name);
					return "GIVE:OK";
				}
			}
			return "GIVE:KO";
		}

		/**
	   * Changement de l'état d'une tâche
	   * @return Une chaine du type "CHANGE:OK"
	   */
	  private String changeTaskStatus(int id, String etat) {
			Iterator<Tache> itr = taskList.iterator();
			while (itr.hasNext()) {
				Tache task = itr.next();
				if (task.getId()==id) {
					if (etat.equals(Status.TODO.toString()))
						task.setStatus(Status.TODO);
					else if (etat.equals(Status.DOING.toString()))
						task.setStatus(Status.DOING);
					else if (etat.equals(Status.DONE.toString()))
						task.setStatus(Status.DONE);
					return "CHANGE:OK";
				}
			}
			return "CHANGE:KO";
	  }

		/**
		 * Lister les tâches d'un utilisateur
		 * @return Une chaine du type "GET:TASKS:'liste des taches'"
		 */
		private String listUsersTasks(String name) {
			String taches = "GET:TASKS";
			Iterator<Tache> itr = taskList.iterator();
			while (itr.hasNext()) {
				Tache task = itr.next();
				if (!name.equals("") && (task.getCreator().equals(name) || task.getUser().equals(name)))
					taches += ":"+task.getId();
				else if (task.getCreator().equals(user) || task.getUser().equals(user)) {
					taches += ":"+task.getId();
				}
			}
			return taches;
		}

		/**
		 * Affiche les informations d'une tâche
		 * @return Une chaine du type "GET:TASK:'etat':'createur':'executant':'description'"
		 */
		private String taskDescprition(int id) {
			Iterator<Tache> itr = taskList.iterator();
			String etat,createur,executant,description;
			while (itr.hasNext()) {
				Tache task = itr.next();
				if (task.getId()==id) {
					etat = task.getStatus().toString();
					createur = task.getCreator();
					executant = task.getUser();
					description = task.getDescription();
					return "GET:TASK:"+etat+":"+createur+":"+executant+":"+description;
				}
			}
			return "GET:TASK:KO";
		}
	}

}
