package tcpServerClientExample;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import code_snippets.clusterLineStrip;
import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;

public class ServerC extends Thread {
	private ServerSocket serverSocket;

	public ServerC() throws IOException {
		serverSocket = new ServerSocket(9988);
		serverSocket.setSoTimeout(100000);
	}

	public synchronized void run() {
		while (true) {
			try {
				System.out.println("Waiting for client on port "
						+ serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to "
						+ server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(
						server.getInputStream());
				System.out.println(in.readUTF());
				
				/*
				 * DataOutputStream out = new DataOutputStream(
				 * server.getOutputStream());
				 * out.writeUTF("Thank you for connecting to " +
				 * server.getLocalSocketAddress() + "\nGoodbye!");
				 */

				ObjectOutputStream out = new ObjectOutputStream(
						server.getOutputStream());

				Distance_scanner.setInstantSimulation(true);

				Distance_scanner scn = Distance_scanner
						.getDistanceScanner("walk");

				scn.start();

				while (true) {
					try {

						CopyOnWriteArrayList<Point> cpy = new CopyOnWriteArrayList<Point>();
						cpy.addAll(SynchronListHandler.getPointVector());
						
						out.writeObject(cpy);
					} catch (SocketException e) {
						System.out.println("SckError");
						server.close();
						break;
					}
				}

			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String[] args) {
		try {
			Thread t = new ServerC();
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
