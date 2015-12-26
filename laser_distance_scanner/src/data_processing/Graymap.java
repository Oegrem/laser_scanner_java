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
	private static int stepsSize = 200;
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
	private static double updateFactor =  (double) 0.05;
	private static int updateUnknownTreshold = 4;
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
	 * erstellt eine neue Karte, 
	 * initialisiert alle felder mit maxUnknownGray, da keinerlei informationen über umgebung bekannt ist
	 * 26.12.15: die erkennung der bewegung wird mit maxUnknownGray +1 realisiert. 
	 * 			-> ab 2. durchlauf erkennung von unterschieden
	 * wenn die aktualisierungsrate vür negativ und positiv unterschiedlich ist, kann eine initialisierung mit minimum oder maximum sinfoller sein
	 * @return
	 */
	private Vector<Vector<Long>> createRawMap(){
		Vector<Vector<Long>> newMap = new Vector<Vector<Long>>();
		// erstellen und nach außen hin bis zu 50% grau färben
		for(int i=0;i<vektorSteps;i++){
			Vector<Long> current = new Vector<Long>();
			for(int j=0;j<steps;j++){
				//current.add((long) (j*grayStep));
				current.add((long) (maxUnknownGray));
			}
			newMap.add(current);
		}
		return newMap;
	}
	
	/**
	 * setzt die newMap auf standart zurück
	 *  je weiter vom start entfernt desto dunkler die felder, bis maxUnknownGray
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
	 * Berechnet aus einer liste mit einzelnen sensormesswerten die sich bewegt haben, zusammehängende bereiche mit einem start und endwert
	 * prüfung auf minimale größe der areale
	 * prüfung auf kleine lücken zwischen arealen und zusammenführen dieser
	 * 
	 * @param movingPoints, eine liste mit den element nummern der sich bewegenden sensorwerte
	 * 						movingPoints[0] => z.b. 7 -> 7. element der orginal pointlist als bewegend erkannt
	 * 						movingPoints[1] => z.b. 345 -> nächstes als bewegend erkannter punkt
	 * @return vector<int[]> [0] = start; 
	 *                       [1] = ende eines areals
	 */
	private Vector<int[]> calcMovingAreas(Vector<Integer> movingPoints){
		Vector<int[]> movingArea = new Vector<int[]>();
		int start=0, stop=0;
		
		if(movingPoints==null)
			return movingArea;
		
		for(int i=0;i<movingPoints.size();i++){
			start = i;
			// suche von i ab alle direkt aufeinanderfolgenden sich bewegenden sensorwerte
			for(int j=1;true;j++){
				if(i+j>=movingPoints.size() || movingPoints.get(i+j)-1!=movingPoints.get(i+j-1)){
					stop = i+j-1;
					j=movingPoints.size();
					break;
				}
			}
			// prüft ob neues area nah genug am forherigen ist um sie zusammen zu fügen
			if(movingArea.size()>0 && movingPoints.get(start) - movingArea.get(movingArea.size()-1)[1]< maxGapSize){
				// vergrößere altes areal bis ende des neuen erkannten
				movingArea.get(movingArea.size()-1)[1] = movingPoints.get(stop);
			}else if(movingPoints.get(stop)-movingPoints.get(start)>=minSize){
				// komplett neues areal
				int[] current = new int[2];
				current[0]=movingPoints.get(start);
				current[1]=movingPoints.get(stop);
				movingArea.add(current);
			}
			// am ende des gefundenen areals weitersuchen
			i = stop+1;
		}
		return movingArea;
	}

	/**
	 * Fügt der graymap die neue moment aufnahme graymap hinzu
	 * 
	 * verbesserungsmöglichkeiten währen
	 * 	- erkennung auf gleiche felder auf erkennung von gleichen vektoren (i) zu erweitern
	 *  - updatevalue unter maxUnknownGray reduzieren um bewegungen im weiß weniger stark in die karte auzunehmen
	 */
	private void mergeMaps(){
		long valueOld=0, valueCurrent=0, valueNew=0;
		double updatevalue = 0;
		
		// jeden vektor absuchen (ein vektor kann mehrere sensorvektoren beinhaltet)
		for(int i=0;i<vektorSteps;i++){
			// addierter grauwert aller steps als erkennung von gleichen vektoren ?
			// vektor in einzelne abschnitte teilen
			for(int j=0;j<steps;j++){
				valueOld = map.get(i).get(j);
				valueCurrent = newMap.get(i).get(j);
				// nur bei unterschied
				if(valueOld!=valueCurrent){
					// neu dunkel
					if(valueOld < valueCurrent){
						// untergrund hell
						if(valueOld < maxUnknownGray)
							updatevalue = updateFactor * (valueCurrent - valueOld) / updateUnknownTreshold;
						// untergrund dunkel
						else 
							updatevalue = updateFactor * (valueCurrent - valueOld) * updateUnknownTreshold;
					}
					// neu hell
					else{
						// untergrund hell
						if(valueOld < maxUnknownGray)
							updatevalue = updateFactor* (valueCurrent - valueOld) * updateUnknownTreshold;
						// untergrund dunkel
						else 
							updatevalue = updateFactor * (valueCurrent - valueOld) / updateUnknownTreshold;
					}
					valueNew= (long) (valueOld + updatevalue);
					map.get(i).set(j,valueNew);
				}
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
		Vector<int[]> movingArea = null;
		int step=0, vektorStep=0;
		long sideUpdate=0, valueOld=0;
		
		// wenn größen nicht passen
		/*if(stepVector.size()!= vektorSteps){
			moving.add((long) -1);
			return moving;
		}*/
		
		clearNewMap();
		
		// neue graymap befüllen
		for(int i=0;i<stepVector.size();i++){
			// finde das feld der map indem der neue punkt gesetzt wird
			vektorStep = i/vektorStepSize;
			step = (int) (stepVector.get(i)/stepsSize);
			newMap.get(vektorStep).set(step,(long) maxGray);
			valueOld = map.get(vektorStep).get(step);
				
			// wenn punkt im hellembereich ist, --> als bewegung erkennen 
				if(valueOld < differentialThreshold){
					movingPoints.add(i);
				}
			
			// sharpEdge = true -> nur das feld in dem momentaner messwert gemessen wurde
			// sharpEge = false -> felder links rechts und vor dem momentanen feld werden ebenfalsl verdunkelt
			
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

		// berechne aus einzelnen bewegenden punkten bereiche mit start und ente
		movingArea = calcMovingAreas(movingPoints);
		
		// graymap zusammenführen
		mergeMaps();
		
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
        		if(current < 0) current =0;
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
