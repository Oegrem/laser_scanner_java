package data_processing;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;


import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;

public class Processing {

	// list with the unchanged raw point data
	private  Vector<Point> pointList = new Vector<Point>();

	// the straighting/smothing class
	private Straighten straighten = new Straighten();


	private Clustering clustering = new Clustering();
	private Vector<Cluster> cluster= new Vector<Cluster>();
	
	public Processing(){
		Settings.updateAllValues();
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
		Vector<Vector<Point>> movingPointLists = new Vector<Vector<Point>>();
		Vector<ClusterPoint> clusteredPoints = new Vector<ClusterPoint>();
		Vector<HelpCluster> hCluster = new Vector<HelpCluster>();
		
		if(Settings.isGraymap_state() == true){
			Graymap map = Graymap.getGraymap();
			Vector<Long> polar = new Vector<Long>();
			Vector<int[]> moving = new Vector<int[]>();
			int start,stop;
			
			polar.addAll(SynchronListHandler.getRawData());
			// berechnet die polarcoordinaten falls noch nicht forhanden
			if(polar.size()<2){
				polar = calcDistances(pointList);
			}
			
			// übergibt polarkoordinaten,
			// das ergebnis ist ein vektor mit bereichen die sich bewegen, [0] = start [1] ende
			moving = map.addNewData(polar);
			for(int i=0;i<moving.size();i++){
				start = moving.get(i)[0];
				stop = moving.get(i)[1];
				if(stop>= pointList.size())
					stop = pointList.size()-1;
				Vector<Point> currentPointList = new Vector<Point>();
				for(int j=start;j<=stop;j++){
					currentPointList.add(pointList.get(j));
				}
				movingPointLists.add(currentPointList);
			}
		}else{
			// ohne graymap alle punkte
			movingPointLists.add(pointList);
		}
		
		
		// alle nachfolgenden algorythmen gehen von zusammenhängenden daten aus, deswegen wurden die daten gesplittet und werden immer wieder pointlist übergeben
		for(int list=0;list <movingPointLists.size();list++){
			pointList = movingPointLists.get(list);
			
			clusteredPoints.removeAllElements();
			for(int i=0;i<pointList.size();i++){
				clusteredPoints.add(new ClusterPoint(pointList.get(i)));
			}
			if(Settings.isStraigthen()){
				switch(Settings.getStraigthen_type()){
					case arithmetic:
						straighten.ArithmetischesMittel(clusteredPoints, pointList);
						break;
					case harmonic:
						straighten.HarmonischeMittel(clusteredPoints, pointList);
						break;
					case geometric:
						straighten.GeometrischeMittel(clusteredPoints, pointList);
						break;
				}
			}
			// clustern
			hCluster.addAll(clustering.cluster(pointList, clusteredPoints));
		}
		
		for(int i=0;i<hCluster.size();i++)
			cluster.add(hCluster.get(i).getCluster());
		
		// TODO cluster bekannten klustern zuordnen
		
		// TODO momentane bewegung berechnen
		
		// TODO soll position aus vorheriger bewegung und progrone mit momentan bewegung vergleichen, neue kluster position
		
		// TODO Prognose erstellen
		
		
		
		
	}
	
	/**
	 * provides the clusterlist
	 * @return
	 */
	public Vector<Cluster> getCluster(){
		return cluster;
	}
}
