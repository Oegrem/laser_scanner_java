package data_processing;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Graymap {
	private static Graymap me = null;




	private static Vector<Vector<Short>> map = new Vector<Vector<Short>>();
	private static Vector<Vector<Short>> newMap = new Vector<Vector<Short>>();
	private static boolean[] change = new boolean[Settings.getAngle_number()];
	private static int minChangeStep = 0;
	private static int maxChangeStep = 0;
	
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
	 * initialisiert alle felder mit maxUnknownGray, da keinerlei informationen �ber umgebung bekannt ist
	 * 26.12.15: die erkennung der bewegung wird mit maxUnknownGray +1 realisiert. 
	 * 			-> ab 2. durchlauf erkennung von unterschieden
	 * wenn die aktualisierungsrate v�r negativ und positiv unterschiedlich ist, kann eine initialisierung mit minimum oder maximum sinfoller sein
	 * @return
	 */
	private Vector<Vector<Short>> createRawMap(){
		Vector<Vector<Short>> newMap = new Vector<Vector<Short>>();
		int maxUnknownGray = Settings.getGraymap_max_unknown_gray();
		int angleSteps = Settings.getGraymap_angle_steps();
		int sectionSteps = Settings.getGraymap_section_steps();
		
		// erstellen und nach au�en hin bis zu 50% grau f�rben
		for(int i=0;i<angleSteps;i++){
			Vector<Short> current = new Vector<Short>();
			for(int j=0;j<sectionSteps;j++){
				//current.add((long) (j*grayStep));
				current.add((short) (maxUnknownGray));
			}
			newMap.add(current);
		}
		return newMap;
	}
	
	/**
	 * setzt die newMap auf standart zur�ck
	 *  je weiter vom start entfernt desto dunkler die felder, bis maxUnknownGray
	 */
	private void clearNewMap(){
		int angleSteps = Settings.getGraymap_angle_steps();
		int sectionSteps = Settings.getGraymap_section_steps();
		int[] graysteps = Settings.getGraymap_section_steps_array();
		for(int i=0;i<angleSteps;i++){
			Vector<Short> current = newMap.get(i);
			for(int j=0;j<sectionSteps;j++){
				current.set(j,(short)graysteps[j]);
			}
		}
	}
	
	/**
	 * Berechnet aus einer liste mit einzelnen sensormesswerten die sich bewegt haben, zusammeh�ngende bereiche mit einem start und endwert
	 * pr�fung auf minimale gr��e der areale
	 * pr�fung auf kleine l�cken zwischen arealen und zusammenf�hren dieser
	 * 
	 * @param movingPoints, eine liste mit den element nummern der sich bewegenden sensorwerte
	 * 						movingPoints[0] => z.b. 7 -> 7. element der orginal pointlist als bewegend erkannt
	 * 						movingPoints[1] => z.b. 345 -> n�chstes als bewegend erkannter punkt
	 * @return vector<int[]> [0] = start; 
	 *                       [1] = ende eines areals
	 */
	private Vector<int[]> calcMovingAreas(Vector<Integer> movingPoints){
		Vector<int[]> movingArea = new Vector<int[]>();
		int start=0, stop=0;
		int maxGapSize = Settings.getGraymap_move_area_gap_max_Size();
		int minSize = Settings.getGraymap_move_area_min_size();
		
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
			// pr�ft ob neues area nah genug am forherigen ist um sie zusammen zu f�gen
			if(movingArea.size()>0 && movingPoints.get(start) - movingArea.get(movingArea.size()-1)[1]< maxGapSize){
				// vergr��ere altes areal bis ende des neuen erkannten
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
	 * F�gt der graymap die neue moment aufnahme graymap hinzu
	 * 
	 * verbesserungsm�glichkeiten w�hren
	 * 	- erkennung auf gleiche felder auf erkennung von gleichen vektoren (i) zu erweitern
	 *  - updatevalue unter maxUnknownGray reduzieren um bewegungen im wei� weniger stark in die karte auzunehmen
	 */
	private void mergeMaps(){
		short valueOld=0, valueCurrent=0, valueNew=0;
		double updatevalue = 0;
		double updateFactor = Settings.getGraymap_update_factor();
		int updateDirectionFactor = Settings.getGraymap_update_direction_factor();
		int maxUnknownGray = Settings.getGraymap_max_unknown_gray();
		int angleSteps = Settings.getGraymap_angle_steps();
		int sectionSteps = Settings.getGraymap_section_steps();
		boolean unknown = false;
		// jeden vektor absuchen (ein vektor kann mehrere sensorvektoren beinhaltet)
		for(int i=0;i<angleSteps;i++){
			// wenn neue werte im alten maxgray -> keine �nderungen zur map, �berspringen
			if(change[i]!=true) continue;
			
			// addierter grauwert aller steps als erkennung von gleichen vektoren ?
			// vektor in einzelne abschnitte teilen
			unknown = false;
			for(int j=minChangeStep;j<maxChangeStep;j++){
				valueOld = map.get(i).get(j);
				valueCurrent = newMap.get(i).get(j);
				if(valueOld==maxUnknownGray){
					if(unknown == true)
						break;
					unknown = true;
				}
				// nur bei unterschied
				if(valueOld!=valueCurrent){
					// neu dunkel
					if(valueOld < valueCurrent){
						// untergrund hell
						if(valueOld < maxUnknownGray)
							updatevalue = updateFactor * (valueCurrent - valueOld) / updateDirectionFactor;
						// untergrund dunkel
						else 
							updatevalue = updateFactor * (valueCurrent - valueOld) * updateDirectionFactor;
					}
					// neu hell
					else{
						// untergrund hell
						if(valueOld < maxUnknownGray)
							updatevalue = updateFactor* (valueCurrent - valueOld) * updateDirectionFactor;
						// untergrund dunkel
						else 
							updatevalue = updateFactor * (valueCurrent - valueOld) / updateDirectionFactor;
					}
					valueNew= (short) (valueOld + updatevalue);
					map.get(i).set(j,valueNew);
				}
			}
			
		}
	}
	
	/**
	 * liefert die das singleton der Graymap zur�ck
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
		short sideUpdate=0, valueOld=0;
		int maxUnknownGray = Settings.getGraymap_max_unknown_gray();
		int maxGray = Settings.getGraymap_max_gray();
		int angleSize = Settings.getGraymap_angle_size();
		int angleSteps= Settings.getGraymap_angle_steps();
		int sectionSteps = Settings.getGraymap_section_steps();
		int sectionSize = Settings.getGraymap_section_size();
		int recognition = Settings.getGraymap_recognition_threshold();
		int oldVectorStep=0, oldStep=0;
		boolean oldMove=false;
		// wenn gr��en nicht passen
		/*if(stepVector.size()!= vektorSteps){
			moving.add((long) -1);
			return moving;
		}*/
		long t1,t2,t3,t4,t5,t6;
		t1 = Settings.nstp.currentNanoSecondsTimestamp();
		clearNewMap();
		t2 = Settings.nstp.currentNanoSecondsTimestamp();
		
		minChangeStep = sectionSteps-1;
		maxChangeStep = 0;
		// neue graymap bef�llen
		for(int i=0;i<stepVector.size();i++){
			// finde das feld der map indem der neue punkt gesetzt wird
			vektorStep = i/angleSize;
			if(vektorStep>=angleSteps){
				vektorStep = angleSteps -1;
			}
			step = (int) (stepVector.get(i)/sectionSize);
			if(step>=sectionSteps){
				step = sectionSteps-1;
			}
			
			if(oldStep==step && oldVectorStep==vektorStep){
				if(oldMove)
					movingPoints.add(i);
				continue;
			}
			
			oldVectorStep = vektorStep;
			oldStep = step;
			if(minChangeStep>step)minChangeStep=step;
			if(maxChangeStep<step)maxChangeStep=step;
			newMap.get(vektorStep).set(step,(short) maxGray);
			valueOld = map.get(vektorStep).get(step);
				
			// wenn punkt im hellembereich ist, --> als bewegung erkennen 
			if(valueOld < recognition){
				movingPoints.add(i);
				oldMove=true;
			}else oldMove = false;
			// wenn punkt nicht im perfekten schwarz, karte muss upgedatet werden
			if(valueOld != maxGray){
				change[vektorStep] = true;
			}else change[vektorStep] = false;
			
			// sharpEdge = true -> nur das feld in dem momentaner messwert gemessen wurde
			// sharpEge = false -> felder links rechts und vor dem momentanen feld werden ebenfalsl verdunkelt
			
			if(Settings.isGraymap_edge_accuracy() == false){ 
				if(step-1 >0){
					sideUpdate = (short) (maxGray- 0.5*( maxGray - newMap.get(vektorStep).get(step-1)));
					newMap.get(vektorStep).set(step-1,(short) sideUpdate);
				}
				if(vektorStep>0){
					sideUpdate = (short) (maxGray- 0.5*( maxGray - newMap.get(vektorStep-1).get(step)));
					newMap.get(vektorStep-1).set(step,(short) sideUpdate);
				}
				if(vektorStep<stepVector.size()-1){
					sideUpdate = (short) (maxGray- 0.5*( maxGray - newMap.get(vektorStep+1).get(step)));
					newMap.get(vektorStep+1).set(step,(short) sideUpdate);
				}
			}
			for(int j=step+1;j<sectionSteps;j++){
				newMap.get(vektorStep).set(j,(short) maxUnknownGray);
			}
		}
		t3 = Settings.nstp.currentNanoSecondsTimestamp();
		// berechne aus einzelnen bewegenden punkten bereiche mit start und ente
		movingArea = calcMovingAreas(movingPoints);
		t4 = Settings.nstp.currentNanoSecondsTimestamp();
		// graymap zusammenf�hren
		mergeMaps();
		t5 = Settings.nstp.currentNanoSecondsTimestamp();
		showVisual(map,movingArea);
		t6 = Settings.nstp.currentNanoSecondsTimestamp();
		Settings.printCalcTime("Graymap clear map ", t1, t2);
		Settings.printCalcTime("Graymap fill  map ", t2, t3);
		Settings.printCalcTime("Graymap calc  move", t3, t4);
		Settings.printCalcTime("Graymap merge map ", t4, t5);
		Settings.printCalcTime("Graymap show  pic ", t5, t6);
		return movingArea;
	}
	
	public Vector<Vector<Short>> getMap(){
		return map;
	}
	
	private int showVisual(Vector<Vector<Short>> map, Vector<int[]> moving){
		label.setIcon( new ImageIcon(getImageFromArray(map,moving).getScaledInstance(1000,300,Image.SCALE_DEFAULT) ) );

	    return 1;
	}
	
	public static Image getImageFromArray(Vector<Vector<Short>> map2, Vector<int[]> moving) {
		int angleSteps = Settings.getGraymap_angle_steps();
		int angleSize = Settings.getGraymap_angle_size();
		int sectionSteps = Settings.getGraymap_section_steps();
		
        BufferedImage image = new BufferedImage(angleSteps, sectionSteps+50, BufferedImage.TYPE_BYTE_GRAY);
        int current = 0;
        for(int i=0;i<angleSteps;i++){
        	for(int j=0;j<sectionSteps;j++){
        		current = Integer.parseInt( map2.get(i).get(j)+"" );

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
        		for(int k=sectionSteps +5; k< sectionSteps + 25;k++){
        			image.setRGB(j/angleSize, k, current );
        		}
            }
        }
        return image;
    }
}
