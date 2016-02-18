package data_processing;

import java.awt.Point;
import java.util.Vector;

public class Clustering {

	
	
	public Clustering(){
		
	}

	public Vector<SimpleCluster> cluster(Vector<Long> sensorData, int areaOffset, int clusterOffset){
		Vector<SimpleCluster> clusterList = new Vector<>();
		Vector<Integer> sensorDataID = new Vector<Integer>();
		long currentSData = 0;
		int clusterCount = clusterOffset;
		int searchRange = Settings.getClustering_search_range();
		int minClusterSize = Settings.getClustering_min_cluster_size();
		for(int i=0;i<sensorData.size();i++){
			
			currentSData = sensorData.get(i);
			
			// look at i - x elements 
			// durchläuft bei kluster grenzen, fehlerhaften daten mehrfach
			for(int j=1;j<searchRange;j++){
				if(i-j >0){
					// if it fits to previous cluster
					if(insideThreshold(currentSData,sensorData.get(i-j),j)){
						int id = sensorDataID.get(i-j);
						sensorDataID.add(id);
						clusterList.get(id).increaseElementCount();
						clusterList.get(id).setLastElement(areaOffset+i);
						break;
					}
				}else break;
			}
			// test if it was asignet to a cluster
			if(sensorDataID.get(i) == null){
				clusterCount++;
				// ID, elemente anzahl, startposition, entposition, distance
				clusterList.add( new SimpleCluster(clusterCount,1,areaOffset+i,areaOffset+i,sensorDataID.get(i)));
			}
		}
		
		// zu kleine Kluster entfernen
		// die kluster entfernung spielt eine rolle, die element anzahl muss bei näheren objekten größer sein als bei weit entfertnet.
		// damit sollen kleine kluster die fälschlich erkannt werden im nahen bereich gefltert, und kleine kluster in weiter nefernung die 
		// durch geringe abtastraten wenig elemente besitzen trotzdem berücksichtigt werden
		for(int i=0;i<clusterList.size();i++){
			if(clusterList.get(i).getEelementCount() < minClusterSize*Settings.getAngle_number()*clusterList.get(i).getEelementCount()/clusterList.get(i).getDistanceSum()){
				clusterList.remove(i);
				i=i-1;
			}	
		}
		return clusterList;
	}
	
	/**
	 * @deprecated
	 * inhaltlich gleich mit cluster(vector<lon>, int, int)
	 * durchsucht die sensordaten in punktform, 
	 * @param points
	 * @param cPoints
	 * @return
	 */
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
						// current Point gets the same cluster ID
						cPoints.get(i).clusterID = cPoints.get(i-j).clusterID;
						hCluster.get(cPoints.get(i).clusterID).addPoint(cPoints.get(i),points.get(i));
						//ret.get(cPoints.get(i).clusterID).increaseElements();
						break;
					}
				}else break;
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
		// die kluster entfernung spielt eine rolle, die element anzahl muss bei näheren objekten größer sein als bei weit entfertnet.
		// damit sollen kleine kluster die fälschlich erkannt werden im nahen bereich gefltert, und kleine kluster in weiter nefernung die 
		// durch geringe abtastraten wenig elemente besitzen trotzdem berücksichtigt werden
		for(int i=0;i<hCluster.size();i++){
			if(hCluster.get(i).getElementCount() < minClusterSize*Settings.getAngle_number() / hCluster.get(i).getCenter().distance(0, 0)){
				hCluster.remove(i);
				i=i-1;
			}	
		}

		return hCluster;
	}
	
	/**
	 * @deprecated
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
		// adjacent = adjacent + adjacent/100;
		//  ankathete * tangenz(4) -> skalierung auf entfernung, aufspannwinkel 4°
		//  leaps 	  * leapFactor -> skalierung auf breite, schritte je 4° 
		double factor = adjacent * angle[leaps] * 1.5;
		//System.out.println( angle[leaps]);
		if(distance < Settings.getClustering_threshold()*factor){
			return true;
		}
		return false;
	}
	
	/**
	 * ersetzt insideThreshold(Point,Point,int). inhaltlich gleich, jedoch wird anstelle der karthesischen koordinaten als punkte, die long radius werte übergeben
	 * @param long1
	 * @param long2
	 * @param leaps
	 * @return
	 */
	private boolean insideThreshold(long long1, Long long2,int leaps) {
		double A = long1;
		double B = long2;
		double adjacent = 0.0;
		double distance = Math.sqrt(A*A+B*B-2*A*B*Math.cos(Math.toRadians(leaps*0.25))); 		// a*a + b*b - 2*a*b*cos(c)
		double[] angle = Settings.getAngle_tan_array();
		if(A<B)
			adjacent = A;
		else 
			adjacent = B;
		double factor = adjacent * angle[leaps] * 1.5;
		if(distance < Settings.getClustering_threshold()*factor){
			return true;
		}
		return false;
	}
}
