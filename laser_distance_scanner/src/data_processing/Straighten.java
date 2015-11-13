package data_processing;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class Straighten {
	int k = 1;
	
	public Straighten(){}
	public Straighten(int _k){
		this.k = _k;
	}
	
	
	/**
	 * Straights the cPoint List, uses the point as Source
	 * cPoint is going to be Changed!
	 * if cPoint isnt tall enough, it will append the required elements
	 * 
	 * This funktion assumes, that the input data is arranged in a row
	 * 
	 * takes the average of k Points to both sides as the new point value
	 * 
	 * cPoint 0 = avrg of 0   to k
	 * cPoint 1 = avrg of 0   to x+k
	 * cPoint x = avrg of x-k to x+k 
	 * 
	 * k = 1
	 * cPoint 0 = avrg of 0   to 1
	 * cPoint 1 = avrg of 0   to 2
	 * cPoint 2 = avrg of 1   to 3
	 * cPoint x = avrg of x-1 to x+1 
	 * 
	 * TODO potenziel verbesserte gewichtung mit mehrfachen beitrag des i. elements 
	 * 
	 * @param cPoint
	 * @param point
	 */
	public void startStraighten(Vector<ClusterPoint> cPoint, Vector<Point> point){
		double x = 0;
		double y = 0;
		int count =1;
		
		if(point.size()<1)
			return;
		
		// if to less points
		if(point.size()<k)
			k = 1;
		// why ever it should by k<1, WE WONT DO IT 
		if(k<1)return;
		
		// if cPoint to small, expand
		if(cPoint.size()<point.size()){
			for(int i=cPoint.size();i<point.size();i++){
				cPoint.add(new ClusterPoint());
			}
		}
			
		// add the x and y of the next k elements
		for(int i=1;i<=k;i++){
			x= x + point.get(i+k).getX();
			y= y + point.get(i+k).getY();
			count ++;
		}
		// element 0 average 
		cPoint.get(0).setLocation(x/count, y/count);
		
		// remove the k+1 point value from behind, add the k+1 point value from the front
		// start with 1
		for(int i=1;i<point.size();i++){
			if(i-k-1>=0){
				x= x - point.get(i-1-k).getX();
				y= y - point.get(i-1-k).getY();
				count --;
			}
			if(i+k+1<point.size()){ // changed <= to < because of ArrayIndexOutOfBoundsException
				x= x + point.get(i+1+k).getX();
				y= y + point.get(i+1+k).getY();
				count++;
			}
			cPoint.get(i).setLocation(x/count, y/count);
		}
	}
}
