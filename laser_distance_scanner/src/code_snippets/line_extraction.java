package code_snippets;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class line_extraction {

	public line_extraction(){
	}

	public static Vector<Line> calcLine(CopyOnWriteArrayList<Point> _pointVector) {
		//lineVector.clear();
		
		Vector<Line> lV = new Vector<Line>();
		
		Line line = new Line();
		
		CopyOnWriteArrayList<Point> pointVector = new CopyOnWriteArrayList<Point>();
		
		pointVector.addAll(_pointVector);

		line.setP1(pointVector.get(0));
		line.setP2(pointVector.get(1));
		
		for (int i = 1; i < pointVector.size()-1; i++) {
			if (line.getP2().distance(pointVector.get(i)) <= 40){
				float angle = getAngle(line.getP1(), line.getP2(), pointVector.get(i));
				//System.out.println(Float.toString(angle));
				if (angle < 0.0005 * line.getP2().distance(pointVector.get(i))) {
					line.setP2(pointVector.get(i));
				}
			} else {
				lV.add(line);
				line = new Line();
				line.setP1(pointVector.get(i));
				line.setP2(pointVector.get(i + 1));
			}
		}
		
		return lV;
	}

	public static float getAngle(Point aP, Point bP, Point cP) {

		double a = aP.distance(bP);

		double b = bP.distance(cP);

		double c = cP.distance(aP);

		float angle = 0;

		angle = (float) Math.abs((Math.acos(((Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2)) / (2 * a * c)))));

		return angle;
	}

}
