package code_snippets;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opencl.CLSVMFreeCallback;

import static code_snippets.clusterPoint.NOTVISITED;
import static code_snippets.clusterPoint.VISITED;
import static code_snippets.clusterPoint.NOISE;

public class dbscan {

	private static int densitySize = 6;

	private static int densityRange = 15;

	private static Vector<Vector<clusterPoint>> clusters = new Vector<Vector<clusterPoint>>();

	public dbscan(int _densitySize, int _densityRange) {
		densityRange = _densityRange;
		densitySize = _densitySize;
	}

	public static Vector<Vector<clusterPoint>> cluster(CopyOnWriteArrayList<Point> _pVector) {
			
		Vector<clusterPoint> cluster = new Vector<clusterPoint>();
		
		clusters.clear();

		clusterPoint cP;

		for (Point p : _pVector) {
			cP = new clusterPoint(p.getX(), p.getY());

			cluster.add(cP);
		}

		Vector<clusterPoint> neighbours = new Vector<clusterPoint>();

		for (clusterPoint cp : cluster) {
			if (cp.getStatus() == NOTVISITED) {
				cp.setStatus(VISITED);
				neighbours.clear();
				neighbours = cp.getNeighbours(cluster, densityRange);
				if (neighbours.size() >= densitySize) {
					Vector<clusterPoint> nextCl = new Vector<clusterPoint>();

					expandCluster(cp, neighbours, nextCl);

					clusters.add(nextCl);

				} else {
					cp.setStatus(NOISE);
				}
			}
		}
		
		return clusters;

	}

	public static void expandCluster(clusterPoint cp, Vector<clusterPoint> neighbours, Vector<clusterPoint> cluster) {
		int distanceRelRange = (int) (densityRange*Math.sqrt(Math.pow(cp.x/50,2)+Math.pow(cp.y/50,2)));
		
		cluster.addElement(cp);
		cp.setToCluster();

		Vector<clusterPoint> neighbourCluster = new Vector<clusterPoint>();
		
		int maxSize = neighbours.size();
		
		for(int i=0; i<maxSize;i++){
			clusterPoint nP = neighbours.elementAt(i);
			
			if (nP.getStatus() == NOTVISITED) {
				nP.setStatus(VISITED);
				neighbourCluster = nP.getNeighbours(neighbours, distanceRelRange);
				if (neighbourCluster.size() >= densitySize) {
				neighbours = mergeVector(neighbours, neighbourCluster);
				
				maxSize = neighbours.size();
				i=0;
				}
			}
			
			if(!nP.isInCluster()){
				cluster.addElement(nP);
				nP.setToCluster();
			}
			
		}
	}
	
	public static Vector<Vector<Point>> getClusters(CopyOnWriteArrayList<Point> _pVector){
		Vector<Vector<clusterPoint>> vvCP = cluster(_pVector);
		
		Vector<Point> pVector = new Vector<Point>();
 		
		for(Vector<clusterPoint> vP : vvCP){
			for(clusterPoint p : vP){
				pVector.add(p);
			}
		}
		
		for(int i=0;i<pVector.size();i++){
			
		}
		
		Vector<Vector<Point>> cluster = new Vector<Vector<Point>>();
		
		for(Point p : pVector){
			
		}
		
		return null;
		
	}
	
	public static int incSize(int inc){
		densitySize+=inc;
		return densitySize;
	}

	public static int incRange(int inc){
		densityRange+=inc;
		return densityRange;
	}
	
	public static Vector<clusterPoint> mergeVector(Vector<clusterPoint> vc1, Vector<clusterPoint> vc2){
		Vector<clusterPoint> clustVect = new Vector<clusterPoint>();
		clustVect.addAll(vc1);
		for(clusterPoint p : vc2){
			boolean isIn = false;
			for(clusterPoint pComp : vc1){
				if(pComp==p){
					isIn = true;
				}
			}
			if(!isIn){
				clustVect.add(p);
			}
		}
		
		return clustVect;
	}
	
	public Vector<Vector<clusterPoint>> getClusters(){
		return clusters;
	}

}
