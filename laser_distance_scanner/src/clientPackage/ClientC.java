package clientPackage;

import java.net.*;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.Point;
import java.io.*;

import code_snippets.clusterLineStrip;
import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;
import tcpServerClientExample.ServerC;

public class ClientC extends Thread {

	public static CopyOnWriteArrayList<Point> copyPoint = new CopyOnWriteArrayList<Point>();

	private static Socket client;

	private static InputStream inFromServer;

	private static ObjectInputStream in;

	public void run() {
		String serverName = "localhost";

		int port = 9988;
		try {
			System.out.println("Connecting to " + serverName + " on port "
					+ port);
			client = new Socket(serverName, port);
			System.out.println("Just connected to "
					+ client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF("Hello from " + client.getLocalSocketAddress());
			inFromServer = client.getInputStream();
			in = new ObjectInputStream(inFromServer);
			// System.out.println("Server says " + in.readUTF());
			CopyOnWriteArrayList<Point> cpy = new CopyOnWriteArrayList<Point>();
			while (true) {
				try {
					CopyOnWriteArrayList<Point> c = new CopyOnWriteArrayList<Point>();

					c.addAll((CopyOnWriteArrayList<Point>) in.readObject());

					if (cpy != c) {
						Graphics.cp.clear();
						Graphics.cp.addAll(c);
						cpy = c;
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e){
					System.out.println("SckError");
				}
			}

			// client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Thread t = new ClientC();
		t.start();

		new Graphics().run();
	}
}
