package laser_distance_scanner;

import data_processing.Processing;

public class Main {

	public static void main(String[] args) {

		Distance_scanner.setInstantSimulation(true); // call and set to true when simulation shall be started instantly
		
		new Graphics().run();

		
		

	}
}
