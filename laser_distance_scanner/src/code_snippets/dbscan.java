package code_snippets;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import static code_snippets.clusterPoint.NOTVISITED;
import static code_snippets.clusterPoint.VISITED;
import static code_snippets.clusterPoint.NOISE;

public class dbscan {

	private int densitySize = 3;

	private double densityRange = 10;

	private Vector<Vector<clusterPoint>> clusters = new Vector<Vector<clusterPoint>>();

	public dbscan(int _densitySize, double _densityRange) {
		densityRange = _densityRange;
		densitySize = _densitySize;
	}

	public void cluster(CopyOnWriteArrayList<Point> _pVector) {
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

	}

	public void expandCluster(clusterPoint cp, Vector<clusterPoint> neighbours, Vector<clusterPoint> cluster) {
		cluster.addElement(cp);
		cp.setToCluster();

		Vector<clusterPoint> neighbourCluster = new Vector<clusterPoint>();
		
		int maxSize = neighbours.size();
		
		for(int i=0; i<maxSize;i++){
			clusterPoint nP = neighbours.elementAt(i);
			
			if (nP.getStatus() == NOTVISITED) {
				nP.setStatus(VISITED);
				neighbourCluster = nP.getNeighbours(neighbours, densityRange);
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
	
	public Vector<clusterPoint> mergeVector(Vector<clusterPoint> vc1, Vector<clusterPoint> vc2){
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
