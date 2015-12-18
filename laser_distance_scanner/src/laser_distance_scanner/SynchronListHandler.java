package laser_distance_scanner;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

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

	private static Processing p;

	public synchronized static void setPointList(Vector<Point> _pointList) {

		copyPointVector.clear();
		copyPointVector.addAll(_pointList);

		p = new Processing();
		p.startProcess(_pointList);

		clusterVector.clear();
		clusterVector.addAll(p.getCluster());

		// clusteredPoints.clear();
		// clusteredPoints.addAll(p.getClusterPoints());

		clusterLines.clear();
		clusterLines.addAll(dbscan.getClustersAsLines(copyPointVector, 1));
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
