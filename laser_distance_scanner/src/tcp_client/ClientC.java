package tcp_client;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.Point;
import java.io.*;

import data_processing.SimpleCluster;
import javafx.scene.paint.Color;
import tcp_server.TransmissionObject;

public class ClientC extends Thread {

	public static CopyOnWriteArrayList<Point> copyPoint = new CopyOnWriteArrayList<Point>();

	private static Socket client;

	private static InputStream inFromServer;

	private static ObjectInputStream in;
	
	public static boolean isSimul = false;
	
	public static String sModel = "";
	
	public static String sSerial = "";

	public static int request = 1;

	public static float slider = -1;

	public static Color grColor = new Color(1, 0, 0, 1);
	
	public static String label = "";

	private String serverName = "141.69.97.178";

	private int port = 9988;
	
	public static String logString = "";

	public ClientC(String _name, int _port) {
		serverName = _name;
		port = _port;
	}

	@SuppressWarnings("unchecked")
	public void run() {

		try {
			System.out.println("Connecting to " + serverName + " on port "
					+ port);
			client = new Socket(serverName, port);
			System.out.println("Just connected to "
					+ client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream outData = new DataOutputStream(outToServer);
			outData.writeUTF("Hello from " + client.getLocalSocketAddress());
			inFromServer = client.getInputStream();
			in = new ObjectInputStream(inFromServer);

			ObjectOutputStream out = new ObjectOutputStream(
					client.getOutputStream());
			
			CopyOnWriteArrayList<Point> cpy = new CopyOnWriteArrayList<Point>();

			CopyOnWriteArrayList<SimpleCluster> simpC = new CopyOnWriteArrayList<SimpleCluster>();

			
			Object data = null;

			while (true) {
				try {

					TransmissionObject ti = ((TransmissionObject) in
							.readObject());
					TransmissionObject to = null;
					switch (ti.id) {
					case 0:
						HashMap<String, String> hM = new HashMap<String, String>();
						
						hM.putAll((HashMap<String, String>) ti.data);
						
						for(Map.Entry<String, String> ent : hM.entrySet()){
							switch(ent.getKey()){
							case "Simul":
								isSimul = true;
								break;
							case "SModel":
								sModel = ent.getValue();
								break;
							case "SSerial":
								sSerial = ent.getValue();
								break;
							}
						}
						if(!(sModel.equals("")&&sSerial.equals(""))){
						logString+="Sensor Model:"+sModel+"\n"+"Sensor Serial:"+sSerial+"\n";
						}
						
						break;
					case 1:
		//				CopyOnWriteArrayList<Point> c = new CopyOnWriteArrayList<Point>();
						
		//				CopyOnWriteArrayList<SimpleCluster> sC = new CopyOnWriteArrayList<SimpleCluster>();
						
		//				Vector<Object> oV = new Vector<Object>();
						
		//				oV.addAll((Vector<Object>) ti.data);
						
		//				c.addAll((CopyOnWriteArrayList<Point>) oV.get(0));
						
		//				sC.addAll((CopyOnWriteArrayList<SimpleCluster>) oV.get(1));
						
						CopyOnWriteArrayList<Point> c = new CopyOnWriteArrayList<Point>();
						
						c.addAll((CopyOnWriteArrayList<Point>) ti.data);
						
						if (cpy != c) {
							Graphics.cp.clear();
							Graphics.cp.addAll(c);
							cpy = c;
						}
						
	//					if (simpC != sC) {
	//						Graphics.sP.clear();
	//						Graphics.sP.addAll(sC);
	//						simpC = sC;
	//					}

						break;
					case 2:
						Vector<String> vS = new Vector<String>();
						vS.addAll((Vector<String>) ti.data);
						System.out.println(vS.get(1));
						label = vS.get(1);
						break;
					default:
						break;
					}

					switch (request) {
					case 4:
						data = slider;
						slider = -1;
						break;
					}

					to = new TransmissionObject(request, data);
					data = null;
					request = 1;

					out.writeObject(to);

				} catch (ClassNotFoundException e) {
					e.printStackTrace();

				} catch (SocketException e) {

					System.out.println("SckError");
					request = 7;
					client.close();
				} catch (EOFException e){
					System.out.println("Disconnected from Server");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			logString += e.getMessage()+"\n";
		}
	}

	public static void main(String[] args) {

		UserInterface.main(args);

	}
}
