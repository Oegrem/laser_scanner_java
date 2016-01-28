package tcp_server;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CopyOnWriteArrayList;

import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;

public class ServerC extends Thread {
	private ServerSocket serverSocket;

	public ServerC() throws IOException {
		serverSocket = new ServerSocket(9988);
		//serverSocket.setSoTimeout();
	}

	public synchronized void run() {

		/*Distance_scanner.setInstantSimulation(true);

		Distance_scanner scn = Distance_scanner.getDistanceScanner("walk");

		scn.start();
		*/
		
		Distance_scanner.getDistanceScanner().start();

		while (true) {
			try {
				System.out.println("Waiting for client on port "
						+ serverSocket.getLocalPort() + "...");
				Socket clientSocket = serverSocket.accept();
				System.out.println("Just connected to "
						+ clientSocket.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(
						clientSocket.getInputStream());
				System.out.println(in.readUTF());

				/*
				 * DataOutputStream out = new DataOutputStream(
				 * server.getOutputStream());
				 * out.writeUTF("Thank you for connecting to " +
				 * server.getLocalSocketAddress() + "\nGoodbye!");
				 */

				new Thread(new ConnectionThread(clientSocket)).start();
				;

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
