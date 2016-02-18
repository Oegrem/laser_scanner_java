package tcp_server;

import java.awt.Point;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;
import tcp_client.UserInterface;

public class ConnectionThread implements Runnable {

	private Socket clientSocket;

	public ConnectionThread(Socket _clientSocket) {
		clientSocket = _clientSocket;
	}

	@Override
	public void run() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

			int toSend = 1;
			Object toData = null;

			TransmissionObject to = null;

			while (true) {
				try {
					switch (toSend) {
					case 1: // no Action => continue with sensordata
						CopyOnWriteArrayList<Point> cpy = new CopyOnWriteArrayList<Point>();
						cpy.addAll(SynchronListHandler.getPointVector());

						to = new TransmissionObject(1, cpy);
						break;
					case 2:
						Vector<String> v = new Vector<String>();
						v.add("walk");
						v.add("ex1");
						to = new TransmissionObject(2, v);
						break;
					case 3: // Play/Pause pressed
						if (Distance_scanner.playRecord) {
							Distance_scanner.playRecord = false;
							System.out.println("Record STOP");
						} else {
							Distance_scanner.playRecord = true;
							System.out.println("Record PLAY");
						}
						break;
					case 4: // Slider moved
						Distance_scanner.sliderValue = ((float) toData);
						break;
					case 5:
						if (!Distance_scanner.playRecord) {
							Distance_scanner.nextFrame = true;
						}
						break;
					case 6:
						if (!Distance_scanner.playRecord) {
							Distance_scanner.lastFrame = true;
						}
						break;
					}

					out.writeObject(to);

					TransmissionObject ti = ((TransmissionObject) in.readObject());

					toSend = ti.id;
					toData = ti.data;
					
				} catch (SocketException e) {
					System.out.println("SckError");
					clientSocket.close();
					ServerC.connCount--;
					if(ServerC.connCount<=0){
						ServerC.connCount = 0;
						Distance_scanner.getDistanceScanner().readData = false;
					}
					break;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
