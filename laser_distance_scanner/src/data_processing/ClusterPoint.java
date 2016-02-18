package data_processing;

import java.awt.Point;

/**
 * @deprecated
 * @author Jakob
 *
 */
public class ClusterPoint extends Point{
	private static final long serialVersionUID = 1L;
	public int clusterID = -1;
	
	public ClusterPoint(){
		clusterID = -1;
	}
	public ClusterPoint(Point p){
		this.setLocation(p);
		clusterID = -1;
	}
}
