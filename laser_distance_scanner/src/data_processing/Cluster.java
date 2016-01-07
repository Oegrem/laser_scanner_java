package data_processing;

import java.awt.Point;
import java.util.Vector;

public class Cluster {
	private Point center = new Point(0,0);
	// minimale und maximale x und y werte in je einem punkt um rechteck erzeugen zu können
	private Point maxCorner = new Point(0,0);
	private Point minCorner = new Point(0,0);
	// alternativ radius
	private int clusterID = -1;
	private int elements = 0;
	
	public Cluster(){}

	public void increaseElements(){
		elements ++;
	}
	public void setMaxCorner(Point p){
		maxCorner = p;
	}
	public void setCenter(Point p){
		center = p;
	}
	public void setMinCorner(Point p){
		minCorner = p;
	}
	public void setID(int ID){
		clusterID = ID;
	}
	public int getElementCount(){
		return elements;
	}
	public Point getMaxCorner(){
		return maxCorner;
	}
	public Point getCenter(){
		return center;
	}
	public Point getMinCorner(){
		return minCorner;
	}
	public int getID(){
		return clusterID;
	}
	
	public String toString(){
		return clusterID+" <"+elements+">("+center.x+"|"+center.y+")";
	}
}

/**
 * HelpCluster erweitert den Cluster um seine zugehörigen Punkte
 * Die punkte liegen in form einer liste bereits for, deswegen wird die Cluster classe simpel gehalten
 * die berechnung der ecken und des mittelpunkts erfolgt aber mit hilfe der clusterPunkte und wird in HelpCluster ausgeführt
 * Somit soll HelpCluster bei der generierung der Cluster helfen um dann die cluster zurück zu liefern
 * 
 * @author Jakob
 *
 */
class HelpCluster{
	Cluster cluster = new Cluster();
	Vector<ClusterPoint> cPointList = new Vector<ClusterPoint>();
	Vector<Point> pointList = new Vector<Point>();
	
	public HelpCluster(){}
	
	
	/**
	 * Die hauptfunktion der gesamten klasse
	 * @return
	 */
	public boolean computeData(){
		double minX=3000000,minY=3000000,maxX=-3000000,maxY=-3000000,sumX=0,sumY=0;
		ClusterPoint current = null;
		for(int i=0;i<cPointList.size();i++){
			current = cPointList.get(i);
			// summe
			sumX = sumX + current.x;
			sumY = sumY + current.y;
			// minimum 
			if(current.x<minX)
				minX = current.x;
			if(current.y<minY)
				minY = current.y;
			// maximum
			if(current.x>maxX)
				maxX = current.x;
			if(current.y>maxY)
				maxY = current.y;
		}
		// center
		Point center = new Point();
		center.setLocation(sumX/cPointList.size(),sumY/cPointList.size());
		cluster.setCenter(center);
		// minCorner
		Point min = new Point();
		min.setLocation(minX,minY);
		cluster.setMinCorner(min);
		//maxCorner
		Point max = new Point();
		max.setLocation(maxX,maxY);
		cluster.setMaxCorner(max);
		
		return true;
	}
	
	public void addPoint(ClusterPoint cP, Point p){
		pointList.add(p);
		cPointList.add(cP);
		cluster.increaseElements();
	}
	public Cluster getCluster(){
		return cluster;
	}
	
	// weiterleitung an die classe cluster
	public void increaseElements(){
		cluster.increaseElements();
	}
	public void setMaxCorner(Point p){
		cluster.setMaxCorner(p);
	}
	public void setCenter(Point p){
		cluster.setCenter(p);
	}
	public void setMinCorner(Point p){
		cluster.setMinCorner(p);
	}
	public void setID(int ID){
		cluster.setID(ID);
	}
	public int getElementCount(){
		return cluster.getElementCount();
	}
	public Point getMaxCorner(){
		return cluster.getMaxCorner();
	}
	public Point getCenter(){
		return cluster.getCenter();
	}
	public Point getMinCorner(){
		return cluster.getMinCorner();
	}
	public int getID(){
		return cluster.getID();
	}
	
	public String toString(){
		return cluster.toString();
	}
}