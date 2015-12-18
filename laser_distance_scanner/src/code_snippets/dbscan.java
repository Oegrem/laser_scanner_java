package code_snippets;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import static code_snippets.clusterPoint.NOTVISITED;
import static code_snippets.clusterPoint.VISITED;
import static code_snippets.clusterPoint.NOISE;

public class dbscan {

	private static int densitySize = 10;

	private static int densityRange = 60;
	
	private static Vector<clusterLineStrip> lastLineStrip = new Vector<clusterLineStrip>();

	public dbscan(int _densitySize, int _densityRange) {
		densityRange = _densityRange;
		densitySize = _densitySize;
	}

	public static Vector<Vector<clusterPoint>> cluster(CopyOnWriteArrayList<Point> _pVector) {
		Vector<Vector<clusterPoint>> clusters = new Vector<Vector<clusterPoint>>();

		Vector<clusterPoint> cluster = new Vector<clusterPoint>();
		
		
		clusterPoint cP;

		for (Point p : _pVector) {
			cP = new clusterPoint(p.getX(), p.getY());

			cluster.add(cP);
		}

		Vector<clusterPoint> neighbours = new Vector<clusterPoint>();

		for (clusterPoint cp : cluster) {
			if (cp.getStatus() == NOTVISITED) {
				cp.setStatus(VISITED);
				neighbours.clear();
				neighbours = cp.getNeighbours(cluster, densityRange);
				if (neighbours.size() < densitySize) {
					cp.setStatus(NOISE);
				} else {
					Vector<clusterPoint> nextCl = new Vector<clusterPoint>();
					
					nextCl.addElement(cp);
					cp.setToCluster();
					
					for(clusterPoint nP : neighbours){
						if(!nP.isInCluster()){
							expandCluster(nP, nextCl, cluster);
						}
					}
					
					clusters.add(nextCl);
					
				}
			}
		}
		
		return clusters;

	}

	public static void expandCluster(clusterPoint cp, Vector<clusterPoint> cluster, Vector<clusterPoint> allPoints) {

		cluster.addElement(cp);
		cp.setToCluster();

		if(cp.getStatus() == NOTVISITED){
			cp.setStatus(VISITED);
			Vector<clusterPoint> neighbours = new Vector<clusterPoint>();
			neighbours = cp.getNeighbours(allPoints, densityRange);
			if(neighbours.size() >= densitySize){
				for(clusterPoint nP : neighbours){
					if(!nP.isInCluster()){
						expandCluster(nP, cluster, allPoints);
					}
				}
			}
		}
	}
	
	public static Vector<clusterLineStrip> getClustersAsLines(CopyOnWriteArrayList<Point> _pVector, int precision){
		Vector<Vector<clusterPoint>> vvcp = cluster(_pVector);
		Vector<clusterLineStrip> vvL = new Vector<clusterLineStrip>();
		
		for(Vector<clusterPoint> vCP : vvcp){
			
			vvL.add(Line.getClusterLineVectors(vCP, precision));
			
			
			
		}
		
		if(lastLineStrip.size()<=0){
			for(int i=0;i<vvL.size();i++){
				vvL.get(i).setClusterId(i);
			}
			lastLineStrip.addAll(vvL);
		}else{
			for(clusterLineStrip clS : vvL){
				for(clusterLineStrip lLS : lastLineStrip){
					if(clS.isSimiliar(lLS)){
						clS.setClusterId(lLS);
					}
				}
			}
			int id = 0;
			for(clusterLineStrip c : vvL){
				if(c.getClusterId()==-1){
					for(clusterLineStrip lLs : lastLineStrip){
						if(lLs.getClusterId()==id){
							id++;
						}
					}
					c.setClusterId(id);
					id++;
				}
			}
			
			
			
			lastLineStrip.clear();
			lastLineStrip.addAll(vvL);
		}

		return vvL;
	}
	
	public static int incSize(int inc){
		densitySize+=inc;
		return densitySize;
	}

	public static int incRange(int inc){
		densityRange+=inc;
		return densityRange;
	}
	
	public static Vector<clusterPoint> mergeVector(Vector<clusterPoint> vc1, Vector<clusterPoint> vc2){
		Vector<clusterPoint> clustVect = new Vector<clusterPoint>();
		clustVect.addAll(vc1);
		for(clusterPoint p : vc2){
			boolean isIn = false;
			for(clusterPoint pComp : vc1){
				if(pComp==p){
					isIn = true;
				}
			}
			if(!isIn){
				clustVect.add(p);
			}
		}
		
		return clustVect;
	}

}
