package data_processing;

import java.awt.Point;
import java.util.Vector;

public class Clustering {
	public static double threshold = 0.7;
	
	// min 2 to skip error values
	// as low as possible, validation, low value same kluster result as high value!
	public static int searchRange = 20;
	// faktor mit dem array elemente entfernt der trashold angeglichen wird
	public static double leapFactor=1.2;
	// faktor mit dem die entfernung zum mitellpunkt über den trashold ausgeglichen wird
	private double tan4= 0.07 ;//0.06993;
	// minimale kluster größe, min 1, max egal
	public static int minClusterSize = 10;
	
	
	public Clustering(){
		
	}
	
	public Vector<HelpCluster> cluster(Vector<Point> points, Vector<ClusterPoint> cPoints){
		Vector<HelpCluster> hCluster = new Vector<>();
		Point currentPoint = null;
		int cCount = -1;
		
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
		
		/*
		Point center = null;
		Point maxCorner = null;
		Point minCorner = null;
		for(int i=0;i<hCluster.size();i++){
			center= hCluster.get(i).getCenter();
			maxCorner = hCluster.get(i).getMaxCorner();
			minCorner = hCluster.get(i).getMinCorner();
			for(int j=0;i+j<hCluster.size();i++){
				
			}
		}*/
		
		
		// zu kleine Kluster entfernen
		for(int i=0;i<hCluster.size();i++){
			if(hCluster.get(i).getElementCount()<minClusterSize)
				hCluster.remove(i);
		}

		return hCluster;
	}
	
	/**
	 * 
	 * TODO komplexe abfrage einbauen die in abhängigkeit der entfernung die schwelle berechnet
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
		if(A<B)
			adjacent = A;
		else 
			adjacent = B;

		//  ankathete * tangenz(4) -> skalierung auf entfernung, aufspannwinkel 4°
		//  leaps 	  * leapFactor -> skalierung auf breite, schritte je 4° 
		double factor = adjacent * tan4 +leaps*leapFactor;
		if(distance < threshold*factor){
			return true;
		}
		return false;
	}
}
