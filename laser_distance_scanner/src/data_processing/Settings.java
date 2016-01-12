package data_processing;

public class Settings {
	
	/**
	 * gobale settings
	 * 
	 * angle_number 	= Anzahl an Sensormessungen 
	 * angle_total 		= Der Winkel des Ausschnits auf dem Gemessen wird, von 0 bis x, die ausrichtung im raum wird nicht berücksichtigt
	 * angle_angle 		= Der Winkel zwischen 2 direkt nebeneinander liegenden Messungen
	 * angle_tan_array 	= der Tangenz alle Winkel zwischen einer Messunge und einem Beliebigen anzahl plätze entfernten winkel. Der Index entspricht die der entfernung zur ausgangsmessung
	 */
	private static int angle_number = 270*4;
	private static int angle_total = 270;
	private static double angle_angle = 0;
	private static double[] angle_tan_array = null;
	
	/**
	 *   graymap 						= schwarz weiß schatierte karte. dunkel = objekt, hell = frei
	 *  		  						  \-> wird zur erkennung von bewegenden objekten verwendet
	 *  graymap_state 					= aktiv oder inaktiv
	 *
	 *   angle 							= winkel -> kreisförmig angeordnete vektoren,
	 *  graymap_angle_size				= anzahl an messwerten die in die selbe Graymap spallte projeziert werden
	 *  graymap_angle_steps 			= anzahl an unabhängigen spalten in der graymap, berechnung = angle_number / graymap_angle_size
	 *
	 *   section 						= abschnitt -> ein einzelner Vektor zerlegt in abschnitte
	 *  graymap_section_count 			= maximale entfernung der messung in mm -> 10 meter 
	 *  graymap_section_size			= größe der graymap section in mm 
	 *  graymap_section_steps			= anzahl an unabhängigen sectionen in der Graymap, berechnung aus graymap_section_count / graymap_section_size
	 *
	 *   move_area 						= bewegendes gebiet -> bereich das als sich bewegend betrachtet wird
	 *  graymap_move_area_min_size 	   	= mininale anzahl an beieinanderliegenden messwerten eines areals
	 *  graymap_move_area_gap_max_Size 	= maximale lücke zwischen zwei arealen befor diese vereint werden
	 *  graymap_max_gray 				= der maximalwert der graymap, wird gesetzt wenn ein punkt im feld erkannt wird
	 *  graymap_max_unknown_gray 		= grau wert der gesetzt wird, wenn keine informationen forhanden sind
	 *  graymap_gray_Step 				= ein einzelner verdunkelungs schritt von weiß zu grau in den sectionen nach außen, wegen sich öffnenden winkeln und daraus entstehenden messfehlern kleiner objekte
	 *
	 *   recognition					= einstellungen die das erkennungsverhalten steuern
	 *  graymap_recognition_threshold 	= schwelle ab der ein objekt als fest erkannt wird
	 *  graymap_update_factor 		 	= factor mit dem die karte angepasst wird
	 *  graymap_update_direction_factor = fector: neu Dunkel auf alt Hell /, neu dunkel auf alt Dunkel *, neu hell auf alt hell *, neu hell auf alt dunkel /
	 * 									  \-> sorgt dafür das stationäre objekte lansamer verschwinden und sich bewegende langsamer festsetzen
	 *  graymap_edge_accuracy			= verwaschung der gesetzten punkte in naheliegende graymap felder, wenn an nur eigenes feld, wenn aus auch nachbarfelder,
	 * 									  \-> wenn =false werden ungenauigkeiten der messung ausgeglichen
	 */
	private static boolean graymap_state = true;				// true! 
	private static int graymap_angle_size = 4;					// 1 - 10 scheint gut zu sein
	private static int graymap_angle_steps = 0;					// berechnung
	private static int graymap_section_count = 10000; 			// 10000 = 10 meter
	private static int graymap_section_size = 500;				// 20 - 500 je größer desto besser
	private static int graymap_section_steps = 0;				// berechnung
	private static int graymap_move_area_min_size = 10;			// 2 - 50, je extremer desto mehr fehler
	private static int graymap_move_area_gap_max_Size = 5;		// 1-100, je extremer desto mehr fehler
	private static int graymap_max_gray = 255;					// 255
	private static int graymap_max_unknown_gray = 127;			// 127
	private static double graymap_gray_Step = 0;				// berechnung
	private static int graymap_recognition_threshold = 128;		// graymap_max_unknown_gray+1 bis graymap_max_gray
	private static int graymap_update_direction_factor = 4;		// 1 - 10;
	private static double graymap_update_factor =(double)0.05;  // 0.01 - 0.08, nicht zu klein wählen, wegen datentyp rundungs problemen
	private static boolean graymap_edge_accuracy = true;		// false
	
