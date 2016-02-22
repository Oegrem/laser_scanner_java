package data_processing;

import java.util.Vector;

public class Clustering {

	
	
	public Clustering(){
		
	}

	public Vector<SimpleCluster> cluster(Vector<Long> sensorData){
		Vector<SimpleCluster> clusterList = new Vector<>();
		Vector<Integer> sensorDataID = new Vector<Integer>();
		long currentSData = 0;
		int clusterCount = 0;
		int searchRange = Settings.getClustering_search_range();
		int minClusterSize = Settings.getClustering_min_cluster_size();
		for(int i=0;i<sensorData.size();i++){			
			currentSData = sensorData.get(i);			
			// look at i - x elements 
			// durchl�uft bei kluster grenzen, fehlerhaften daten mehrfach
			for(int j=1;j<searchRange;j++){
				if(i-j >0){
					// if it fits to previous cluster
					if(insideThresholdValue(currentSData,sensorData.get(i-j),j)){
						int id = sensorDataID.get(i-j);
						sensorDataID.add(id);
						clusterList.get(id).increaseElementCount();
						clusterList.get(id).setLastElement(i);
						clusterList.get(id).increaseDistanceSum(currentSData);
						break;
					}
				}else break;
			}
			// test if it was asignet to a cluster
			if(sensorDataID.size() == i){
				sensorDataID.add(clusterCount);
				// ID, elemente anzahl, startposition, entposition, distance
				clusterList.add( new SimpleCluster(clusterCount,1,i,i,currentSData));
				clusterCount++;
			}
		}		
		
		// zu kleine Kluster entfernen
		// die kluster entfernung spielt eine rolle, die element anzahl muss bei n�heren objekten gr��er sein als bei weit entfertnet.
		// damit sollen kleine kluster die f�lschlich erkannt werden im nahen bereich gefltert, und kleine kluster in weiter nefernung die 
		// durch geringe abtastraten wenig elemente besitzen trotzdem ber�cksichtigt werden
		for(int i=0;i<clusterList.size();i++){
			//System.out.println(clusterList.get(i));
			//System.out.println(clusterList.get(i));
			if(clusterList.get(i).getEelementCount() < minClusterSize*Settings.getAngle_number()*clusterList.get(i).getEelementCount()/clusterList.get(i).getDistanceSum()){
				clusterList.remove(i);
				i=i-1;
			}	
		}
		//System.out.println("");
		return clusterList;
	}
	
	private boolean insideThresholdValue(long long1, long long2,int leaps){
		long difference = long2 - long1;
		if(long1 > long2){
			difference = long1 - long2;
		}
		if(difference < Settings.getClustering_threshold_value()*(Settings.getClustering_threshold_factor()/leaps - Settings.getClustering_threshold_factor_minus()))
			return true;
		return false;
	}
	
}
