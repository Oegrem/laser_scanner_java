package code_snippets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClientThread implements Runnable {

	DatagramSocket Socket;

	@Override
	public void run() {
		
		byte[] da = new byte[1024];
		
		ByteArrayInputStream in = new ByteArrayInputStream(da);

		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(in);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {

			Socket = new DatagramSocket();

			InetAddress IPAddress = InetAddress.getByName("localhost");

			byte[] incomingData = new byte[1024];

			// Student student = new Student(1, "Bijoy", "Kerala");

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ObjectOutputStream os = new ObjectOutputStream(outputStream);

			// os.writeObject(student);

			byte[] data = outputStream.toByteArray();

			DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);

			Socket.send(sendPacket);

			System.out.println("Message sent from client");

			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);

			Socket.receive(incomingPacket);

			String response = new String(incomingPacket.getData());

			System.out.println("Response from server:" + response);
			

			


			String connString = (String) is.readObject();


		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
