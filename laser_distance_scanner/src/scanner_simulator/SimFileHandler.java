package scanner_simulator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class SimFileHandler {
	private String fileName; // Filename for writing and reading

	public SimFileHandler(String _fileName) {
		fileName = _fileName;
	}

	public void writeObject(Vector<SData> _data) { // Writing/Creating an Object
		try {
			FileOutputStream fos = new FileOutputStream(fileName); // FileOutput
			BufferedOutputStream bos = new BufferedOutputStream(fos); // BufferedOutput
			ObjectOutputStream oos = new ObjectOutputStream(bos); // ObjectOutput

			oos.writeObject(_data); // Serialize and Write Object in File
 
			oos.close(); // Close Stream

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public Vector<SData> readObject() { // Reading existing File
		try {
			FileInputStream fis = new FileInputStream(fileName); // FileInput
			BufferedInputStream bis = new BufferedInputStream(fis); // BufferedInput
			ObjectInputStream ois = new ObjectInputStream(bis); // ObjectInput

			Object dObj = ois.readObject(); // Reading File into an Object

			ois.close(); // Close Stream

			Vector<SData> obj = (Vector<SData>) dObj; // Cast Object to original Object: Vector<SData>

			return obj;

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		return null;

	}

}
