package laser_distance_scanner;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import data_processing.Cluster;

/*
 * Class to enable saving and getting Multithreaded lists
 */
public class SynchronListHandler {

	private static CopyOnWriteArrayList<Point> copyPointVector = new CopyOnWriteArrayList<Point>();

	private static CopyOnWriteArrayList<Cluster> copyClusterVector = new CopyOnWriteArrayList<Cluster>();

	public static void setPointList(Vector<Point> _pointList) {
		copyPointVector.clear();
		copyPointVector.addAll(_pointList);
	}
	
	public static void setClusterList(Vector<Cluster> _clusterVector){
		copyClusterVector.clear();
		copyClusterVector.addAll(_clusterVector);
	}
	
	public static CopyOnWriteArrayList<Point> getPointVector(){
		return copyPointVector; // Can be used for pointList.addAll(SynchronListHandler.getPointVector());
	}
	
	public static CopyOnWriteArrayList<Cluster> getClusterVector(){
		return copyClusterVector;
	}

}
