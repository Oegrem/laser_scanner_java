package data_processing;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import laser_distance_scanner.Distance_scanner;

public class Processing {

	private CopyOnWriteArrayList<Point> pointList = new CopyOnWriteArrayList<Point>();
	
	private Vector<ClusterPoint> clusteredPoints = new Vector<ClusterPoint>();
	
	private Distance_scanner scanner;
	
	public Processing(Distance_scanner _scanner){
		scanner = _scanner;
	}
	
	public synchronized void startProcess(){
		pointList.addAll(scanner.getPointVector());
	}
	
	public Vector<ClusterPoint> getClusterPoints(){
		return clusteredPoints;
	}
}
