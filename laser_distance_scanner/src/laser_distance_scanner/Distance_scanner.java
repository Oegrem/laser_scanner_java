package laser_distance_scanner;

import java.awt.Point;
import java.util.Vector;

import com.kristou.urgLibJ.Connection.EthernetConnection;
import com.kristou.urgLibJ.RangeSensor.RangeSensorInformation;
import com.kristou.urgLibJ.RangeSensor.UrgDevice;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureData;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureSettings;

import data_processing.Cluster;
import data_processing.Processing;
import scanner_simulator.SData;
import scanner_simulator.SimFileHandler;

// Framework for URG Laser Distance Scanner; Multithreaded
public class Distance_scanner implements Runnable {

	private Thread t; // Thread for running passive

	private UrgDevice device; // The LaserScanner

	private Vector<Point> pointVector = new Vector<Point>(); // Synchron Object
																// to Write and
																// Read

	Vector<Cluster> clusterVector = new Vector<Cluster>();
	
	private Vector<SData> sVect = new Vector<SData>(); // SData Vector for
														// Recording

	private int readTimes = 0; // Read times to save drawing-time in Graphics

	private boolean isRecorded = false; // When true the data will be recorded

	private String recordName = ""; // Filename of the recorded file

	private boolean isConnected = false; // set to true when Thread more than
											// one times started

	private String alternativeSimFile = "sim1"; // name of recorded File when
												// connection not successful

	private boolean usingSimFile = false; // Dummy-Plug-System; set to true when
											// connection not successful

	/*
	 * Constructor with name of alternative SimFile (SIMulated FILE)
	 */
	public Distance_scanner(String _alternativeSimFile) {
		alternativeSimFile = _alternativeSimFile;
	}

	/*
	 * Connect to Device (when not successful enable
	 * Simulation (Dummy-Plug)) when opening more
	 * Threads
	 */
	public void connect() { 
		device = new UrgDevice(new EthernetConnection());
		// Connect to the sensor
		if (device.connect("192.168.0.10")) { // Connection to IP of Sensor
			System.out.println("connected");

			// Get the sensor information
			RangeSensorInformation info = device.getInformation();
			if (info != null) {
				System.out.println("Sensor model:" + info.product);
				System.out.println("Sensor serial number:" + info.serial_number);
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
	 * Synchronized write Data from
	 * Sensor to exchange Vector
	 */
	public synchronized void writeData() {

		readTimes++; // Increment readTimes to be compared to avoid calculations
						// of the same data

		CaptureData data = null;
		// Data reception happens when calling capture
		data = device.capture();

		pointVector.clear(); // empty vector

		if (data != null) {
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

					pointVector.addElement(p1); // Add to Vector

				}
			}

			if (isRecorded) { // for recording of sensor data
				SData nD = new SData(); // sData combines x/y Point Vector and
										// Timestamp
				nD.pVector.addAll(pointVector);
				nD.timestamp = data.timestamp;
				sVect.add(nD);
			}
		} else {
			System.out.println("Sensor error:" + device.what());
		}
	}

	/*
	 * method for starting measurment
	 * Set the continuous capture type, Please refer to the SCIP
	 * protocol for further details
	 */
	public void getDistances() {

		device.setCaptureMode(CaptureSettings.CaptureMode.MD_Capture_mode); // communication
																			// type
																			// (SCIP
																			// 2.0)

		// We set the capture type to a continuous mode so we have to start
		// the capture
		device.startCapture(); // starting to capture

		while (true) { // Running until Thread gets interrupted
			if (!t.isInterrupted()) {
				writeData();
			} else {
				device.stopCapture(); // stop Caputre !!important!!

				if (isRecorded) {
					SimFileHandler sFH = new SimFileHandler(recordName);
					sFH.writeObject(sVect);
				}

				disconnect(); // disconnecting !!!VERY IMPORTANT!!! to
								// disconnect => else not able reconnecting
				return;
			}

		}
	}

	/*
	 * alternative of getDistance() when
	 * connection failed and Dummy
	 * started
	 */
	public void getRecordedDistances() { 

		SimFileHandler sFH = new SimFileHandler(alternativeSimFile); // SimFileHandler
																		// for
																		// writing
																		// and
																		// reading
																		// SimFiles
		
		Vector<SData> dataVector = sFH.readObject(); // reading an SimFile

		long tStmp = System.currentTimeMillis(); // TimeStamp to see loop time

		while (true) { // Looping until interrupted => recorded File starts from Beginning after its over
			System.out.println(Long.toString(System.currentTimeMillis() - tStmp) + " ms"); // calculating
																							// loop
																							// time
			tStmp = System.currentTimeMillis();

			for (SData sD : dataVector) { // each Sensor Data Vector will be
											// written in pointVector

				readTimes++; // avoiding more calculation than needed

				if (!t.isInterrupted()) { // exiting at interrupt
					pointVector = sD.pVector;
					Processing p = new Processing(this);
					p.startProcess();
					clusterVector = p.getCluster();
					try {
						Thread.sleep(sD.timestamp); // sleeping timestamp in
													// millis (timestamp in
													// millis between sensor
													// data)
					} catch (InterruptedException e) {
						return;
					}
				} else {
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

	public Vector<Cluster> getClusterVector(){
		return clusterVector;
	}
	
	public Vector<Point> getPointVector() {
		return pointVector;
	}

	public int getReadTimes() {
		return readTimes;
	}

	/*
	 * Called for recording Sensor Data in SimFile "FileName", for milliseconds time
	 */
	public void recordSimFile(String FileName, int milliseconds) {
		isRecorded = true;
		recordName = FileName;

		start(); // Start Recording
		try {
			Thread.sleep(milliseconds); // wait Time while recording
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		interrupt(); // interrupt recording
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * running multithreaded and decide if getting Distances live from sensor
	 * or getting it through recorded SimFile
	 */
	@Override
	public void run() {
		if (!usingSimFile) {
			getDistances();
		} else {
			getRecordedDistances();
		}

	}

	
	/*
	 * Starting the thread
	 */
	public void start() {
		if (!isConnected) { // When not connected then do it
			connect();
		}
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

}
