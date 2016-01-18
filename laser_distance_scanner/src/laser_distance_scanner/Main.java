package laser_distance_scanner;

import java.util.Vector;

import scanner_simulator.SData;
import scanner_simulator.SimFileHandler;

public class Main {

	public static void main(String[] args) {

		//startDisplay();
		
		//startDisplay("ex1", false, 5000);
		
		startDisplay("walk", false, 5000);

		// runSimFileTest("ex1");
	}

	public static void runSimFileTest(String name) {
		SimFileHandler sFH = new SimFileHandler(name);
		Vector<SData> dataVector = sFH.readObject();

		long time = 0l;

		for (SData sd : dataVector) {
			System.out.println(sd.freq);
			time += sd.freq;
		}

		System.out.println("ZEIT: " + time);
		System.out.println(dataVector.size());
	}

	public static void startDisplay() {
		
		Distance_scanner.getDistanceScanner().start();
		
		new Graphics().run();
	}
	
	public static void startDisplay(String name){
		Distance_scanner.setInstantSimulation(true); // call and set to true
		// when simulation
		// shall be started
		// instantly
		
		Distance_scanner.alternativeSimFile = name;
	}

	public static void startDisplay(String name, boolean record,
			Integer... time) {
		if (!record) {
			Distance_scanner.setInstantSimulation(true); // call and set to true
															// when simulation
															// shall be started
															// instantly

			Distance_scanner.alternativeSimFile = name;
		} else {
			Distance_scanner.getDistanceScanner().recordSimFile(name,time[0]);
		}
		new Graphics().run();
	}

	public static void recordFor(String name, int time) {
		Distance_scanner.getDistanceScanner().recordSimFile(name,time);
	}
}
