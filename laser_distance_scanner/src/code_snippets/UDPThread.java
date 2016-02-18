package code_snippets;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;

import laser_distance_scanner.SynchronListHandler;

public class UDPThread implements Runnable {

	DatagramSocket socket = null;

	InetAddress IPAddress = null;

	int port;

	public UDPThread() {
	}

	@Override
	public void run() {

		setAddress();
		
		try {

			socket = new DatagramSocket(9876);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ObjectOutputStream os = new ObjectOutputStream(outputStream);

			while (true) {

				CopyOnWriteArrayList<Point> cpy = new CopyOnWriteArrayList<Point>();
				cpy.addAll(SynchronListHandler.getPointVector());

				os.writeObject(cpy);

			}

		} catch (SocketException e) {

			e.printStackTrace();

		} catch (IOException i) {

			i.printStackTrace();

		}

	}

	private void setAddress() {

		try {

			byte[] incomingData = new byte[1024];

			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);

			socket.receive(incomingPacket);

			byte[] data = incomingPacket.getData();

			ByteArrayInputStream in = new ByteArrayInputStream(data);

			ObjectInputStream is = new ObjectInputStream(in);


			String connString = (String) is.readObject();

			System.out.println("String object received = " + connString);


			IPAddress = incomingPacket.getAddress();

			port = incomingPacket.getPort();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
