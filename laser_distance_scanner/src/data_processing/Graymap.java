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
	private static int stepsSize = 50;
	private static int steps = size / stepsSize;
	private static int vektorSize = 270*4;
	private static int vektorStepSize = 2;
	private static int vektorSteps = vektorSize / vektorStepSize ;
	private static int differenzSearchSize = 5;
	private static int maxGray = 255;
	private static int maxUnknownGray = 127;
	private static double grayStep = ((double)maxUnknownGray-1)/((double)steps);
	private static float differentialThreshold = 130;
	private static double updateFactor =  (double) 0.05;
	private static boolean sharpEdge = true;
	private static Vector<Vector<Long>> map = new Vector<Vector<Long>>();
	private static Vector<Vector<Long>> newMap = new Vector<Vector<Long>>();
	
	
	private BufferedImage visualMap = new BufferedImage(vektorSize, steps,  BufferedImage.TYPE_INT_ARGB);
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
	 * das maximum ergibt sich �ber grayStep aus maxUnknownGray
	 * @return
	 */
	private Vector<Vector<Long>> createRawMap(){
		Vector<Vector<Long>> newMap = new Vector<Vector<Long>>();
		// erstellen und nach au�en hin bis zu 50% grau f�rben
		for(int i=0;i<vektorSteps;i++){
			Vector<Long> current = new Vector<Long>();
			for(int j=0;j<steps;j++){
				current.add((long) (j*grayStep));
			}
			newMap.add(current);
		}
		return newMap;
	}
	
	/**
	 * setzt die newMap auf standart zur�ck
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
	public Vector<Long> addNewData(Vector<Long> stepVector){
		Vector<Long> moving1 = new Vector<Long>();
		Vector<Long> moving2 = new Vector<Long>();
		//CopyOnWriteArrayList<Step> stepVector =  SynchronListHandler.getRawData();
		// TODO daten testen
		// step ?++--

		int step=0;
		int vektorStep = 0;
		long sideUpdate = 0;
		long valueOld = 0;
		long valueCurrent = 0;
		long valueNew = 0;
		
		// wenn gr��en nicht passen
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
				moving1.add((long) i);
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
		
		// graymap zusammenf�hren
		for(int i=0;i<vektorSteps;i++){
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
		int  count =0;
		boolean minSize = true;
		for(int i=0;i<moving1.size();i++){
			minSize = true;
			for(int j=0;j<differenzSearchSize;j++){
				if(i+j< moving1.size()){
					if(moving1.get(i)+j != moving1.get(i+j))
						minSize = false;
				}
				if(i-j>0){
					if(moving1.get(i)-j != moving1.get(i-j))
						minSize = false;
				}
			}
			if(minSize){
				moving2.add(moving1.get(i));
			}
		}
		showVisual(map,moving2);
		return moving2;
	}
	
	public Vector<Vector<Long>> getMap(){
		return map;
	}
	
	private int showVisual(Vector<Vector<Long>> map, Vector<Long> moving){
		label.setIcon( new ImageIcon(getImageFromArray(map,moving).getScaledInstance(1000,300,Image.SCALE_DEFAULT) ) );

	    return 1;
	}
	
	public static Image getImageFromArray(Vector<Vector<Long>> input, Vector<Long> moving) {
        BufferedImage image = new BufferedImage(vektorSteps, steps+50, BufferedImage.TYPE_BYTE_GRAY);
        int current = 0;
        /*
        for(int i=0;i<vektorSteps;i++){
        	for(int j=0;j<steps;j++){
        		current = Integer.parseInt( input.get(i).get(j)+"" );
        		current = new Color(255- current,255- current,255- current).getRGB();
        		image.setRGB(i, j, current );
   
            }
        }*/
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
        int asdf = 0;
        for(int i=0;i<moving.size();i++){
        	asdf = Integer.parseInt(moving.get(i)+"");
        	for(int j=steps+5;j<steps+50;j++){
        		current = new Color(127/vektorStepSize,127/vektorStepSize,127/vektorStepSize).getRGB();
        		image.getRGB(asdf/vektorStepSize, j);
        		image.setRGB(asdf/vektorStepSize, j, image.getRGB(asdf/vektorStepSize, j) + current );
   
            }
        }
        return image;
    }
	
}
