package data_processing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Graymap {
	private static Graymap me = null;
	private static int size = 10000;
	private static int stepsSize = 300;
	private static int steps = size / stepsSize;
	private static int vektorSize = 270*4;
	private static int vektorStepSize = 3;
	private static int vektorSteps = vektorSize / vektorStepSize ;
	private static int minSize = 10;
	private static int maxGapSize = 100;
	private static int maxGray = 255;
	private static int maxUnknownGray = 127;
	private static double grayStep = ((double)maxUnknownGray-1)/((double)steps);
	private static float differentialThreshold = 128;
	private static double updateFactor =  (double) 0.04;
	private static boolean sharpEdge = false;
	private static Vector<Vector<Long>> map = new Vector<Vector<Long>>();
	private static Vector<Vector<Long>> newMap = new Vector<Vector<Long>>();
	
	
	JFrame frame = new JFrame();
	JLabel label = new JLabel();
	
	private Graymap(){
		me = this;
		map = createRawMap();
		newMap = createRawMap();
		frame.add(label);
		frame.setVisible(true);
		frame.setSize(1050, 350);
	}
	/**
	 * erstellt eine neue Karte, die Karte wird mit ansteigender Graustufe generiert.
	 * das maximum ergibt sich über grayStep aus maxUnknownGray
	 * @return
	 */
	private Vector<Vector<Long>> createRawMap(){
		Vector<Vector<Long>> newMap = new Vector<Vector<Long>>();
		// erstellen und nach außen hin bis zu 50% grau färben
		for(int i=0;i<vektorSteps;i++){
			Vector<Long> current = new Vector<Long>();
			for(int j=0;j<steps;j++){
				//current.add((long) (j*grayStep));
				current.add((long) (maxGray/2));
			}
			newMap.add(current);
		}
		return newMap;
	}
	
	/**
	 * setzt die newMap auf standart zurück
	 */
	private void clearNewMap(){
		for(int i=0;i<vektorSteps;i++){
			Vector<Long> current = newMap.get(i);
			for(int j=0;j<steps;j++){
				current.set(j, (long) (j*grayStep));
			}
		}
	}
	
	/**
	 * liefert die das singleton der Graymap zurück
	 * wenn noch keine Graymap existiert, wird diese erzeugt
	 * @return
	 */
	static public Graymap getGraymap(){
		if(me == null)
			new Graymap();
		return me;
	}
	
	/**
	 * 
	 * 
	 * @param data
	 * @return liste mit punkten die zu einem als sich bewegenden objekt erkannt wurden
	 * 		   fehler: ein einzelnes listenelement mit -1;
	 */
	public Vector<int[]> addNewData(Vector<Long> stepVector){
		Vector<Integer> movingPoints = new Vector<Integer>();
		Vector<int[]> movingArea = new Vector<int[]>();


		int step=0;
		int vektorStep = 0;
		long sideUpdate = 0;
		long valueOld = 0;
		long valueCurrent = 0;
		long valueNew = 0;
		
		// wenn größen nicht passen
		/*if(stepVector.size()!= vektorSteps){
			moving.add((long) -1);
			return moving;
		}*/
		
		clearNewMap();
		
		// neue graymap erstellen
		for(int i=0;i<stepVector.size();i++){
			vektorStep = i/vektorStepSize;
			step = (int) (stepVector.get(i)/stepsSize);
			newMap.get(vektorStep).set(step,(long) maxGray);
			valueOld = map.get(vektorStep).get(step);
			// wenn punkt im hellembereich ist, --> als bewegung erkennen TODO HIER DIE WIRGLICH WICHTIGE FUNKTION
			if(valueOld < differentialThreshold){
			//if(valueOld < 10){
				movingPoints.add(i);
			}
			if(sharpEdge == false){ 
				if(step-1 >0){
					sideUpdate = (long) (maxGray- 0.5*( maxGray - newMap.get(vektorStep).get(step-1)));
					newMap.get(vektorStep).set(step-1,(long) sideUpdate);
				}
				if(vektorStep>0){
					sideUpdate = (long) (maxGray- 0.5*( maxGray - newMap.get(vektorStep-1).get(step)));
					newMap.get(vektorStep-1).set(step,(long) sideUpdate);
				}
				if(vektorStep<stepVector.size()-1){
					sideUpdate = (long) (maxGray- 0.5*( maxGray - newMap.get(vektorStep+1).get(step)));
					newMap.get(vektorStep+1).set(step,(long) sideUpdate);
				}
			}
			for(int j=step+1;j<steps;j++){
				newMap.get(vektorStep).set(j,(long) maxUnknownGray);
			}
		}
		int start=0, stop=0;
		for(int i=0;i<movingPoints.size();i++){
			start = i;
			for(int j=1;true;j++){
				if(i+j>=movingPoints.size() || movingPoints.get(i+j)-1!=movingPoints.get(i+j-1)){
					stop = i+j-1;
					j=movingPoints.size();
					break;
				}
			}
			// prüft ob neues area nah genug am forherigen ist um sie zusammen zu fügen
			if(movingArea.size()>0 && movingPoints.get(start) - movingArea.get(movingArea.size()-1)[1]< maxGapSize){
				movingArea.get(movingArea.size()-1)[1] = movingPoints.get(stop);
			}else if(movingPoints.get(stop)-movingPoints.get(start)>=minSize){
				// komplett neues areal
				int[] current = new int[2];
				current[0]=movingPoints.get(start);
				current[1]=movingPoints.get(stop);
				movingArea.add(current);
			}
			i = stop+1;
		}
		
		// graymap zusammenführen
		double updatevalue = 0;
		for(int i=0;i<vektorSteps;i++){
			for(int j=0;j<steps;j++){
				valueOld = map.get(i).get(j);
				valueCurrent = newMap.get(i).get(j);
				if(valueOld < valueCurrent){
					updatevalue = updateFactor * (valueCurrent - valueOld) /2;
				}
				else{
					updatevalue = updateFactor * (valueCurrent - valueOld);
				}
				valueNew= (long) (valueOld + updatevalue);
				map.get(i).set(j,valueNew);
			}
		}
		
		showVisual(map,movingArea);
		return movingArea;
	}
	
	public Vector<Vector<Long>> getMap(){
		return map;
	}
	
	private int showVisual(Vector<Vector<Long>> map, Vector<int[]> moving){
		label.setIcon( new ImageIcon(getImageFromArray(map,moving).getScaledInstance(1000,300,Image.SCALE_DEFAULT) ) );

	    return 1;
	}
	
	public static Image getImageFromArray(Vector<Vector<Long>> input, Vector<int[]> moving) {
        BufferedImage image = new BufferedImage(vektorSteps, steps+50, BufferedImage.TYPE_BYTE_GRAY);
        int current = 0;
        for(int i=0;i<vektorSteps;i++){
        	for(int j=0;j<steps;j++){
        		current = Integer.parseInt( input.get(i).get(j)+"" );
        		if(current > 255) current =255;
        		try {
					current = new Color(255- current,255- current,255- current).getRGB();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		image.setRGB(i, j, current );
   
            }
        }
        int start,stop = 0;
        current = new Color(127,127,127).getRGB();
        for(int i=0;i<moving.size();i++){
        	start = moving.get(i)[0];
        	stop = moving.get(i)[1];
        	for(int j=start;j<stop;j++){
        		for(int k=steps +5; k< steps + 25;k++){
        			image.setRGB(j/vektorStepSize, k, current );
        		}
            }
        }
        return image;
    }
}
