package laser_distance_scanner;

import java.awt.Point;
import java.util.Vector;

import com.kristou.urgLibJ.Connection.EthernetConnection;
import com.kristou.urgLibJ.RangeSensor.RangeSensorInformation;
import com.kristou.urgLibJ.RangeSensor.UrgDevice;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureData;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureSettings;

import data_processing.ClusterPoint;
import data_processing.dataStorage;

public class Distance_scanner implements Runnable {

	private Thread t;
	private UrgDevice device;

	private Vector<Point> pointVector = new Vector<Point>();
	
	private Vector<Point> smoothedPoints = new Vector<Point>();

	private Vector<ClusterPoint> clusteredPoints = new Vector<ClusterPoint>();
	
	private int readTimes = 0;
	
	public Distance_scanner() {

	}

	public void connect() {
		device = new UrgDevice(new EthernetConnection());
		// Connect to the sensor
		if (device.connect("192.168.0.10")) {
			System.out.println("connected");

			// Get the sensor information
			RangeSensorInformation info = device.getInformation();
			if (info != null) {
				System.out.println("Sensor model:" + info.product);
				System.out.println("Sensor serial number:" + info.serial_number);
			} else {
				System.out.println("Sensor error:" + device.what());
			}

		} else {
			System.out.println("not connected: " + device.what());
		}

	}

	public void disconnect() {

		// Disconnect from the sensor
		device.disconnect();
	}

	public synchronized void writeData() {
		
		readTimes++;
		
		CaptureData data = null;
		// Data reception happens when calling capture
		data = device.capture();
		
		pointVector.clear();
		
		if (data != null) {
			// System.out.println("Scan " + (i + 1) + ", steps " +
			// data.steps.size());
			for (int b = 0; b < data.steps.size(); b++) {
				long l = data.steps.elementAt(b).distances.elementAt(0);

				if (l > 21 && l < 30000) {

					double rad = device.index2rad(b);

					long x = (long) (l * Math.cos(rad));
					long y = (long) (l * Math.sin(rad));
					Point p1 = new Point();

					
					p1.setLocation(x, y);
					pointVector.addElement(p1);

					// System.out.println("x:" + Long.toString(x) + " y:" +
					// Long.toString(y));
				}
			}

		} else {
			System.out.println("Sensor error:" + device.what());
			// potentiell könnte man hier zu testzwecken gespeicherte sensordaten in den vector packen, Gruß Jakob
			// TODO ACHTUNG TEST
			dataStorage dS = dataStorage.getDataStorage();
			pointVector = dS.getNextPointList();
			// das war irgendwie ungünstig readTimes++;
			// TODO ACHTUNG TEST
		}
	}

	public void getDistances(int times) {
		// Set the continuous capture type, Please refer to the SCIP
		// protocol for further details

		device.setCaptureMode(CaptureSettings.CaptureMode.MD_Capture_mode);

		// We set the capture type to a continuous mode so we have to start
		// the capture
		device.startCapture();

		if (times == -1) {
			while (true) {
				if(!t.isInterrupted()){
					writeData();
				} else {
					device.stopCapture();
					disconnect();
					return;
				}

			}
		} else {
			for (int i = 0; i < times; i++) {
				if(!t.isInterrupted()){
					writeData();
				} else {
					device.stopCapture();
					disconnect();
					return;
				}
			}
		}

		// System.out.println(Integer.toString(pointVector.size()));

		// Stop the capture
		device.stopCapture();
	}

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

	public Vector<Point> getPointVector() {
		return pointVector;
	}
	
	public int getReadTimes(){
		return readTimes;
	}

	@Override
	public void run() {

		getDistances(-1);

	}

	public void start() {
		if (t == null) {
			t = new Thread(this, "Scanner_Thread");
			t.start();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void interrupt() {
		t.interrupt();

	}

}
