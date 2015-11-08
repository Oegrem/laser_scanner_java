package laser_distance_scanner;

import java.awt.Point;
import java.util.Vector;

import com.kristou.urgLibJ.Connection.EthernetConnection;
import com.kristou.urgLibJ.RangeSensor.RangeSensorInformation;
import com.kristou.urgLibJ.RangeSensor.RangeSensorParameter;
import com.kristou.urgLibJ.RangeSensor.UrgDevice;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureData;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureData.Step;
import com.kristou.urgLibJ.RangeSensor.Capture.CaptureSettings;

public class distance_scanner {

	private UrgDevice device;

	public distance_scanner() {
		device = new UrgDevice(new EthernetConnection());
	}

	public void connect() {
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
	
	public void disconnect(){

		// Disconnect from the sensor
		device.disconnect();
	}

	public Vector<Point> getDistances(int times) {
		// Set the continuous capture type, Please refer to the SCIP
		// protocol for further details

		device.setCaptureMode(CaptureSettings.CaptureMode.MD_Capture_mode);

		
		
		// We set the capture type to a continuous mode so we have to start
		// the capture
		device.startCapture();
		
		
		Vector<Point> pointVector = new Vector<Point>();
		CaptureData data = null;
		for (int i = 0; i < times; i++) {
			// Data reception happens when calling capture
			data = device.capture();

			if (data != null) {
				System.out.println("Scan " + (i + 1) + ", steps " + data.steps.size());
				for (int b = 0; b < data.steps.size(); b++) {
					double rad = device.index2rad(b);

					long l = data.steps.elementAt(b).distances.elementAt(0);

					long x = (long) (l * Math.cos(rad));
					long y = (long) (l * Math.sin(rad));
					Point p1 = new Point();
					
					p1.setLocation(x, y);
					pointVector.addElement(p1);
					
					System.out.println("x:" + Long.toString(x) + " y:" + Long.toString(y));
				}

			} else {
				System.out.println("Sensor error:" + device.what());
			}
		}

		System.out.println(Integer.toString(pointVector.size()));

		// Stop the capture
		device.stopCapture();
		
		return pointVector;
	}

}
