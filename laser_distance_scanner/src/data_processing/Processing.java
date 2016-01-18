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
		Vector<Vector<Long>> movingLongLists = new Vector<Vector<Long>>();
		Vector<ClusterPoint> clusteredPoints = new Vector<ClusterPoint>();
		Vector<HelpCluster> hCluster = new Vector<HelpCluster>();
		Vector<Long> polar = new Vector<Long>();
		
		polar.addAll(SynchronListHandler.getRawData());
		// berechnet die polarcoordinaten falls noch nicht forhanden
		if(polar.size()<2){
			polar = calcDistances(pointList);
		}
		
		long t1,t2,t3,t4 = (long) 0.000000000000000001,t5=(long) 0.000000000000000001;
		
		t1=Settings.nstp.currentNanoSecondsTimestamp();
		if(Settings.isGraymap_state() == true){
			Graymap map = Graymap.getGraymap();
			Vector<int[]> moving = new Vector<int[]>();
			int start,stop;
			t4=Settings.nstp.currentNanoSecondsTimestamp();
			// übergibt polarkoordinaten,
			// das ergebnis ist ein vektor mit bereichen die sich bewegen, [0] = start [1] ende
			moving = (Settings.isGraymap_direct_adding())?map.addNewDataDirect(polar):map.addNewData(polar);
			t5=Settings.nstp.currentNanoSecondsTimestamp();
			for(int i=0;i<moving.size();i++){
				start = moving.get(i)[0];
				stop = moving.get(i)[1];
				if(stop>= pointList.size())
					stop = pointList.size()-1;
				Vector<Point> currentPointList = new Vector<Point>();
				Vector<Long> currentLongList = new Vector<>();
				for(int j=start;j<=stop;j++){
					currentPointList.add(pointList.get(j));
					currentLongList.add(polar.get(j));
				}
				movingPointLists.add(currentPointList);
				movingLongLists.add(currentLongList);
			}
		}else{
			// ohne graymap alle punkte
			movingPointLists.add(pointList);
			movingLongLists.add(polar);
		}
		t2 = Settings.nstp.currentNanoSecondsTimestamp();
		if(Settings.isClustering_state()== true){
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
				//hCluster.addAll(clustering.cluster(pointList, clusteredPoints));
				hCluster.addAll(clustering.cluster2(movingLongLists.get(list), clusteredPoints));
			}
			
			for(int i=0;i<hCluster.size();i++)
				cluster.add(hCluster.get(i).getCluster());
		}
		t3 = Settings.nstp.currentNanoSecondsTimestamp();
		
		RuntimeMeasure.getRuntimeMeasure().addNames("prozessingStartTime");
		RuntimeMeasure.getRuntimeMeasure().add(NanoSecondsTimestampProvider.getCurrentTimeAsString());
		RuntimeMeasure.getRuntimeMeasure().addNames("init");
		RuntimeMeasure.getRuntimeMeasure().add(t4-t1+"");
		RuntimeMeasure.getRuntimeMeasure().addNames("Graymap");
		RuntimeMeasure.getRuntimeMeasure().add(t5-t4+"");
		RuntimeMeasure.getRuntimeMeasure().addNames("post");
		RuntimeMeasure.getRuntimeMeasure().add(t2-t5+"");
		RuntimeMeasure.getRuntimeMeasure().addNames("Clustern");
		RuntimeMeasure.getRuntimeMeasure().add(t3-t2+"");
		// TODO cluster bekannten klustern zuordnen
		
		// TODO momentane bewegung berechnen
		
		// TODO soll position aus vorheriger bewegung und progrone mit momentan bewegung vergleichen, neue kluster position
		
		// TODO Prognose erstellen
		
		t1 = Settings.nstp.currentNanoSecondsTimestamp();
		String zeit1 = NanoSecondsTimestampProvider.getCurrentTimeAsString();
		try {
			wait(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t2 = Settings.nstp.currentNanoSecondsTimestamp();
		String zeit2 = NanoSecondsTimestampProvider.getCurrentTimeAsString();
		
		RuntimeMeasure.getRuntimeMeasure().addNames("sekunde1");
		RuntimeMeasure.getRuntimeMeasure().add(zeit1);
		RuntimeMeasure.getRuntimeMeasure().addNames("sekunde2");
		RuntimeMeasure.getRuntimeMeasure().add(zeit2);
		RuntimeMeasure.getRuntimeMeasure().addNames("sekunde");
		RuntimeMeasure.getRuntimeMeasure().add(t2-t1+"");
	}
	
	/**
	 * provides the clusterlist
	 * @return
	 */
	public Vector<Cluster> getCluster(){
		return cluster;
	}
	
	
	
	
}
