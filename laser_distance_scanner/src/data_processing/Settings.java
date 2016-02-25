package data_processing;

public class Settings {
	
	
	/**
	 * gobale settings
	 * 
	 * angle_number 	= Anzahl an Sensormessungen 
	 * angle_total 		= Der Winkel des Ausschnits auf dem Gemessen wird, von 0 bis x, die ausrichtung im raum wird nicht berï¿½cksichtigt
	 * angle_angle 		= Der Winkel zwischen 2 direkt nebeneinander liegenden Messungen
	 * angle_tan_array 	= der Tangenz alle Winkel zwischen einer Messunge und einem Beliebigen anzahl plï¿½tze entfernten winkel. Der Index entspricht die der entfernung zur ausgangsmessung
	 * longVersion		= Es gibt 2 grundlegende Implementierungen, eine die ausschlieï¿½lich mit den Long werten des Sensors arbeitet, die zweite arbeitet mit den Long werten + den daraus berechneten kartesischen koordinaten als punkte
	 */
	private static int angle_number = 270*4;
	private static int angle_total = 270;
	private static double angle_angle = 0;
	private static double[] angle_tan_array = null;
	
	/**
	 *   graymap 						= schwarz weiï¿½ schatierte karte. dunkel = objekt, hell = frei
	 *  		  						  \-> wird zur erkennung von bewegenden objekten verwendet
	 *  graymap_state 					= aktiv oder inaktiv
	 *  graymap_direct_adding			= aktiv   -> funktion addNewDataDirect: Erstellt eine Momentane karte, die mit den neuen daten befï¿½llt wird, und die in einem weiteren schritt in die globale karte geupdatet wird
	 *  								  inaktiv -> funktion addNewData:       ï¿½nderungen werden direkt in die Globale Karte gespeichert. Dadurch sollen resourcen gespart werden
	 *
	 *   angle 							= winkel -> kreisfï¿½rmig angeordnete vektoren,
	 *  graymap_angle_size				= anzahl an messwerten die in die selbe Graymap spallte projeziert werden
	 *  graymap_angle_steps 			= anzahl an unabhï¿½ngigen spalten in der graymap, berechnung = angle_number / graymap_angle_size
	 *	
	 *   section 						= abschnitt -> ein einzelner Vektor zerlegt in abschnitte
	 *  graymap_section_count 			= maximale entfernung der messung in mm -> 10 meter 
	 *  graymap_section_size			= grï¿½ï¿½e der graymap section in mm 
	 *  graymap_section_steps			= anzahl an unabhï¿½ngigen sectionen in der Graymap, berechnung aus graymap_section_count / graymap_section_size
	 *	graymap_section_steps_array		= alle einzelnen steps, als array um rechenzeit zu sparen
	 *
	 *   move_area 						= bewegendes gebiet -> bereich das als sich bewegend betrachtet wird
	 *  graymap_move_area_min_size 	   	= mininale anzahl an beieinanderliegenden messwerten eines areals
	 *  graymap_move_area_gap_max_Size 	= maximale lï¿½cke zwischen zwei arealen befor diese vereint werden
	 *  graymap_max_gray 				= der maximalwert der graymap, wird gesetzt wenn ein punkt im feld erkannt wird
	 *  graymap_max_unknown_gray 		= grau wert der gesetzt wird, wenn keine informationen forhanden sind
	 *  graymap_gray_Step 				= ein einzelner verdunkelungs schritt von weiï¿½ zu grau in den sectionen nach auï¿½en, wegen sich ï¿½ffnenden winkeln und daraus entstehenden messfehlern kleiner objekte
	 *
	 *   recognition					= einstellungen die das erkennungsverhalten steuern
	 *  graymap_recognition_threshold 	= schwelle ab der ein objekt als fest erkannt wird
	 *  graymap_update_factor 		 	= factor mit dem die karte angepasst wird
	 *  graymap_update_sympel_direction_center  = Wert mit dem bei mergeValueSimpel der wert in richtung 127 abgï¿½ndert wird
	 *  graymap_update_sympel_direction_extreme = Wert mit dem bei mergeValueSimpel der wert in richtung 0 oder Max Gray abgï¿½ndert wird
	 *  graymap_update_sympel                   = Stellt zwischen mergeValueSimpel und mergeValue um, wobei mergeValue die grï¿½ï¿½ere Rechenzeit besitzt, jedoch schï¿½ner funktioniert
	 *
	 *  graymap_update_direction_factor = fector: neu Dunkel auf alt Hell /, neu dunkel auf alt Dunkel *, neu hell auf alt hell *, neu hell auf alt dunkel /
	 * 									  \-> sorgt dafï¿½r das stationï¿½re objekte lansamer verschwinden und sich bewegende langsamer festsetzen
	 *  graymap_edge_accuracy			= verwaschung der gesetzten punkte in naheliegende graymap felder, wenn an nur eigenes feld, wenn aus auch nachbarfelder,
	 * 									  \-> wenn =false werden ungenauigkeiten der messung ausgeglichen
	 * graymap_visual_server			= zeigt serverseitig ein fenster mit einer linearen representation der graymap an.
	 */
	private static boolean graymap_state = false;				// true! 
	private static boolean graymap_direct_adding = true;		// true, direktes einfï¿½gen ist performanter
	private static int graymap_angle_size = 8;					// 1 - 10 scheint gut zu sein
	private static int graymap_angle_steps = 0;					// berechnung
	private static int graymap_section_count = 10000; 			// 10000 = 10 meter
	private static int graymap_section_size = 500;				// 20 - 500 je grï¿½ï¿½er desto besser
	private static int graymap_section_steps = 0;				// berechnung
	private static int[] graymap_section_steps_array;			// berechnung
	private static int graymap_move_area_min_size = 10;			// 2 - 50, je extremer desto mehr fehler
	private static int graymap_move_area_gap_max_Size = 5;		// 1-100, je extremer desto mehr fehler
	private static int graymap_max_gray = 255;					// 255
	private static int graymap_max_unknown_gray = 127;			// 127
	private static double graymap_gray_Step = 0;				// berechnung
	private static int graymap_recognition_threshold = 128;		// graymap_max_unknown_gray+1 bis graymap_max_gray
	private static int graymap_update_direction_factor = 4;		// 1 - 10;
	private static double graymap_update_factor =(double)0.05;  // 0.01 - 0.08, nicht zu klein wï¿½hlen, wegen datentyp rundungs problemen
	private static int graymap_update_sympel_direction_center = 1; // update in die richtung 127 Gray
	private static int graymap_update_sympel_direction_extreme = 2; //  update in die richtung extreme 0 und Max Gray
	private static boolean graymap_update_sympel = true;		// true, verwendet eine einfachere berechnung der zusammenfï¿½hrung der karten
	private static boolean graymap_edge_accuracy = true;		// true, deutlich erhï¿½hter rechenaufwand
	private static boolean graymap_visual_server = false;		// false, sehr sehr rechenaufwï¿½ndig!
	
