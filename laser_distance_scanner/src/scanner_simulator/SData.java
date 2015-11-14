package scanner_simulator;

import java.awt.Point;
import java.io.Serializable;
import java.util.Vector;


// Serializable Object to Save Sensor Points with Timestamp
public class SData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public Vector<Point> pVector;
	public Long timestamp = 0l;
	
	public SData(){
		pVector = new Vector<Point>();
	}
	
}
