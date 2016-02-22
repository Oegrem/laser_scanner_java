package data_processing;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import laser_distance_scanner.Distance_scanner;
import laser_distance_scanner.SynchronListHandler;

public class Processing {

	static boolean isWorking = false;
	
	static boolean settings = false;
	// list with the unchanged raw point data
	private  Vector<Point> pointList = new Vector<Point>();
	private  Vector<Long> longList = new Vector<Long>();
	
	// the straighting/smothing class
	private Straighten straighten = new Straighten();


	private Clustering clustering = new Clustering();
	private Vector<Cluster> cluster= new Vector<Cluster>();
	private Vector<SimpleCluster> sCluster = new Vector<SimpleCluster>();
	
	
	public Processing(){
		if(settings==false){
			settings = true;
			Settings.updateAllValues();
		}
	}
	
	/**
	 * switches the mode of the processing algorythms and starts the spezific funktion
	 */
	public synchronized void startProcess(){ 
		if(isWorking == true)
			System.out.println(isWorking);
		isWorking = true;
		if(Settings.isLongVersion()==true){
			longList.clear();
			sCluster.clear();
			longList.addAll(SynchronListHandler.getRawData());
			if(longList == null || longList.size()<1 && pointList != null){
				pointList.addAll(SynchronListHandler.getPointVector());
				Point center = new Point(0,0);
				for(int i=0;i<pointList.size();i++){
					longList.add((long)center.distance(pointList.get(i)));
				}
			}
			startLong(longList);
		}else{
			longList.clear();
			pointList.clear();
			cluster.clear();
			longList.addAll(SynchronListHandler.getRawData());
			pointList.addAll(SynchronListHandler.getPointVector());
			startPoint(pointList);
		}
		isWorking = false;
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
	 * @deprecated
	 * Analysiert die sensordaten, Die Sensordaten müssen in Kartesischen Koordinaten in form der übergebenen Pointlist sowie als Long array hinterlegt sein
	 */ // doesnt need to be synchronized => data copied already in startProcess()
	public synchronized void startPoint(Vector<Point> pointList){ 
		if(pointList == null || pointList.size()<1){
			return;
		}
		Vector<Vector<Point>> movingPointLists = new Vector<Vector<Point>>();
		Vector<ClusterPoint> clusteredPoints = new Vector<ClusterPoint>();
		Vector<HelpCluster> hCluster = new Vector<HelpCluster>();
		
		// berechnet die polarcoordinaten falls noch nicht forhanden
		if(longList.size()<2){
			longList = calcDistances(pointList);
		}

		if(Settings.isGraymap_state() == true){
			Graymap map = Graymap.getGraymap();
			Vector<int[]> moving = new Vector<int[]>();
			int start,stop;
			
			// übergibt polarkoordinaten,
			// das ergebnis ist ein vektor mit bereichen die sich bewegen, [0] = start [1] ende
			moving = (Settings.isGraymap_direct_adding())?map.addNewDataDirect(longList):map.addNewData(longList);
			
			for(int i=0;i<moving.size();i++){
				start = moving.get(i)[0];
				stop = moving.get(i)[1];
				if(stop>= pointList.size())
					stop = pointList.size()-1;
				Vector<Point> currentPointList = new Vector<Point>();
				Vector<Long> currentLongList = new Vector<>();
				for(int j=start;j<=stop;j++){
					currentPointList.add(pointList.get(j));
					currentLongList.add(longList.get(j));
				}
				movingPointLists.add(currentPointList);
			}
		}else{
			// ohne graymap alle punkte
			movingPointLists.add(pointList);
		}

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
				hCluster.addAll(clustering.cluster(pointList, clusteredPoints));
			}
			for(int i=0;i<hCluster.size();i++)
				cluster.add(hCluster.get(i).getCluster());
		}
	}
	
	/**
	 * ersetzt die startPoint funktion
	 * inhaltlich gleich, unterschied, es wird ausschließlich mit den Long werten des Sensorvektors gearbeitet. es ist keine umrechnung ins Kartesische koordinatensystem nötig.
	 * @param polar
	 */
	public synchronized void startLong(Vector<Long> polar){ 
		if(polar == null || polar.size()<1){
			return;
		}
		Vector<Vector<Long>> movingLongLists = new Vector<Vector<Long>>();
		Vector<SimpleCluster> clusterList = new Vector<SimpleCluster>();
		Vector<int[]> moving = new Vector<int[]>();
		if(Settings.isGraymap_state() == true){
			Graymap map = Graymap.getGraymap();
			int start,stop;
			
			// übergibt polarkoordinaten,
			// das ergebnis ist ein vektor mit bereichen die sich bewegen, [0] = start [1] ende
			moving = (Settings.isGraymap_direct_adding())?map.addNewDataDirect(polar):map.addNewData(polar);
			
			for(int i=0;i<moving.size();i++){
				start = moving.get(i)[0];
				stop = moving.get(i)[1];
				if(stop>= polar.size())
					stop = polar.size()-1;
				Vector<Long> currentLongList = new Vector<>();
				for(int j=start;j<=stop;j++){
					currentLongList.add(polar.get(j));
				}
				movingLongLists.add(currentLongList);
			}
		}else{
			movingLongLists.add(polar);
		}

		if(Settings.isClustering_state()== true){
			int start = 0;
			// alle nachfolgenden algorythmen gehen von zusammenhängenden daten aus, deswegen wurden die daten gesplittet und werden immer wieder pointlist übergeben
			for(int list=0;list <movingLongLists.size();list++){
				// zusammenhängendeLongListe, start der liste im original
				if(list < moving.size())
					start =moving.get(list)[0];
				Vector<SimpleCluster> current = clustering.cluster(movingLongLists.get(list));
				for(SimpleCluster sC : current){
					sC.setFirstElement(sC.getFirstElement() + start);
					sC.setLastElement(sC.getLastElement() + start);
					sC.setID(sC.getID() + clusterList.size());
				}
				clusterList.addAll(current);
			}
		}
		sCluster.addAll(clusterList);
	}
	
	/**
	 * @deprecated
	 * provides the clusterlist
	 * @return
	 */
	public Vector<Cluster> getCluster(){
		return cluster;
	}
	/**
	 * provides the SimpleClusterList
	 * @return
	 */
	public Vector<SimpleCluster> getSimpleCluster(){
		return sCluster;
	}
	
	
	
	
}
