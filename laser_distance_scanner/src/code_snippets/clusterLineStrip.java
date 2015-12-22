package code_snippets;

import java.awt.Point;
import java.io.Serializable;
import java.util.Vector;

public class clusterLineStrip implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector<Point> lineStripPoints = new Vector<Point>();

	private int clusterId = -1;
	
	public boolean recognised = false;
	
	public boolean moving = false;

	public clusterLineStrip() {

	}

	public void setLineStripPoints(Vector<Point> lsP) {
		lineStripPoints.clear();
		lineStripPoints.addAll(lsP);
	}

	public void addLineStripPoints(Vector<Point> vnP) {
		lineStripPoints.addAll(vnP);
	}

	public void addLineStripPoint(Point nP) {
		if (!lineStripPoints.contains(nP)) {
			lineStripPoints.add(nP);
		}
	}

	public boolean isSimiliar(clusterLineStrip _cls) { // Implement better test
														// for similiarity
		double distance = getCenter().distance(_cls.getCenter());
		
		if (distance < 25) {
			return true;
		} else if(distance < 70){
			moving = true;
			return true;			
		}

		return false;
	}
	
	public int getNextCluster(Vector<clusterLineStrip> _Vcls){
		int cID = -1;
		
		double minDistance = -1;
		for(clusterLineStrip cls : _Vcls){
			double distance = getCenter().distance(cls.getCenter());
			if(distance<minDistance || minDistance==-1){
				minDistance = distance;
				if(minDistance<100){
					cID = cls.getClusterId();
				}
			}
		}
		
		return cID;
	}

	public void setClusterId(int _cId) {
		clusterId = _cId;
	}

	public void setClusterId(clusterLineStrip clS) {
		clusterId = clS.getClusterId();
	}

	public Point getCenter() {
		Point p = new Point();
		int x = 0;
		int y = 0;
		for (Point c : lineStripPoints) {
			x += c.x;
			y += c.y;
		}

		p.x = x / lineStripPoints.size();
		p.y = y / lineStripPoints.size();

		return p;
	}

	public Vector<Point> getLineStripPoints() {
		return lineStripPoints;
	}

	public int getClusterId() {
		return clusterId;
	}

}
