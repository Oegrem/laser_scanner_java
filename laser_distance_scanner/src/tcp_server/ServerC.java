package tcp_server;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CopyOnWriteArrayList;

import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;

public class ServerC extends Thread {
	private ServerSocket serverSocket;

	private InetAddress serverIp = InetAddress.getByName("localhost");
	
	public static int connCount = 0;
	
	public ServerC() throws IOException {
		serverSocket = new ServerSocket(9988);
		//serverSocket.setSoTimeout();
	}

	public synchronized void run() {		
		Distance_scanner scn = Distance_scanner.getDistanceScanner("walk");

		scn.start();
		
		scn.readData = false;
		
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

				new Thread(new ConnectionThread(clientSocket)).start();
				
				connCount++;
				if(connCount==1){
					Distance_scanner.getDistanceScanner().readData = true;
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
		if(args.length >0){
			if(args[0].equals("-s")){
				Distance_scanner.setInstantSimulation(true);
			}
		}
		
		
		
		try {
			Thread t = new ServerC();
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
