package laser_distance_scanner;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import com.kristou.urgLibJ.RangeSensor.Capture.CaptureData.Step;

import data_processing.Cluster;
import data_processing.ClusterPoint;
import data_processing.Processing;
import data_processing.SimpleCluster;

/*
 * Class to enable saving and getting Multithreaded lists
 */
public class SynchronListHandler {

	private static CopyOnWriteArrayList<Point> copyPointVector = new CopyOnWriteArrayList<Point>();

	private static CopyOnWriteArrayList<Long> rawSensorData = new CopyOnWriteArrayList<Long>();
	
	private static CopyOnWriteArrayList<SimpleCluster> simCluster = new CopyOnWriteArrayList<SimpleCluster>();
	
	private static Processing p;

	public synchronized static void setRawData(Vector<Long> vS){
		rawSensorData.clear();
		rawSensorData.addAll(vS);
	}

	public synchronized static void setPointList(Vector<Point> _pointList) {
		
		copyPointVector.clear();
		copyPointVector.addAll(_pointList);

		p = new Processing();
		p.startProcess();

		simCluster.clear();
		simCluster.addAll(p.getSimpleCluster());
	
	}
	
	public synchronized static CopyOnWriteArrayList<Long> getRawData(){ // Sensor Data: data.elementAt(index).distances.elementAt(0); 
		return rawSensorData;											//	gibt Distanz von Step index aus: 1080 Steps auf 270 Grad
	}

	public synchronized static CopyOnWriteArrayList<Point> getPointVector() {
		return copyPointVector; // Can be used for
								// pointList.addAll(SynchronListHandler.getPointVector());
	}
	
	public synchronized static CopyOnWriteArrayList<SimpleCluster> getSimpleCluster() {
		return simCluster;
	}

}
