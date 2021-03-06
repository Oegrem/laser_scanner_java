package laser_distance_scanner;

import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.kristou.urgLibJ.Connection.EthernetConnection;
import com.kristou.urgLibJ.RangeSensor.RangeSensorInformation;
import com.kristou.urgLibJ.RangeSensor.UrgDevice;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureData;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureData.Step;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureSettings;

import scanner_simulator.SData;
import scanner_simulator.SimFileHandler;

// Framework for URG Laser Distance Scanner; Multithreaded
public class Distance_scanner implements Runnable {

	private static Distance_scanner scn; // Singleton Object

	private static boolean instantSimulation = false;

	private Thread t; // Thread for running passive

	private UrgDevice device; // The LaserScanner

	private int currFreq = 25;

	private Vector<SData> sVect = new Vector<SData>(); // SData Vector for
														// Recording

	public boolean isRecorded = false; // When true the data will be recorded

	private String recordName = ""; // Filename of the recorded file

	public boolean isConnected = false; // set to true when Thread more than
											// one times started

	public static String alternativeSimFile = "ex1"; // name of recorded File
														// when
	// connection not successful

	public static boolean usingSimFile = false; // Dummy-Plug-System; set to
												// true when
	// connection not successful

	public static String sModel = "";

	public static String sSerial = "";

	public static boolean playRecord = true;

	public static boolean nextFrame = false;

	public static boolean lastFrame = false;

	public static Float sliderValue = -1.0f;

	public static long slomo = (long) 1.0;

	public static boolean readData = true;

	public static boolean simChanged = false;

	public static boolean reconnectAttempt = false;

	/*
	 * Constructor using static SimFileName
	 */
	public Distance_scanner() {
	}

	/*
	 * Constructor with name of alternative SimFile (SIMulated FILE)
	 */
	public Distance_scanner(String _alternativeSimFile) {
		alternativeSimFile = _alternativeSimFile;
	}

	/*
	 * Singleton Constructor with alternativ SimFile Name
	 */
	public static Distance_scanner getDistanceScanner(String _altSimFile) {
		if (scn == null) {
			scn = new Distance_scanner(_altSimFile);
		}
		return scn;
	}

	/*
	 * Singleton Constructor with default SimFile Name; to be called when scn
	 * exists
	 */
	public static Distance_scanner getDistanceScanner() {
		if (scn == null) {
			scn = new Distance_scanner();
		}
		return scn;
	}

	/*
	 * Enables the instantly starting of the simulation instead of waiting for
	 * the connection to fail
	 */
	public static void setInstantSimulation(boolean _instantSimul) {
		instantSimulation = _instantSimul;
	}

	public static void setSimFile(String _alternativeSimFile) {
		alternativeSimFile = _alternativeSimFile;
	}

	/*
	 * Connect to Device (when not successful enable Simulation (Dummy-Plug))
	 * when opening more Threads
	 */
	public void connect() {

		if (instantSimulation) {
			System.out.println("Connect now to Dummy-Plug-System");

			usingSimFile = true; // Enabling Recorded File as Sensor Data Input

			return;
		}

		device = new UrgDevice(new EthernetConnection());

		// Connect to the sensor
		if (device.connect("192.168.0.10")) { // Connection to IP of Sensor
			System.out.println("connected");

			// Get the sensor information
			RangeSensorInformation info = device.getInformation();
			if (info != null) {

				sModel = info.product;
				sSerial = info.serial_number;

				System.out.println("Sensor model:" + info.product);
				System.out
						.println("Sensor serial number:" + info.serial_number);
			} else {
				System.out.println("Sensor error:" + device.what());
			}

			isConnected = true; // To avoid opening Connection a second time in
								// start()

		} else {
			System.out.println("not connected: " + device.what());
			System.out.println("Connect now to Dummy-Plug-System");

			usingSimFile = true; // Enabling Recorded File as Sensor Data Input

		}

	}

	public void disconnect() {
		// Disconnect from the sensor
		device.disconnect();
	}

	/*
	 * Synchronized write Data from Sensor to exchange Vector
	 */
	public synchronized int writeData() {

		Vector<Point> pVect = new Vector<Point>();

		CaptureData data = null;
		// Data reception happens when calling capture

		data = device.capture();

		if (data != null) {

			Vector<Long> Vlong = new Vector<Long>();
			for (Step p : data.steps) {
				Vlong.add(p.distances.elementAt(0));
			}

			SynchronListHandler.setRawData(Vlong);

			for (int b = 0; b < data.steps.size(); b++) { // read all steps
				long l = data.steps.elementAt(b).distances.elementAt(0); // get
																			// distance
																			// of
																			// step

				if (l > 21 && l < 30000) { // avoid adding error-values to
											// vector

					double rad = device.index2rad(b); // get radiant of step

					long x = (long) (l * Math.cos(rad)); // calculate x out of
															// distance and
															// cosinus of
															// radiant
					long y = (long) (l * Math.sin(rad)); // calculate y out of
															// distance and
															// sinus of radiant

					Point p1 = new Point(); // Use Point object
					p1.setLocation(x, y);

					pVect.addElement(p1); // Add to Vector

				}
			}

			SynchronListHandler.setPointList(pVect);

			if (isRecorded) { // for recording of sensor data
				SData nD = new SData(); // sData combines x/y Point Vector and
										// Timestamp
				nD.pVector.addAll(pVect);
				nD.freq = currFreq;

				sVect.add(nD);
			}
		} else {

			System.out.println("Sensor error:" + device.what());

			usingSimFile = true;

			isConnected = false;

			return 255;
		}

		return 1;
	}

