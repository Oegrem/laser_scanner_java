package data_processing;

import java.awt.Point;
import java.util.Vector;

public class Straighten {
	
	public Straighten(){}
	
	
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
		int count =0;
		int smoothingFactor = Settings.getStraightenFactor();
		
		if(point.size()<1)
			return;
		
		// if to less points
		if(point.size()<smoothingFactor)
			smoothingFactor = 1;
		// why ever it should by k<1, WE WONT DO IT 
		//if(k<1)return;
		
		// if cPoint to small, expand
		if(cPoint.size()<point.size()){
			for(int i=cPoint.size();i<point.size();i++){
				cPoint.add(new ClusterPoint());
			}
		}
		
		if(point.size()<=smoothingFactor)
			smoothingFactor -= (smoothingFactor-point.size())+1;
			
		// add the x and y of the next k elements
		for(int i=0;i<=smoothingFactor;i++){
			x= x + point.get(i).getX();
			y= y + point.get(i).getY();
			count ++;
		}
		// add element 0 again
		x= x + point.get(0).getX();
		y= y + point.get(0).getY();
		count ++;
		
		// element 0 average 
		cPoint.get(0).setLocation(x/count, y/count);
		
		// remove the k+1 point value from behind, add the k+1 point value from the front
		// start with 1
		for(int i=1;i<point.size();i++){
			if((i-1-smoothingFactor)>=0){
				x= x - point.get(i-1-smoothingFactor).getX();
				y= y - point.get(i-1-smoothingFactor).getY();
				count --;
			}
			if(i+smoothingFactor<point.size()){ // changed <= to < because of ArrayIndexOutOfBoundsException
				x= x + point.get(i+smoothingFactor).getX();
				y= y + point.get(i+smoothingFactor).getY();
				count++;
			}
			x= x + point.get(i).getX();
			y= y + point.get(i).getY();
			x= x - point.get(i-1).getX();
			y= y - point.get(i-1).getY();
			cPoint.get(i).setLocation(x/count, y/count);
		}
		return;
	}
	public void startStraighten0(Vector<ClusterPoint> cPoint, Vector<ClusterPoint> point){
		double x = 0;
		double y = 0;
		int count =0;
		int smoothingFactor = Settings.getStraightenFactor();
		
		if(point.size()<1)
			return;
		
		// if to less points
		if(point.size()<smoothingFactor)
			smoothingFactor = 1;
		// why ever it should by k<1, WE WONT DO IT 
		//if(k<1)return;
		
		// if cPoint to small, expand
		if(cPoint.size()<point.size()){
			for(int i=cPoint.size();i<point.size();i++){
				cPoint.add(new ClusterPoint());
			}
		}
		
		if(point.size()<=smoothingFactor)
			smoothingFactor -= (smoothingFactor-point.size())+1;
			
		// add the x and y of the next k elements
		for(int i=0;i<=smoothingFactor;i++){
			x= x + point.get(i).getX();
			y= y + point.get(i).getY();
			count ++;
		}
		// add element 0 again
		x= x + point.get(0).getX();
		y= y + point.get(0).getY();
		count ++;
		
		// element 0 average 
		cPoint.get(0).setLocation(x/count, y/count);
		
		// remove the k+1 point value from behind, add the k+1 point value from the front
		// start with 1
		for(int i=1;i<point.size();i++){
			if((i-1-smoothingFactor)>=0){
				x= x - point.get(i-1-smoothingFactor).getX();
				y= y - point.get(i-1-smoothingFactor).getY();
				count --;
			}
			if(i+smoothingFactor<point.size()){ // changed <= to < because of ArrayIndexOutOfBoundsException
				x= x + point.get(i+smoothingFactor).getX();
				y= y + point.get(i+smoothingFactor).getY();
				count++;
			}
			x= x + point.get(i).getX();
			y= y + point.get(i).getY();
			x= x - point.get(i-1).getX();
			y= y - point.get(i-1).getY();
			cPoint.get(i).setLocation(x/count, y/count);
		}
		return;
	}
	public void startStraighten3(Vector<ClusterPoint> cPoint,Vector<Point> point){
		double x = 0;
		double y = 0;
		int count =0;
		int smoothingFactor = Settings.getStraightenFactor();
		
		if(cPoint.size()<1)
			return;
		if(cPoint.size()!=point.size())
			return;
		
		// if to less points
		if(point.size()<=smoothingFactor)
			smoothingFactor -= (smoothingFactor-point.size())+1;
			
		// add the x and y of the next k elements
		for(int i=0;i<=smoothingFactor;i++){
			x= x + point.get(i).getX();
			y= y + point.get(i).getY();
			count ++;
		}
		// add element 0 again
		x= x + point.get(0).getX();
		y= y + point.get(0).getY();
		count ++;
		
		// element 0 average 
		cPoint.get(0).setLocation(x/count, y/count);
		count +=smoothingFactor;
		// remove the k+1 point value from behind, add the k+1 point value from the front
		// start with 1
		for(int i=1;i<point.size();i++){
			if((i-1-smoothingFactor)>=0){
				x= x - point.get(i-1-smoothingFactor).getX();
				y= y - point.get(i-1-smoothingFactor).getY();
				count --;
			}
			if(i+smoothingFactor<point.size()){ // changed <= to < because of ArrayIndexOutOfBoundsException
				x= x + point.get(i+smoothingFactor).getX();
				y= y + point.get(i+smoothingFactor).getY();
				count++;
			}
			x= x + point.get(i).getX();
			y= y + point.get(i).getY();
			x= x - point.get(i-1).getX();
			y= y - point.get(i-1).getY();
			cPoint.get(i).setLocation(x/count, y/count);
		}
		return;
	}
	public void ArithmetischesMittel(Vector<ClusterPoint> cPoint, Vector<Point> point){
		int smoothingFactor = Settings.getStraightenFactor();
		if(cPoint.size()!=point.size())
			return;
		
		if(smoothingFactor>cPoint.size())
			smoothingFactor-= smoothingFactor-cPoint.size()+1;
		
		double currentX = 0,currentY = 0;
		int count = 0;
		int currentK = 0;
		for(int i=0;i<cPoint.size();i++){
			currentK = smoothingFactor;
			currentX = 0;
			currentY = 0;
			count = 0;
			if(i-smoothingFactor<0)
				currentK =i;
			if(i+currentK>=cPoint.size())
				currentK = cPoint.size() - i-1;
			if(currentK>smoothingFactor)
				currentK = smoothingFactor;
			//if(currentK>2)
			//	System.out.println(currentK);
			for(int j=0;j<=currentK;j++){
				currentX += point.get(i+j).getX();
				currentY += point.get(i+j).getY();
				currentX += point.get(i-j).getX();
				currentY += point.get(i-j).getY();
				count +=2;
			}
			if(count != 0)
				cPoint.get(i).setLocation(currentX/count, currentY/count);
		}
		return;
	}
	
	public void HarmonischeMittel(Vector<ClusterPoint> clusterPoints, Vector<Point> sourcePoints){
		int smoothingFactor = Settings.getStraightenFactor();
		if(clusterPoints.size()!=sourcePoints.size())
			return;
		
		if(smoothingFactor>sourcePoints.size())
			smoothingFactor-= smoothingFactor-sourcePoints.size()+1;
		
		double currentX = 0,currentY = 0;
		int count = 0;
		int currentK = 0;
		for(int i=0;i<sourcePoints.size();i++){
			currentK = smoothingFactor;
			currentX = 0;
			currentY = 0;
			count = 0;
			if(i-smoothingFactor<0)
				currentK =i;
			if(i+currentK>=sourcePoints.size())
				currentK = sourcePoints.size() - i-1;
			if(currentK>smoothingFactor)
				currentK = smoothingFactor;

			for(int j=0;j<=currentK;j++){
				currentX += 1/(sourcePoints.get(i+j).getX());
				currentY += 1/(sourcePoints.get(i+j).getY());
				currentX += 1/(sourcePoints.get(i-j).getX());
				currentY += 1/(sourcePoints.get(i-j).getY());
				count +=2;
			}
			if(count != 0)
				clusterPoints.get(i).setLocation(count/(currentX), count/currentY);
		}
		return;
	}
	
	public void GeometrischeMittel(Vector<ClusterPoint> clusterPoints, Vector<Point> sourcePoints){
		int smoothingFactor = Settings.getStraightenFactor();
		if(clusterPoints.size()!=sourcePoints.size())
			return;
		
		if(smoothingFactor>sourcePoints.size())
			smoothingFactor-= smoothingFactor-sourcePoints.size()+1;
		
		double currentX = 0,currentY = 0;
		int count = 0;
		int currentK = 0;
		for(int i=0;i<sourcePoints.size();i++){
			currentK = smoothingFactor;
			currentX = 1;
			currentY = 1;
			count = 0;
			if(i-smoothingFactor<0)
				currentK =i;
			if(i+currentK>=sourcePoints.size())
				currentK = sourcePoints.size() - i-1;
			if(currentK>smoothingFactor)
				currentK = smoothingFactor;

			for(int j=0;j<=currentK;j++){
				currentX *= sourcePoints.get(i+j).getX();
				currentY *= sourcePoints.get(i+j).getY();
				currentX *= sourcePoints.get(i-j).getX();
				currentY *= sourcePoints.get(i-j).getY();
				count +=2;
			}
			if(count != 0){
				double x=0;
				double y = 0;
				if(sourcePoints.get(i).getX() <0){
					x = Math.pow(currentX, 1.0/count);
					x = x*-1;
				}
				else
					x = Math.pow(currentX, 1.0/count);
				if(sourcePoints.get(i).getY() <0){
					y = Math.pow(currentY, 1.0/count);
					y = y*-1;
				}
				else
					y = Math.pow(currentY, 1.0/count);
				clusterPoints.get(i).setLocation(x , y );
			}
		}
		return;
	}
}
