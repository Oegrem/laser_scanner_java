package data_processing;

import java.awt.Point;
import java.util.Vector;

public class Clustering {
	private int threshold = 100;
	// min 2, to skip single error values
	private int searchRange = 5;
	private int minClusterSize = 5;
	
	public Clustering(){
		
	}
	
	public Vector<Cluster> cluster(Vector<Point> points, Vector<ClusterPoint> cPoints){
		Vector<Cluster> ret = new Vector<Cluster>();
		Vector<HelpCluster> hCluster = new Vector<>();
		Point currentPoint = null;
		int cCount = -1;
		
		// Clustern
		
		// each original Point
		for(int i=0;i<points.size();i++){
			// current = i
			currentPoint = points.get(i);
			// look at i - x elements 
			// durchläuft bei kluster grenzen, fehlerhaften daten mehrfach
			for(int j=1;j<searchRange;j++){
				if(i-j >0){
					// if it fits to previous cluster
					if(insideThreshold(currentPoint,points.get(i-j))){
						// current Point gehts the same cluster ID
						cPoints.get(i).clusterID = cPoints.get(i-j).clusterID;
						hCluster.get(cPoints.get(i).clusterID).addPoint(cPoints.get(i));
						ret.get(cPoints.get(i).clusterID).increaseElements();
						j=searchRange;
					}
				}
			}
			// test if it was asignet to a cluster
			if(cPoints.get(i).clusterID < 0){
				// if not already assignet, create new cluster
				cCount++;
				hCluster.add(new HelpCluster());
				hCluster.get(cCount).addPoint(cPoints.get(i));
				ret.add(hCluster.get(cCount).getCluster());
				ret.get(cCount).setID(cCount);
				ret.get(cCount).increaseElements();
				cPoints.get(i).clusterID = cCount;
			}
		}
		
		// zu kleine Kluster entfernen
		for(int i=0;i<ret.size();i++){
			if(ret.get(i).getElementCount()<minClusterSize)
				ret.remove(i);
		}
		
		for(int i=0;i<hCluster.size();i++){
			hCluster.get(i).computeData();
		}
		
		return ret;
	}
	
	/**
	 * 
	 * TODO komplexe abfrage einbauen die in abhängigkeit der entfernung die schwelle berechnet
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean insideThreshold(Point a, Point b){
		double distance = a.distance(b);
		if(distance < threshold){
			return true;
		}
		return false;
	}
}
