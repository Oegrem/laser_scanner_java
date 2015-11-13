package data_processing;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class dataStorage {
	static dataStorage dS = null;
	String filename = "point.data";
	Vector<Vector<Point>> data = new Vector<Vector<Point>>();
	int current = 0;
	
	private dataStorage() {
		readStoredData();
		current = 0;
	}

	static public dataStorage getDataStorage(){
		if(dS == null)
			dS = new dataStorage();
		return dS;
	}
	
	// Alternativ kann auch mit Serializable das ganze Object gespeichert werden,jedoch nicht Menschenlesbar
	public boolean storeData(Vector<Point> array) { 
		File output = new File(filename);
		FileWriter writer;
		try {
			// new FileWriter(file ,true) - falls die Datei bereits existiert
			// werden die Bytes an das Ende der Datei geschrieben
			writer = new FileWriter(output, true);

			for (int i = 0; i < array.size(); i++) {
				writer.write(array.get(i).getX() + "I" + array.get(i).getY() + "|");
			}

			/* Alternative zu oben:
			 * for(Point p : array){
			 *   writer.write(p.getX()+""+p.getY()+"-"); 
			 * }
			 */

			// Platformunabhängiger Zeilenumbruch wird in den Stream geschrieben
			writer.write(System.getProperty("line.separator"));

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void readStoredData() {
		FileReader fr;

		try {
			fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			Point current;
			Vector<Point> currentList = null;
			String row = "";
			String[] split;
			String[] superSplit;
			// lese zeile vür zeile
			while((row = br.readLine() )!= null){
				// wenn zeile nicht lehr
				if (row.length() > 10){
					currentList = new Vector<Point>();
					split= row.split("\\|");
					// zeile auftrennen zu punkten
					for (int i = 0; i < split.length; i++) {
						// wenn ein punkt als inhalt
						if(split[i].contains("I")){
							// erstelle punkt, füge werte ein
							current = new Point(0, 0);
							superSplit = split[1].split("I");
							current.setLocation(Double.parseDouble(superSplit[0]), Double.parseDouble(superSplit[1]));
							// füge sie der momentane liste hinzu
							currentList.add(current);
						}
					}
					data.add(currentList);
				}
			}
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  liefert immer eine liste mit punkten zurück
	 *  
	 * @return
	 */
	
	public Vector<Point> getNextPointList(){
		Vector<Point> ret = new Vector<Point>();
		Vector<Point> toRet = null;
		
		toRet = data.get(current);
		for(int i=0;i<toRet.size();i++){
			ret.add((Point)toRet.get(i).clone());
		}
		
		if (ret.size()<1){
			ret = new Vector<Point>();
			ret.add(new Point(0,0));
		}
		current ++;
		if(current>= data.size())
			current = 0;
		
		return ret;
	}
}
