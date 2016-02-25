package tcp_server;

import java.awt.Point;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import data_processing.SimpleCluster;
import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;
import scanner_simulator.SimFileHandler;
import tcp_client.UserInterface;

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
			ObjectInputStream in = new ObjectInputStream(
					clientSocket.getInputStream());

			int toSend = 0;
			Object toData = null;

			TransmissionObject to = null;

			while (true) {
				try {
					switch (toSend) {
					case 0: // Start Info

						HashMap<String, String> hM = new HashMap<String, String>();

						if (Distance_scanner.usingSimFile) {
							hM.put("Simul", "true");
						} else {
							hM.put("SModel", Distance_scanner.sModel);
							hM.put("SSerial", Distance_scanner.sSerial);
							
						}
						hM.put("Mode", "Sensor");
						hM.put("Mode1", "ex1");
						hM.put("Mode2", "walk");

						to = new TransmissionObject(0, hM);
						break;
					case 1: // no Action => continue with sensordata

						Vector<Object> oV = new Vector<Object>();

						CopyOnWriteArrayList<Point> cpy = new CopyOnWriteArrayList<Point>();
						cpy.addAll(SynchronListHandler.getPointVector());

						CopyOnWriteArrayList<SimpleCluster> simC = new CopyOnWriteArrayList<SimpleCluster>();
						simC.addAll(SynchronListHandler.getSimpleCluster());

						oV.add(cpy);

						oV.add(simC);

						to = new TransmissionObject(1, oV);
						break;
					case 3: // Play/Pause Commands
						switch ((Integer) toData) {
						case 1:
							if (Distance_scanner.playRecord) {
								Distance_scanner.playRecord = false;
								System.out.println("Record STOP");
							} else {
								Distance_scanner.playRecord = true;
								System.out.println("Record PLAY");
							}
							break;
						case 2:
							if (!Distance_scanner.playRecord) {
								Distance_scanner.lastFrame = true;
							}
							break;
						case 3:
							if (!Distance_scanner.playRecord) {
								Distance_scanner.nextFrame = true;
							}
							break;
						default:
							System.out.println("Wrong data in Type:" + toSend
									+ " " + toData);
						}

						break;
					case 4: // Slider moved
						Distance_scanner.sliderValue = ((float) toData);
						break;
					case 5:
						if (((String) toData).equals("Sensor")) {
							Distance_scanner.reconnectAttempt = true;
						
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {

							}
							if(Distance_scanner.getDistanceScanner().isConnected){
								to = new TransmissionObject(2, "Sensor");
							}
						} else {
							Distance_scanner.alternativeSimFile = (String) toData;
							Distance_scanner.simChanged = true;
							to = new TransmissionObject(2, (String) toData);
						}
						break;
					case 6:
						Distance_scanner.slomo = (long) ((float) toData);
						System.out.println(((float) toData));
						break;
					case 7:
						clientSocket.close();
						ServerC.connCount--;
						if (ServerC.connCount <= 0) {
							ServerC.connCount = 0;
							Distance_scanner.resetSettings();
							Distance_scanner.readData = false;

						}
						return;
					case 8:
						if(!Distance_scanner.usingSimFile){
							if(!Distance_scanner.getDistanceScanner().isRecorded){
							Distance_scanner.getDistanceScanner().recordSimFile((String) toData);
							}else{
							Distance_scanner.getDistanceScanner().stopRecording();	
							}
						}
						break;
					default:
						System.out.println(Integer.toString(toSend));
						break;
					}

					out.writeObject(to);

					TransmissionObject ti = ((TransmissionObject) in
							.readObject());

					toSend = ti.id;
					toData = ti.data;

				} catch (SocketException e) {
					System.out.println("SckError");

					ServerC.connCount--;
					if (ServerC.connCount <= 0) {
						ServerC.connCount = 0;
						Distance_scanner.resetSettings();
						Distance_scanner.readData = false;
					}

					clientSocket.close();

					return;

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
