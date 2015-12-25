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
	private Vector<Cluster> cluster= new Vector<Cluster>();
	
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

		currentPoints.addAll(SynchronListHandler.getPointVector());
		
		pointList.addAll(currentPoints);

		startProcess(pointList);
	}
	
	private Vector<Long> calcDistances(Vector<Point> pointList){
		Vector<Long> step = new Vector<Long>();
		Point center = new Point(0, 0);
		for(int i=0;i<pointList.size();i++){
			step.add((long) center.distance(pointList.get(i)));
		}
		return step;
	}
	
	/**
	 * starts prozessing
	 */ // doesnt need to be synchronized => data copied already in startProcess()
	public synchronized void startProcess(Vector<Point> pointList){ 

		Graymap map = Graymap.getGraymap();
		Vector<Long> current = new Vector<Long>();
		//current.addAll(SynchronListHandler.getRawData());
		// berechnet die polarcoordinaten falls noch nicht forhanden
		if(current.size()<2){
			current = calcDistances(pointList);
		}
		// übergibt polarkoordinaten,
		// das ergebnis ist eine liste mit punkten die sich laut graymap bewegt haben
		current = map.addNewData(current);
		Vector<Point> newPointList = new Vector<>();
		for(int i=0;i<current.size();i++){
			newPointList.add(pointList.get(Integer.parseInt(current.get(i)+"")));
		}
		pointList = newPointList;
		System.out.println("pointlist size "+pointList.size() + " (nur als sich bewegend erkannte punkte)");
		
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
		Vector<HelpCluster> hCluster = clustering.cluster(pointList, clusteredPoints);
		
		//testzwäcke TODO entfernen
		Cluster testCluster = new Cluster();
		testCluster.setID(9999);
		testCluster.setMaxCorner(new Point(100, 100));
		testCluster.setMinCorner(new Point(-100, -100));
		cluster.add(testCluster);
		
		for(int i=0;i<hCluster.size();i++)
			cluster.add(hCluster.get(i).getCluster());
		//System.out.println("clusterlist size "+cluster.size());
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
