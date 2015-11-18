package laser_distance_scanner;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import code_snippets.dbscan;
import code_snippets.line_extraction;
import code_snippets.Line;
import code_snippets.clusterPoint;
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
	
	private static Vector<Cluster> clusterVector = new Vector<Cluster>();
	
	private static Vector<ClusterPoint> clusteredPoints = new Vector<ClusterPoint>();
	
	public static void setPointList(Vector<Point> _pointList) {
		copyPointVector.clear();
		copyPointVector.addAll(_pointList);
		
		CopyOnWriteArrayList<Point> ppp = new CopyOnWriteArrayList<Point>();
		
		copyLineVector.clear();
		copyLineVector.addAll(line_extraction.calcLine(copyPointVector));		
		
		//Processing p = new Processing();
		//p.startProcess();
		//clusterVector = p.getCluster();
		//clusteredPoints = p.getClusterPoints();
		
	
	}
	
	public static CopyOnWriteArrayList<Line> getLineList(){
		return copyLineVector;
	}
	
	public static Vector<Cluster> getClusterVector(){
		return clusterVector;
	}
	
	public static Vector<ClusterPoint> getClusteredPoints(){
		return clusteredPoints;
	}
	
	public static void setClusterList(Vector<Cluster> _clusterVector){
		copyClusterVector.clear();
		copyClusterVector.addAll(_clusterVector);
	}
	
	public static CopyOnWriteArrayList<Point> getPointVector(){
		return copyPointVector; // Can be used for pointList.addAll(SynchronListHandler.getPointVector());
	}
	
	public static CopyOnWriteArrayList<Cluster> getClusterArra(){
		return copyClusterVector;
	}

}