	/**
	 * Clustering = objekte finden
	 * clustering_state 				= ob der algorythmus aktiv ist
	 * clustering_threshold_Value   	= eine schwelle an entfernung bis zu dem zwei punkten selben kluster angehï¿½ren,
	 * clustering_threshold_factor_minus= \
	 * clustering_threshold_factor		= -\-> factor und vactor_minus kontrollieren wie groß die schwelle (value) mit zunehmender entfernung der messpunkte schrumpft, bis sie wegen vector_minus unter 0 fällt
	 * clustering_search_range 			= anzahl an punkten die rï¿½ckwers ï¿½berprï¿½ft werden, ob sie den selben cluster angehï¿½ren. sollte so klein gewï¿½hlt werden wie mï¿½glich, aber sogroï¿½ das, dass selbe ergebnis wie bei einer beliebig groï¿½en zahl herauskommt
	 * clustering_min_cluster_size 		= die minimale anzahl an elementen die ein cluster benï¿½tigt damit er als Cluster anerkannt wird. soll messfehler ausgleichen
	 * 								  	\-> Die anzahl wird in relation zur Entfernung zum Mittelpunkt gesetzt um kleine kluster verstï¿½rkt im nahen bereich zu filtern
	 * 									  	Damit soll die messungenauigkeit in der entfernung berï¿½cksichtigt werden
	 * 
	 * cluster zuweisungen erfolgen mit der formel: differenze < clustering_threshold_Value * (clustering_threshold_factor / step - clustering_threshold_factor_minus)
	 * step steht für die entfernung der beiden messpunkte im messvektor. diese reichweite kann mit searchRange begrenzt werden
	 * differenze steht für den längenuterschied der messpunkte zum mittelpunkt
	 */
	private static boolean clustering_state = true;					// true
	private static double clustering_threshold_value = 20.0;		// 10 - 100
	private static double clustering_threshold_factor_minus = 0.1;	//  0.000001 - 1 
	private static double clustering_threshold_factor = 2.0;		// 1 - 10
	private static int clustering_search_range = 300;				// 1 - 1000000, kleiner = besser
	private static int clustering_min_cluster_size = 10;				// 1 - 1000000, sollte so gewï¿½hlt werden das kleine gegenstï¿½nde bei maximaler entfernung erkannt werden, mï¿½glicherweise eine entfernung zur mitte in bezugziehen

	
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
		graymap_section_steps_array = new int[graymap_section_steps];
		for(int i=0;i<graymap_section_steps;i++){
			graymap_section_steps_array[i] = (int) ((i*graymap_gray_Step));
			if(graymap_section_steps_array[i] > graymap_max_unknown_gray)
				graymap_section_steps_array[i] = graymap_max_unknown_gray;
		}
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
	public static boolean isGraymap_direct_adding() {
		return graymap_direct_adding;
	}