	/*
	 * method for starting measurment Set the continuous capture type, Please
	 * refer to the SCIP protocol for further details
	 */
	public void getDistances() {

		device.setCaptureMode(CaptureSettings.CaptureMode.GD_Capture_mode); // communication
																			// type
																			// (SCIP
																			// 2.0)
		// We set the capture type to a continuous mode so we have to start
		// the capture
		// device.stopCapture();
		device.startCapture(); // starting to capture

		while (true) { // Running until Thread gets interrupted

			if (t.isInterrupted()) {

				// device.stopCapture(); // stop Capture !!important!!

				if (isRecorded) {
					SimFileHandler sFH = new SimFileHandler(recordName);
					sFH.writeObject(sVect);
				}

				disconnect(); // disconnecting !!!VERY IMPORTANT!!! to
								// disconnect => else not able reconnecting

			} else {
				if (readData) {
					if (writeData() == 255) {
						return;
					}
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	/*
	 * alternative of getDistance() when connection failed and Dummy started
	 */
	public void getRecordedDistances() {
		SimFileHandler sFH = new SimFileHandler(alternativeSimFile); // SimFileHandler
																		// for
																		// writing
																		// and
																		// reading
																		// SimFiles

		Vector<SData> dataVector = sFH.readObject(); // reading an SimFile

		String usedSimFile = alternativeSimFile;

		while (true) { // Looping until interrupted =>
						// recorded File starts from
			// Beginning after its over

			if (!alternativeSimFile.equals(usedSimFile)) {
				sFH = new SimFileHandler(alternativeSimFile);
				dataVector = sFH.readObject();
				usedSimFile = alternativeSimFile;

				simChanged = false;
			} else if (simChanged) {
				simChanged = false;
			}

			int sDSize = dataVector.size();

			for (int i = 0; i < sDSize; i++) {

				if (!t.isInterrupted() && !simChanged) { // exiting at interrupt

					while (!playRecord) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							return;
						}
						if (nextFrame) {
							if (i >= sDSize - 1) {
								i = 0;
							} else {
								i++;
							}
							SData sD = dataVector.elementAt(i);

							SynchronListHandler.setPointList(sD.pVector);

							nextFrame = false;
						}

						if (lastFrame) {
							if (i <= 0) {
								i = sDSize - 1;
							} else {
								i -= 1;
							}

							SData sD = dataVector.elementAt(i);

							SynchronListHandler.setPointList(sD.pVector);

							lastFrame = false;
						}

						if (sliderValue != -1.0f) {
							i = ((int) ((sliderValue / 1000) * ((float) sDSize) - 1));

							SData sD = dataVector.elementAt(i);

							SynchronListHandler.setPointList(sD.pVector);

							sliderValue = -1.0f;
						}
					}

					SData sD = dataVector.elementAt(i);

					SynchronListHandler.setPointList(sD.pVector);

					if (reconnectAttempt) {
						usingSimFile = false;
						reconnectAttempt = false;
						instantSimulation = false;
						return;
					}

					try {
						Thread.sleep(sD.freq * slomo); // sleeping timestamp in
														// millis (timestamp in
														// millis between sensor
														// data)
					} catch (InterruptedException e) {
						return;
					}
				} else if (t.isInterrupted()) {
					return;
				}
			}
		}
	}

	/*
	 * NOT USE UNPROVEN => EVTL TO MUCH CALC TIME
	 */
	public static Point getNearest(Point point, Vector<Point> pointVector) {
		Point nearest = pointVector.elementAt(0);
		double minDistance = point.distance(pointVector.elementAt(0));
		for (Point p : pointVector) {
			double distance = point.distance(p);
			if (distance != 0 && distance < minDistance) {
				minDistance = distance;
				nearest = p;
			}
		}

		return nearest;
	}

	public boolean isUsingSimFile() {
		return usingSimFile;
	}

	/*
	 * Called for recording Sensor Data in SimFile "FileName", for milliseconds
	 * time
	 */
	public void recordSimFile(String FileName) {

		sVect.clear();

		isRecorded = true;
		recordName = FileName;

		start(); // Start Recording
	}

	public void recordSimFile(String FileName, int time) {

		recordSimFile(FileName);
		Timer tm = new Timer();
		TimerTask tmTask = new TimerTask() {

			@Override
			public void run() {
				stopRecording();
			}
		};

		tm.schedule(tmTask, time);

	}

	public void stopRecording() {
		interrupt();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		isRecorded = false;

		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() running multithreaded and decide if getting
	 * Distances live from sensor or getting it through recorded SimFile
	 */
	@Override
	public void run() {
		while (true) {
			if (!isConnected) { // When not connected then do it
				connect();
			}
			if (!usingSimFile) {
				getDistances();
			}
			if (usingSimFile) {
				getRecordedDistances();
			}
		}
	}

	/*
	 * Starting the thread
	 */
	public void start() {
		if (t == null) { // If Thread already exists dont start it again
			t = new Thread(this, "Scanner_Thread"); // create new Thread
			t.start(); // start the new Thread
			try {
				Thread.sleep(200); // Timeout to let sensor start measuring
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * interrupt Thread and cancel data capturing
	 */
	public void interrupt() {
		t.interrupt();
	}

	/*
	 * Resets the Settings
	 */
	public static void resetSettings() {
		usingSimFile = false;
		playRecord = true;
		nextFrame = false;
		lastFrame = false;
		sliderValue = -1.0f;
		slomo = (long) 1.0;
		readData = true;
		simChanged = false;
		reconnectAttempt = false;
	}

}
