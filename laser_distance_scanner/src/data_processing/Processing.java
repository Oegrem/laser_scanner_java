package data_processing;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import laser_distance_scanner.Distance_scanner;

public class Processing {

	// list with the unchanged raw point data
	private CopyOnWriteArrayList<Point> pointList = new CopyOnWriteArrayList<Point>();
	// list with points assigned to a cluster
	private Vector<ClusterPoint> clusteredPoints = new Vector<ClusterPoint>();
	// data encoder	and likely receiver
	private Distance_scanner scanner;
	// the straighting/smothing class
	private Straighten straighten = new Straighten(3);
	
	// polish/straighten funktionality
	boolean isStraightening = true;
	// test funktionality dataStorage
	int count = 50;
	boolean storeData = true;
	
	// initialisation
	public Processing(Distance_scanner _scanner){
		scanner = _scanner;
	}
	
	/**
	 * gehts the Points from the scanner class, fills the pointlist and starts the real start prozessing
	 */
	public synchronized void startProcess(){
		if(storeData == true){
			count --;
			if(count < 0){
				dataStorage storage = new dataStorage();
				storage.storeData(scanner.getPointVector());
				storeData = false;
			}
		}
		pointList.addAll(scanner.getPointVector());
		startProcess(pointList);
	}
	
	/**
	 * starts prozessing
	 */
	public synchronized void startProcess(CopyOnWriteArrayList<Point> pointList){
		// copy the data
		pointList.addAll(scanner.getPointVector());
		
		if(isStraightening == true){
			// creats clustered Points List with straightened Date
			straighten.startStraighten(clusteredPoints,pointList);
		}else{
			// creats clustered Points List with the raw Data
			for(int i=0;i<pointList.size();i++){
				clusteredPoints.add(new ClusterPoint(pointList.get(i)));
			}
		}
		
		
		
		// TODO clustern
		
		// TODO cluster bekannten klustern zuordnen
		
		// TODO momentane bewegung berechnen
		
		// TODO soll position aus vorheriger bewegung und progrone mit momentan bewegung vergleichen, neue kluster position
		
		// TODO Prognose erstellen
		
		
		
		
	}
	
	/**
	 * provides the processed data
	 * @return Vector<ClusterPoint> clustered Points
	 */
	public Vector<ClusterPoint> getClusterPoints(){
		return clusteredPoints;
	}
}
