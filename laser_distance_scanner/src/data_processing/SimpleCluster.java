package data_processing;

public class SimpleCluster {
	private int ID = -1;				// eindeutige identifikation
	private int elementCount = 0;		// anzahl an elementen
	private int firstElement = -1;		// erste position des ersten elements in der sensorListe
	private int lastElement = -1;		// letzte position
	private long distanceSum = 0;		// summierte entfernungwerte der einzelnen elemente 

	SimpleCluster(){
		ID = -1;
		elementCount = 0;
		firstElement = -1;
		lastElement = -1;
		distanceSum = 0;
	}
	SimpleCluster(int ID){
		this.ID = ID;
		elementCount = 0;
		firstElement = -1;
		lastElement = -1;
		distanceSum = 0;
	}
	SimpleCluster(int ID, int elementCount, int start, int stop, int distance){
		this.ID = ID;
		this.elementCount = elementCount;
		this.firstElement = start;
		this.lastElement = stop;
		this.distanceSum = distance;
	}
	
	public int getID(){
		return ID;
	}
	public boolean setID(int ID){
		if(ID >=0){
			this.ID = ID;
			return true;
		}
		return false;
	}
	
	public void increaseElementCount(){
		elementCount++;
	}
	public int getEelementCount(){
		return elementCount;
	}
	
	public boolean setPosition(int first, int last){
		if(first >=0 && last > first){
			firstElement = first;
			lastElement = last;
			return true;
		}
		return false;
	}
	public boolean setLastElement(int last){
		if(last > firstElement){
			lastElement = last;
			return true;
		}
		return false;
	}
	public boolean setFirstElement(int first){
		if(first >=0){
			firstElement = first;
			return true;
		}
		return false;
	}
	public int[] getPosition(){
		return new int[]{firstElement,lastElement};
	}
	public int getFirstElement(){
		return firstElement;
	}
	public int getLastElement(){
		return lastElement;
	}
	
	public void increaseDistanceSum(int toAdd){
		distanceSum += toAdd;
	}
	public long getDistanceSum(){
		return distanceSum;
	}
	
}
