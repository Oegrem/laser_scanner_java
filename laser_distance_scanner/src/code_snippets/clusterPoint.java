package code_snippets;

import java.awt.Point;
import java.util.Vector;

public class clusterPoint extends Point {

	public static final int NOTVISITED = 0;
	
	public static final int VISITED = 1;
	
	public static final int NOISE = 2;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int status = NOTVISITED;
	
	private boolean belongToCluster = false;
	
	public clusterPoint(){
		super();
	}
	
	public clusterPoint(double _x, double _y){
		super();
		setLocation(_x, _y);
		
		status = NOTVISITED;
	}
	
	public clusterPoint(Point p1){
		super();
		
		setLocation(p1);
	}
	
	public Vector<clusterPoint> getNeighbours(Vector<clusterPoint> _cluster, double _densityRange){
		Vector<clusterPoint> neighbours = new Vector<clusterPoint>();
		for(clusterPoint cp : _cluster){
			if(cp != this && this.distance(cp) <= _densityRange){
				neighbours.add(cp);
			}
		}
		
		return neighbours;
	}
	
	public void setStatus(int _newStatus){
		status = _newStatus;
	}
	
	public int getStatus(){
		return status;
	}
	
	public boolean isInCluster(){
		return belongToCluster;
	}
	
	public void setToCluster(){
		belongToCluster = true;
	}
	
	
}
