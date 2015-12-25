package data_processing;

import java.util.Vector;

public class Graymap {
	private static Graymap me = null;
	private int size = 10000;
	private int stepsSize = 50;
	private int steps = size / stepsSize;
	private int vektorSteps = 270*4;
	private int maxGray = 255;
	private int maxUnknownGray = 127;
	private Long grayStep = (long) (maxUnknownGray/steps);
	private float differentialThreshold = 150;
	private double updateFactor =  (double) 0.03;
	private boolean sharpEdge = false;
	private Vector<Vector<Long>> map = new Vector<Vector<Long>>();
	private Vector<Vector<Long>> newMap = new Vector<Vector<Long>>();
	
	private Graymap(){
		me = this;
		map = createRawMap();
		newMap = createRawMap();
	}
	/**
	 * erstellt eine neue Karte, die Karte wird mit ansteigender Graustufe generiert.
	 * das maximum ergibt sich über grayStep aus maxUnknownGray
	 * @return
	 */
	private Vector<Vector<Long>> createRawMap(){
		Vector<Vector<Long>> newMap = new Vector<Vector<Long>>();
		grayStep = (long) (maxUnknownGray/steps);
		// erstellen und nach außen hin bis zu 50% grau färben
		for(int i=0;i<vektorSteps;i++){
			Vector<Long> current = new Vector<Long>();
			for(int j=0;j<steps;j++){
				current.add(i*grayStep);
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
				current.set(j, i*grayStep);
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
	public Vector<Long> addNewData(Vector<Long> stepVector){
		Vector<Long> moving = new Vector<Long>();
		//CopyOnWriteArrayList<Step> stepVector =  SynchronListHandler.getRawData();
		// TODO daten testen
		// step ?++--

		int step=0;
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
			step = (int) (stepVector.get(i)/stepsSize);
			newMap.get(i).set(step,(long) maxGray);
			valueOld = map.get(i).get(step);
			// wenn punkt im hellembereich ist, --> als bewegung erkennen TODO HIER DIE WIRGLICH WICHTIGE FUNKTION
			if(valueOld < differentialThreshold){
			//if(valueOld < 10){
				moving.add((long) i);
			}
			if(sharpEdge == false){ 
				newMap.get(i).set(step-1,(long) maxUnknownGray);
				if(i>0){
					sideUpdate = (long) (maxGray- 0.5*( maxGray - newMap.get(i-1).get(step)));
					newMap.get(i-1).set(step,(long) maxGray);
				}
				if(i<stepVector.size()-1){
					sideUpdate = (long) (maxGray- 0.5*( maxGray - newMap.get(i+1).get(step)));
					newMap.get(i+1).set(step,(long) maxGray);
				}
			}
			for(int j=i+1;j<steps;j++){
				newMap.get(i).set(j,(long) maxUnknownGray);
			}
		}
		
		// graymap zusammenführen
		for(int i=0;i<stepVector.size();i++){
			for(int j=0;j<steps;j++){
				valueOld = map.get(i).get(j);
				valueCurrent = newMap.get(i).get(j);
				double updatevalue = updateFactor * (valueCurrent - valueOld);
				valueNew= (long) (valueOld + updatevalue);
				//if(valueOld != valueCurrent)
				//	System.out.println(i + " " + j + " " + (valueOld-valueCurrent) + " " + valueNew + " " + updatevalue);
				map.get(i).set(j,valueNew);
			}
		}
		
		return moving;
	}
	
	public Vector<Vector<Long>> getMap(){
		return map;
	}
}
