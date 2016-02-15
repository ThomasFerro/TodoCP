package fr.lille1.iut.helloworld.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurTCP {
	private ServerSocket serveurSocket = null;
	private MonRunnable monRunnable = null;

	public ServeurTCP(int port) {
		try {
			serveurSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
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

	  public MonRunnable(Socket unClient) {
	    this.unClient = unClient;
	  }

	  public void run() {
			PrintWriter envoi = null;
			BufferedReader reception = null;
			try {
				envoi = new PrintWriter(unClient.getOutputStream(), true);

				reception = new BufferedReader(
											new InputStreamReader(unClient.getInputStream()));

				String message = reception.readLine();
				// try{
				// 	Thread.sleep(2500);
				// } catch(Exception e) {
				// 	e.printStackTrace();
				// }
				envoi.println(message + " World !");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
	  }
	}

}
