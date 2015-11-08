package laser_distance_scanner;

public class main {

	public static void main(String[] args) {
		
		new graphics().run();
		
		distance_scanner dis = new distance_scanner();
		dis.connect();
		
		dis.getDistances(1);
		
		dis.disconnect();
	}
}
