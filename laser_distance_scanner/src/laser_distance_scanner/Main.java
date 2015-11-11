package laser_distance_scanner;

import data_processing.Processing;

public class Main {

	public static void main(String[] args) {

		// new Graphics().run();

		Distance_scanner scn = new Distance_scanner(); // Creating new Thread
		// scn.disconnect();
		scn.connect();
		scn.start();

		Processing proc = new Processing(scn);
		for (int i = 0; i < 51; i++) {
			proc.startProcess();
		}

		// scn.disconnect();
		scn.interrupt();

	}
}
