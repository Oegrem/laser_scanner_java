package tcp_server;

import java.awt.Point;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;

import laser_distance_scanner.SynchronListHandler;

public class ConnectionThread implements Runnable {

	private Socket clientSocket;

	public ConnectionThread(Socket _clientSocket) {
		clientSocket = _clientSocket;
	}

	@Override
	public void run() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					clientSocket.getOutputStream());

			while (true) {
				try {

					CopyOnWriteArrayList<Point> cpy = new CopyOnWriteArrayList<Point>();
					cpy.addAll(SynchronListHandler.getPointVector());

					out.writeObject(cpy);
				} catch (SocketException e) {
					System.out.println("SckError");
					clientSocket.close();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
