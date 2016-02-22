package data_processing;

import java.awt.Point;
import java.util.Vector;
import laser_distance_scanner.SynchronListHandler;

public class Processing {

	static boolean isWorking = false;
	
	static boolean settings = false;
	// list with the unchanged raw point data
	private  Vector<Point> pointList = new Vector<Point>();
	private  Vector<Long> longList = new Vector<Long>();


	private Clustering clustering = new Clustering();
	private Vector<SimpleCluster> sCluster = new Vector<SimpleCluster>();
	
	
	public Processing(){
		// einmaliges ausführen der updatefunktion die die settings classe aktualisiert
		if(settings==false){
			settings = true;
			Settings.updateAllValues();
		}
	}
	
	/**
	 * switches the mode of the processing algorythms and starts the spezific funktion
	 */
	public synchronized void startProcess(){ 
		if(Settings.isClustering_state()== false)
			return;
		
		if(isWorking == true)
			System.out.println("Processing, startProcess, doppelt ausgeführt");
		isWorking = true;

		longList.clear();
		sCluster.clear();
		longList.addAll(SynchronListHandler.getRawData());
		// aufnahme ist in karthesischen koordinaten, muss erst orginaler messdatenvektor berechnet werden
		if(longList == null || longList.size()<1){
			pointList.addAll(SynchronListHandler.getPointVector());
			Point center = new Point(0,0);
			for(int i=0;i<pointList.size();i++){
				longList.add((long)center.distance(pointList.get(i)));
			}
		}
		startLong(longList);
		isWorking = false;
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
	 * provides the SimpleClusterList
	 * @return
	 */
	public Vector<SimpleCluster> getSimpleCluster(){
		return sCluster;
	}
	
	
	
	
}
