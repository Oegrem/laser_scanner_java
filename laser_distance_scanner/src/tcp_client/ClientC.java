package tcp_client;

import java.net.*;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.Point;
import java.io.*;

import code_snippets.clusterLineStrip;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;
import tcp_server.ServerC;
import tcp_server.TransmissionObject;

public class ClientC extends Thread {

	public static CopyOnWriteArrayList<Point> copyPoint = new CopyOnWriteArrayList<Point>();

	private static Socket client;

	private static InputStream inFromServer;

	private static ObjectInputStream in;
	
	public static int request = 1;
	
	public static float slider = -1;

	public void run() {
		String serverName = "localhost";

		int port = 9988;
		try {
			System.out.println("Connecting to " + serverName + " on port " + port);
			client = new Socket(serverName, port);
			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream outData = new DataOutputStream(outToServer);
			outData.writeUTF("Hello from " + client.getLocalSocketAddress());
			inFromServer = client.getInputStream();
			in = new ObjectInputStream(inFromServer);

			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			// System.out.println("Server says " + in.readUTF());
			CopyOnWriteArrayList<Point> cpy = new CopyOnWriteArrayList<Point>();
			while (true) {
				try {

					TransmissionObject ti = ((TransmissionObject) in.readObject());
					TransmissionObject to = null;
					switch (ti.id) {
					case 1:
						CopyOnWriteArrayList<Point> c = new CopyOnWriteArrayList<Point>();
						c.addAll((CopyOnWriteArrayList<Point>) ti.data);
						if (cpy != c) {
							Graphics.cp.clear();
							Graphics.cp.addAll(c);
							cpy = c;
						}

						break;
					case 2:
						FXMLLoader fxmlLoader = new FXMLLoader();
						Pane p = fxmlLoader.load(getClass().getResource("ControlBoard.fxml").openStream());
						Controller Controller = (Controller) fxmlLoader.getController();
						break;
					default:
						break;
					}

					switch (request) {
					case 1:
						to = new TransmissionObject(1);
						break;
					case 2:
						to = new TransmissionObject(2);
						request = 1;
						break;
					case 3:
						to = new TransmissionObject(3);
						request = 1;
						break;
					case 4:
						to = new TransmissionObject(4,slider);
						slider = -1;
						request = 1;
					}

					out.writeObject(to);

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					System.out.println("SckError");
					client.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Thread t = new ClientC();
		t.start();

		UserInterface.main(args);
	}
}
