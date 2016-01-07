package laser_distance_scanner;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import com.kristou.urgLibJ.RangeSensor.Capture.CaptureData.Step;

import code_snippets.clusterLineStrip;
import code_snippets.dbscan;
import code_snippets.Line;
import data_processing.Cluster;
import data_processing.ClusterPoint;
import data_processing.Processing;

/*
 * Class to enable saving and getting Multithreaded lists
 */
public class SynchronListHandler {

	private static CopyOnWriteArrayList<Point> copyPointVector = new CopyOnWriteArrayList<Point>();

	private static CopyOnWriteArrayList<Cluster> copyClusterVector = new CopyOnWriteArrayList<Cluster>();

	private static CopyOnWriteArrayList<Line> copyLineVector = new CopyOnWriteArrayList<Line>();

	private static CopyOnWriteArrayList<Cluster> clusterVector = new CopyOnWriteArrayList<Cluster>();

	private static CopyOnWriteArrayList<ClusterPoint> clusteredPoints = new CopyOnWriteArrayList<ClusterPoint>();

	private static CopyOnWriteArrayList<clusterLineStrip> clusterLines = new CopyOnWriteArrayList<clusterLineStrip>();

	private static CopyOnWriteArrayList<Long> rawSensorData = new CopyOnWriteArrayList<Long>();
	
	private static Processing p;
	
	public synchronized static void setRawData(Vector<Long> vS){
		rawSensorData.clear();
		rawSensorData.addAll(vS);
	}

	public synchronized static void setPointList(Vector<Point> _pointList) {
		
		copyPointVector.clear();
		copyPointVector.addAll(_pointList);
		
		p = new Processing();
		p.startProcess(_pointList);

		clusterVector.clear();
		clusterVector.addAll(p.getCluster());

		// clusteredPoints.clear();
		// clusteredPoints.addAll(p.getClusterPoints());
		
		//clusterLines.clear();
		//clusterLines.addAll(dbscan.getClustersAsLines(copyPointVector, 0));
		
	
	}
	
	public synchronized static CopyOnWriteArrayList<Long> getRawData(){ // Sensor Data: data.elementAt(index).distances.elementAt(0); 
		return rawSensorData;											//	gibt Distanz von Step index aus: 1080 Steps auf 270 Grad
	}

	public synchronized static CopyOnWriteArrayList<Line> getLineList() {
		return copyLineVector;
	}

	public synchronized static CopyOnWriteArrayList<Cluster> getClusterVector() {
		return clusterVector;
	}

	public synchronized static CopyOnWriteArrayList<ClusterPoint> getClusteredPoints() {
		return clusteredPoints;
	}

	public synchronized static void setClusterList(
			Vector<Cluster> _clusterVector) {
		copyClusterVector.clear();
		copyClusterVector.addAll(_clusterVector);
	}

	public synchronized static CopyOnWriteArrayList<Point> getPointVector() {
		return copyPointVector; // Can be used for
								// pointList.addAll(SynchronListHandler.getPointVector());
	}

	public synchronized static CopyOnWriteArrayList<Cluster> getClusterArra() {
		return copyClusterVector;
	}

	public synchronized static CopyOnWriteArrayList<clusterLineStrip> getClusterLines() {
		return clusterLines;
	}

}
