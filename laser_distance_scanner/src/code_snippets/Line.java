package code_snippets;

import java.awt.Point;

public class Line {

	private Point p1;
	private Point p2;
	
	public Line(){
		
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
	
}
