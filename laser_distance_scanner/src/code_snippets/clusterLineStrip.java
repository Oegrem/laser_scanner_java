package code_snippets;

import java.awt.Point;
import java.util.Vector;

public class clusterLineStrip {

	private Vector<Point> lineStripPoints = new Vector<Point>();
	
	private int clusterId = -1;
	
	public clusterLineStrip(){
		
	}
	
	public void setLineStripPoints(Vector<Point> lsP){
		lineStripPoints.clear();
		lineStripPoints.addAll(lsP);
	}
	
	public void addLineStripPoints(Vector<Point> vnP){
		lineStripPoints.addAll(vnP);
	}
	
	public void addLineStripPoint(Point nP){
		if(!lineStripPoints.contains(nP)){
			lineStripPoints.add(nP);
		}
	}
	
	public boolean isSimiliar(clusterLineStrip _cls){ // Implement better test for similiarity
		if(getCenter().distance(_cls.getCenter())<70){
			return true;
		}
		return false;
	}
	
	public void setClusterId(int _cId){
		clusterId = _cId;
	}
	
	public void setClusterId(clusterLineStrip clS){
		clusterId = clS.getClusterId();
	}
	
	public Point getCenter(){
		Point p = new Point();
		int x = 0;
		int y = 0;
		for(Point c : lineStripPoints){
			x+=c.x;
			y+=c.y;
		}
		
		p.x = x/lineStripPoints.size();
		p.y = y/lineStripPoints.size();
		
		return p;
	}
	
	public Vector<Point> getLineStripPoints(){
		return lineStripPoints;
	}
	
	public int getClusterId(){
		return clusterId;
	}
	
}
