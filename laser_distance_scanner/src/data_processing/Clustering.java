package data_processing;

import java.awt.Point;
import java.util.Vector;

public class Clustering {

	
	
	public Clustering(){
		
	}
	
	public Vector<HelpCluster> cluster(Vector<Point> points, Vector<ClusterPoint> cPoints){
		Vector<HelpCluster> hCluster = new Vector<>();
		Point currentPoint = null;
		int cCount = -1;
		int searchRange = Settings.getClustering_search_range();
		int minClusterSize = Settings.getClustering_min_cluster_size();
		// Clustern
		
		// each original Point
		for(int i=0;i<cPoints.size();i++){
			// current = i
			currentPoint = cPoints.get(i);
			// look at i - x elements 
			// durchläuft bei kluster grenzen, fehlerhaften daten mehrfach
			for(int j=1;j<searchRange;j++){
				if(i-j >0){
					// if it fits to previous cluster
					if(insideThreshold(currentPoint,cPoints.get(i-j),j)){
						// current Point gehts the same cluster ID
						cPoints.get(i).clusterID = cPoints.get(i-j).clusterID;
						hCluster.get(cPoints.get(i).clusterID).addPoint(cPoints.get(i),points.get(i));
						//ret.get(cPoints.get(i).clusterID).increaseElements();
						j=searchRange;
					}
				}
			}
			// test if it was asignet to a cluster
			if(cPoints.get(i).clusterID < 0){
				// if not already assignet, create new cluster
				cCount++;
				hCluster.add(new HelpCluster());
				hCluster.get(cCount).addPoint(cPoints.get(i),points.get(i));
				//ret.add(hCluster.get(cCount).getCluster());
				hCluster.get(cCount).setID(cCount);
				hCluster.get(cCount).increaseElements();
				cPoints.get(i).clusterID = cCount;
			}
		}
		
		// cluster werte berechnen
		for(int i=0;i<hCluster.size();i++){
			hCluster.get(i).computeData();
		}
		
		// zu kleine Kluster entfernen
		// TODO entfernung zur mitte in bezug ziehen
		// das macht wirglich sinn, einfügen !
		for(int i=0;i<hCluster.size();i++){
			if(hCluster.get(i).getElementCount()<minClusterSize)
				hCluster.remove(i);
		}

		return hCluster;
	}
	
	/**
	 * Prüft ob ein punkt nah genug an einem weiteren punkt ist, sodass beide dem gleichen Cluster angehöhren
	 * es wird davon ausgegangen das die punkte nebeneinander liegen, leaps ist die anzahl an plätzen zwischen den punkten
	 * wodurch der winkel bestimmt wird der zum entfernungs ausgleich im radius wie auch zum mittelpunkt genutzt wird
	 * 
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean insideThreshold(Point a, Point b, int leaps){
		double distance = a.distance(b);
		double A = a.distance(0,0);
		double B = b.distance(0,0);
		double adjacent = 0.0;
		double[] angle = Settings.getAngle_tan_array();
		if(A<B)
			adjacent = A;
		else 
			adjacent = B;

		//  ankathete * tangenz(4) -> skalierung auf entfernung, aufspannwinkel 4°
		//  leaps 	  * leapFactor -> skalierung auf breite, schritte je 4° 
		double factor = adjacent * angle[leaps];
		if(distance < Settings.getClustering_threshold()*factor){
			return true;
		}
		return false;
	}
}