	/**
	 * Clustering = objekte finden
	 * clustering_state 			= ob der algorythmus aktiv ist
	 * clustering_threshold 		= eine schwelle an entfernung ab dem zwei punkte dem selben kluster angehören
	 * clustering_search_range 		= anzahl an punkten die rückwers überprüft werden, ob sie den selben cluster angehören. sollte so klein gewählt werden wie möglich, aber sogroß das, dass selbe ergebnis wie bei einer beliebig großen zahl herauskommt
	 * clustering_min_cluster_size 	= die minimale anzahl an elementen die ein cluster benötigt damit er als Cluster anerkannt wird. soll messfehler ausgleichen
	 * 								  \-> Die anzahl wird in relation zur Entfernung zum Mittelpunkt gesetzt um kleine kluster verstärkt im nahen bereich zu filtern
	 * 									  Damit soll die messungenauigkeit in der entfernung berücksichtigt werden
	 */
	private static boolean clustering_state = true;				// true
	private static double clustering_threshold = 1.5;				// 0.7				// 1.2 
	private static int clustering_search_range = 50;			// 1 - 1000000, kleiner = besser
	private static int clustering_min_cluster_size = 10;		// 1 - 1000000, sollte so gewählt werden das kleine gegenstände bei maximaler entfernung erkannt werden, möglicherweise eine entfernung zur mitte in bezugziehen
	
	/**
	 * straighten			= glätten
	 * straigthen_state		= ob geglättet wirt
	 * straightenFactor 	= die stärke der glättung, kleine werte reichen meist
	 * straighten_type_enum - arithmetic: Das ArithmetischeMittel
	 * 						- harmonic: Das HarmonischeMittel
	 * 						- geometric: Das GeometrischeMittel
	 * straigthen_type		= der Algorythmus der zum Glätten verwendet wird
	 */
	private static boolean straigthen_state = false;													// true
	private static int straightenFactor = 4;													// 1- 30, klein = wenig, groß = sehr glatt, meist zu glatt,
	public enum straighten_type_enum {arithmetic,harmonic,geometric};
	private static straighten_type_enum straigthen_type = straighten_type_enum.arithmetic ;		// arithmetic
	
	public Settings(){
		updateAllValues();
	}
	
	public static void updateAllValues(){
		angle_angle = (double)angle_total/angle_number;
		angle_tan_array = new double[angle_number];
		for(int i=0;i<angle_number;i++){
			angle_tan_array[i] = Math.tan(Math.toRadians((double)(angle_angle*i)));
		}
		graymap_angle_steps = (angle_number / graymap_angle_size);
		graymap_section_steps = (graymap_section_count / graymap_section_size);
		graymap_gray_Step = (((double)graymap_max_unknown_gray-1)/((double)graymap_section_steps));
	
	}

	
	public static int getAngle_number() {
		return angle_number;
	}
	public static void setAngle_number(int angle_number) {
		if(angle_number > 100 && angle_number < 10000){
			Settings.angle_number = angle_number;
			updateAllValues();
		}
	}

	public static int getAngle_total() {
		return angle_total;
	}
	public static void setAngle_total(int angle_total) {
		if(angle_total > 1 && angle_total <= 360){
			Settings.angle_total = angle_total;
			updateAllValues();
		}
	}

	public static double getAngle_angle() {
		return angle_angle;
	}

	
	public static double[] getAngle_tan_array() {
		return angle_tan_array;
	}


	// GRAYMAP
	
	
	public static boolean isGraymap_state() {
		return graymap_state;
	}
	public static void setGraymap_state(boolean graymap_state) {
		Settings.graymap_state = graymap_state;
	}

	
	public static int getGraymap_angle_size() {
		return graymap_angle_size;
	}
	public static void setGraymap_angle_size(int graymap_angle_size) {
		if(graymap_angle_size >0 && graymap_angle_size< angle_number){
			Settings.graymap_angle_size = graymap_angle_size;
			Settings.updateAllValues();
		}
	}

	
	public static int getGraymap_angle_steps() {
		return graymap_angle_steps;
	}


