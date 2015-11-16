package data_processing;

import java.awt.Point;
import java.util.Vector;

public class Clustering {
	private int threshold = 100;
	private int minThreshold = 10;
	private double thresholdRatio = 1.3;
	// min 2, to skip single error values
	private int searchRange = 5;
	private int minClusterSize = 5;
	
	public Clustering(){
		
	}
	
	public Vector<Cluster> cluster(Vector<Point> points, Vector<ClusterPoint> cPoints){
		Vector<Cluster> ret = new Vector<Cluster>();
		Vector<Cluster> cluster = new Vector<Cluster>();
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
						cluster.get(cPoints.get(i).clusterID).increaseElements();
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
				cluster.add(hCluster.get(cCount).getCluster());
				cluster.get(cCount).setID(cCount);
				cluster.get(cCount).increaseElements();
				cPoints.get(i).clusterID = cCount;
			}
		}
		
		for(int i=0;i<hCluster.size();i++){
			hCluster.get(i).computeData();
		}
		
		// zu kleine Kluster entfernen
		for(Cluster c: cluster){
			if(c.getElementCount()>minClusterSize)
				ret.add(c);
		}
		
		
		return ret;
	}
	
	/**
	 * 
	 * TODO komplexe abfrage einbauen die in abhängigkeit der entfernung die schwelle berechnet
	 * 
	 * grundidee verhältnis der entfernung der punkte, zum 
	 * 
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean insideThreshold(Point a, Point b){
		double distance = a.distance(b);
		
		// wenn sehr nach beieinander
		if(distance < minThreshold)
			return true;
		
		Point center = new Point(0,0);
		double distance0A = center.distance(a);
		double distance0B = center.distance(b);
		double distance0AB = (distance0A+distance0B) /2;
				
		// wenn entfernung größer als entfernung zur mitte sowiso weg
		if(distance > distance0AB){
			return false;
		}
		
		if(distance0A/distance < thresholdRatio)
			return true;
		return false;
	}
}
