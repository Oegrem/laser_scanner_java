package code_snippets;

import java.awt.Point;
import java.util.Vector;

public class Line {

	private Point p1;
	private Point p2;
	
	public Line(){
		
	}
	
	public Line(Point _p1, Point _p2){
		p1 = _p1;
		p2 = _p2;
	}
	
	public void setLocation(Point _p1, Point _p2){
		p1 = _p1;
		p2 = _p2;
	}
	
	public void setP1(Point _p1){
		p1 = _p1;
	}
	
	public void setP2(Point _p2){
		p2 = _p2;
	}
	
	public Point getP1(){
		return p1;
	}
	
	public Point getP2(){
		return p2;
	}
	
	public Vector<clusterPoint> getMissingPointsOnList(Vector<clusterPoint> vP){
		Vector<clusterPoint> missingPoints = new Vector<clusterPoint>();
		missingPoints.addAll(vP);
		
		for(clusterPoint p : vP){
			if(getDistance(p)<20){
				missingPoints.remove(p);

			}
		}

		
		return missingPoints;
	}
	
	public Vector<clusterPoint> getPointsOnList(Vector<clusterPoint> vP){
		Vector<clusterPoint> missingPoints = new Vector<clusterPoint>();
		missingPoints.addAll(vP);
		
		for(clusterPoint p : vP){
			if(getDistance(p)>20){
				missingPoints.remove(p);

			}
		}

		
		return missingPoints;
	}
	
	public clusterPoint getFarest(Vector<clusterPoint> vcP){
		double maxDis = 0;
		
		clusterPoint farest = new clusterPoint();
		
		for(clusterPoint cP : vcP){
			double nextDis = getDistance(cP);
			if(nextDis>=maxDis){
				
				farest = cP;
				
				maxDis = nextDis;
			}
		}
		
		
		if(maxDis==0 && farest.x==0 && farest.y==0){ // Error => take Point exactly between Line Start and End
			farest = new clusterPoint(getMiddlePoint());
		}
		
		
		return farest;
	}
	
	public static Line getLineFromPoints(Vector<clusterPoint> cPV){
		Line line = new Line();
		
		clusterPoint startPoint = new clusterPoint(0,0);
		
		clusterPoint endPoint = new clusterPoint(0,0);
		
		double maxDistance = 0;

		
		for(clusterPoint scP : cPV){
			for(clusterPoint ecP : cPV){

				double dis = scP.distance(ecP);
				if(dis>maxDistance){
					if(startPoint != scP){
					startPoint = scP;
					}
					endPoint = ecP;
					maxDistance = dis;
				}
			
			}
		}
		
		line.setLocation(startPoint, endPoint);

		return line;
		
	}
	
	public Vector<Point> getEdgedLine(Vector<clusterPoint> cP, clusterLineStrip clS, int maxDepth, int depth){

		Vector<Point> vL = new Vector<Point>();

		clusterPoint far = getFarest(cP);

		if(depth>=maxDepth){
			clS.addLineStripPoint(p1);
			clS.addLineStripPoint(far);
			clS.addLineStripPoint(p2);

			return vL;
		}
		
		Vector<clusterPoint> cP1 = new Vector<clusterPoint>();
		Vector<clusterPoint> cP2 = new Vector<clusterPoint>();
		boolean firsthalf = true;
		for(clusterPoint cc : cP){
			
			if(firsthalf){
				cP1.add(cc);
			}else{
				cP2.add(cc);
			}
			if(cc==far){
				firsthalf = false;
			}
		}
		
		vL.addAll(new Line(p1,far).getEdgedLine(cP1, clS, maxDepth, depth+1));
		vL.addAll(new Line(far,p2).getEdgedLine(cP2, clS, maxDepth, depth+1));
		
		return vL;
	}
	
	public static clusterLineStrip getClusterLineVectors(Vector<clusterPoint> cP, int precision){
		clusterLineStrip lV = new clusterLineStrip();
		
		Line baseLine = getLineFromPoints(cP);
		
		lV.addLineStripPoint(baseLine.p1);
		
		baseLine.getEdgedLine(cP, lV,precision, 0);

		lV.addLineStripPoint(baseLine.p2);
		
		return lV;
	}
	
	public int getPointsBetween(Vector<clusterPoint> cP){
		int num = 0;
		
		for(clusterPoint c : cP){
			if(getDistance(c)<40){
				num++;
			}
		}
		
		return num;
	}
	
	public Point getMiddlePoint(){
		Point mP = new Point();
		
		mP.setLocation((p1.x+p2.x)/2,(p1.y+p2.y)/2);
		
		return mP;
	}
	
	public double getDistance(Point disPoint){
		double distance = 0;
		
		distance = (Math.abs(((p2.y-p1.y)*disPoint.x)-((p2.x-p1.x)*disPoint.y)+(p2.x*p1.y)-(p2.y*p1.x)))/p1.distance(p2);
		
		return distance;
	}
	
}