	public static int getGraymap_section_count() {
		return graymap_section_count;
	}
	public static void setGraymap_section_count(int graymap_section_count) {
		if(graymap_section_count >0 && graymap_section_count < 100000){
			Settings.graymap_section_count = graymap_section_count;
			Settings.updateAllValues();
		}
	}

	
	public static int getGraymap_section_size() {
		return graymap_section_size;
	}
	public static void setGraymap_section_size(int graymap_section_size) {
		if(graymap_section_size >0 && graymap_section_size<graymap_section_count){
			Settings.graymap_section_size = graymap_section_size;
			Settings.updateAllValues();
		}
	}

	
	public static int getGraymap_section_steps() {
		return graymap_section_steps;
	}

	
	public static int getGraymap_move_area_min_size() {
		return graymap_move_area_min_size;
	}
	public static void setGraymap_move_area_min_size(int graymap_move_area_min_size) {
		if(graymap_move_area_min_size>0 && graymap_move_area_min_size<500){
			Settings.graymap_move_area_min_size = graymap_move_area_min_size;
			Settings.updateAllValues();
		}
	}

	
	public static int getGraymap_move_area_gap_max_Size() {
		return graymap_move_area_gap_max_Size;
	}
	public static void setGraymap_move_area_gap_max_Size(int graymap_move_area_gap_max_Size) {
		if(graymap_move_area_gap_max_Size>0 && graymap_move_area_gap_max_Size < 500){
			Settings.graymap_move_area_gap_max_Size = graymap_move_area_gap_max_Size;
			Settings.updateAllValues();
		}
	}

	
	public static int getGraymap_max_gray() {
		return graymap_max_gray;
	}
	protected static void setGraymap_max_gray(int graymap_max_gray) {
		if(graymap_max_gray>0 && graymap_max_gray<=255){
			Settings.graymap_max_gray = graymap_max_gray;
			Settings.updateAllValues();
		}
	}

	
	public static int getGraymap_max_unknown_gray() {
		return graymap_max_unknown_gray;
	}
	protected static void setGraymap_max_unknown_gray(int graymap_max_unknown_gray) {
		if(graymap_max_unknown_gray>0 && graymap_max_unknown_gray<graymap_max_gray){
			Settings.graymap_max_unknown_gray = graymap_max_unknown_gray;
			Settings.updateAllValues();
		}
	}

	public static double getGraymap_gray_Step() {
		return graymap_gray_Step;
	}


	public static int getGraymap_recognition_threshold() {
		return graymap_recognition_threshold;
	}
	public static void setGraymap_recognition_threshold(int graymap_recognition_threshold) {
		if(graymap_recognition_threshold>graymap_max_unknown_gray && graymap_recognition_threshold<graymap_max_gray){
			Settings.graymap_recognition_threshold = graymap_recognition_threshold;
			Settings.updateAllValues();
		}
	}

	
	public static int getGraymap_update_direction_factor() {
		return graymap_update_direction_factor;
	}
	public static void setGraymap_update_direction_factor(int graymap_update_direction_factor) {
		if(graymap_update_direction_factor>0 && graymap_update_direction_factor<20){
			Settings.graymap_update_direction_factor = graymap_update_direction_factor;
			Settings.updateAllValues();
		}
	}

	
	public static double getGraymap_update_factor() {
		return graymap_update_factor;
	}
	public static void setGraymap_update_factor(double graymap_update_factor) {
		if(graymap_update_factor>0 && graymap_update_factor<1){
			Settings.graymap_update_factor = graymap_update_factor;
			Settings.updateAllValues();
		}
	}

	
	public static boolean isGraymap_edge_accuracy() {
		return graymap_edge_accuracy;
	}
	public static void setGraymap_edge_accuracy(boolean graymap_edge_accuracy) {
		Settings.graymap_edge_accuracy = graymap_edge_accuracy;
		Settings.updateAllValues();
	}

	
	// CLUSTERING
	
	
	public static boolean isClustering_state() {
		return clustering_state;
	}
	public static void setClustering_state(boolean clustering_state) {
		Settings.clustering_state = clustering_state;
		Settings.updateAllValues();
	}

	
	public static double getClustering_threshold() {
		return clustering_threshold;
	}
	public static void setClustering_threshold(double clustering_threshold) {
		if(clustering_threshold>0 && clustering_threshold<20){
			Settings.clustering_threshold = clustering_threshold;
			Settings.updateAllValues();
		}
	}

	
	public static int getClustering_search_range() {
		return clustering_search_range;
	}
	public static void setClustering_search_range(int clustering_search_range) {
		if(clustering_search_range>0 && clustering_search_range< 100000){
			Settings.clustering_search_range = clustering_search_range;
			Settings.updateAllValues();
		}
	}

	
	public static int getClustering_min_cluster_size() {
		return clustering_min_cluster_size;
	}
	public static void setClustering_min_cluster_size(int clustering_min_cluster_size) {
		if(clustering_min_cluster_size>0 && clustering_min_cluster_size<1000){
			Settings.clustering_min_cluster_size = clustering_min_cluster_size;
			Settings.updateAllValues();
		}
	}

	// Glätten
	
	public static boolean isStraigthen() {
		return straigthen_state;
	}
	public static void setStraigthen(boolean straigthen) {
		Settings.straigthen_state = straigthen;
		Settings.updateAllValues();
	}

	public static int getStraightenFactor() {
		return straightenFactor;
	}
	public static void setStraightenFactor(int straightenFactor) {
		if(straightenFactor>0 && straightenFactor<100){
			Settings.straightenFactor = straightenFactor;
			Settings.updateAllValues();
		}
	}

	public static straighten_type_enum getStraigthen_type() {
		return straigthen_type;
	}
	public static void setStraigthen_type(straighten_type_enum straigthen_type) {
		Settings.straigthen_type = straigthen_type;
		Settings.updateAllValues();
	}
}