	public static void setGraymap_direct_adding(boolean graymap_direct_adding) {
		Settings.graymap_direct_adding = graymap_direct_adding;
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
	public static int[] getGraymap_section_steps_array() {
		return graymap_section_steps_array;
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

	
	public static double getGraymap_update_sympel_direction_center() {
		return graymap_update_sympel_direction_center;
	}
	public static void setGraymap_update_sympel_direction_center(int graymap_update_sympel_direction_center) {
		if(graymap_update_sympel_direction_center > 0)
			Settings.graymap_update_sympel_direction_center = graymap_update_sympel_direction_center;
	}

	public static double getGraymap_update_sympel_direction_extreme() {
		return graymap_update_sympel_direction_extreme;
	}
	public static void setGraymap_update_sympel_direction_extreme(int graymap_update_sympel_direction_extreme) {
		if(graymap_update_sympel_direction_extreme > 0)
			Settings.graymap_update_sympel_direction_extreme = graymap_update_sympel_direction_extreme;
	}

	public static boolean isGraymap_update_sympel() {
		return graymap_update_sympel;
	}

	public static void setGraymap_update_sympel(boolean graymap_update_sympel) {
		Settings.graymap_update_sympel = graymap_update_sympel;
	}

	public static boolean isGraymap_edge_accuracy() {
		return graymap_edge_accuracy;
	}
	public static void setGraymap_edge_accuracy(boolean graymap_edge_accuracy) {
		Settings.graymap_edge_accuracy = graymap_edge_accuracy;
		Settings.updateAllValues();
	}

	
	public static boolean isGraymap_visual_server() {
		return graymap_visual_server;
	}
	public static void setGraymap_visual_server(boolean graymap_visual_server) {
		Settings.graymap_visual_server = graymap_visual_server;
	}

	
	// CLUSTERING
	
	
	public static boolean isClustering_state() {
		return clustering_state;
	}
	public static void setClustering_state(boolean clustering_state) {
		Settings.clustering_state = clustering_state;
		Settings.updateAllValues();
	}

	
	public static double getClustering_threshold_value() {
		return clustering_threshold_value;
	}
	public static void setClustering_threshold_value(double clustering_threshold_value) {
		if(clustering_threshold_value > 1){
			Settings.clustering_threshold_value = clustering_threshold_value;
			Settings.updateAllValues();
		}
	}

	public static double getClustering_threshold_factor_minus() {
		return clustering_threshold_factor_minus;
	}
	public static void setClustering_threshold_factor_minus(double clustering_threshold_factor_minus) {
		if(clustering_threshold_factor_minus > 0){
			Settings.clustering_threshold_factor_minus = clustering_threshold_factor_minus;
			Settings.updateAllValues();
		}
	}	
	
	public static double getClustering_threshold_factor() {
		return clustering_threshold_factor;
	}
	public static void setClustering_threshold_factor(double clustering_threshold_factor) {
		if(clustering_threshold_factor > 0){
			Settings.clustering_threshold_factor = clustering_threshold_factor;
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
}

