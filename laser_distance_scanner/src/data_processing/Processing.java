package data_processing;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;

public class Processing {

	// list with the unchanged raw point data
	private  Vector<Point> pointList = new Vector<Point>();
	// list with points assigned to a cluster
	private Vector<ClusterPoint> clusteredPoints = new Vector<ClusterPoint>();

	// the straighting/smothing class
	private Straighten straighten = new Straighten(2);
	
	// polish/straighten funktionality
	public static boolean isStraightening = false;
	// test funktionality dataStorage
	int count = 200;
	int eachTimes = 8; 
	int times = 0;
	boolean storeData = false;
	
	private Clustering clustering = new Clustering();
	private Vector<Cluster> cluster= null;
	
	public Processing(){
		
	}
	
	// initialisation
	public Processing(Distance_scanner _scanner){
	}
	
	/**
	 * gehts the Points from the scanner class, fills the pointlist and starts the real start prozessing
	 */
	public synchronized void startProcess(){ 
		pointList.clear();
		CopyOnWriteArrayList<Point> currentPoints = new CopyOnWriteArrayList<>();
		
		//currentPoints.addAll(scanner.getPointVector()); // Vereinfacht Datentransfer über SynchronListHandler Klasse
		currentPoints.addAll(SynchronListHandler.getPointVector());
		
		//test
		//dataStorage storage2 = dataStorage.getDataStorage();
		//currentPoints.addAll(storage2.getNextPointList());
		//test
		
		
		/*
		for(int i=0;i<currentPoints.size();i++){
			pointList.add((Point)currentPoints.get(i).clone());  // recht rechenaufwendig
		}
 		*/
		pointList.addAll(currentPoints);
		/*
		if(storeData == true){
			times ++;
			if(times == eachTimes){
				times = 0;
				count --;
				if(count > 0){
					dataStorage storage = dataStorage.getDataStorage();
					Vector<Point> p = new Vector<Point>();
					p.addAll(pointList);
					//storage.storeData(scanner.getPointVector()); // saving all saved Data or only the last?
					storage.storeData(p); // saving all 50 sensor Data: change storeData(Vector<Point>) to storeData(CopyOnWriteArrayList<Point>)
				}
				else
					storeData = false;
			}
		}*/
		startProcess(pointList);
	}
	
	/**
	 * starts prozessing
	 */ // doesnt need to be synchronized => data copied already in startProcess()
	public synchronized void startProcess(Vector<Point> pointList){ 

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
		cluster = clustering.cluster(pointList, clusteredPoints);
		
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
	
	/**
	 * provides the clusterlist
	 * @return
	 */
	public Vector<Cluster> getCluster(){
		return cluster;
	}
}
