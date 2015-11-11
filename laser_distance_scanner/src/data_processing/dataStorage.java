package data_processing;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

public class dataStorage {
	
	String filename = "point.data";
	
	public dataStorage(){}
	
	public boolean storeData(Vector<Point> array){
		File output = new File(filename);
		FileWriter writer;
		 try {
		       // new FileWriter(file ,true) - falls die Datei bereits existiert
		       // werden die Bytes an das Ende der Datei geschrieben
			   if(output.exists())
				   output.delete();
		       writer = new FileWriter(output ,true);
		       
		       for(int i=0;i<array.size();i++){
		    	   writer.write(array.get(i).getX() + "I" + array.get(i).getY() + "-");
		       }
		       
		       // Platformunabhängiger Zeilenumbruch wird in den Stream geschrieben
		       writer.write(System.getProperty("line.separator"));

		       writer.flush();
		       writer.close();
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		return false;
	}
	
	public Vector<Point> readStoredData(){
		FileReader fr;
		Vector<Point> ret = new Vector<>();
		try {
			fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			// lese eine zeile
		    String zeile = br.readLine();
		    // wenn daten nicht in 1. zeile lese 2. zeile
		    if(zeile.length()<10)
		    	zeile = br.readLine();
		    // PA ich such doch jetzt nicht nach den scheiß daten
		    if(zeile.length()<10)
		    	return null;
		    
		    String[] split = zeile.split("-");
		    String[] superSplit;
		    Point current;
		    for(int i=0;i<split.length;i++){
		    	current = new Point(0,0);
		    	superSplit = split[1].split("I");
		    	current.setLocation(Double.parseDouble(superSplit[0]), Double.parseDouble(superSplit[1]));
		    	ret.add(current);
		    }

		    br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	    
		return ret;
	}
	
}
