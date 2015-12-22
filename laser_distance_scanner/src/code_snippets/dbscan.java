package code_snippets;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.Sys;

import static code_snippets.clusterPoint.NOTVISITED;
import static code_snippets.clusterPoint.VISITED;
import static code_snippets.clusterPoint.NOISE;

public class dbscan {

	private static int densitySize = 10;

	private static int densityRange  = 60;

	private static Vector<clusterLineStrip> lastLineStrip = new Vector<clusterLineStrip>();

	private static Vector<clusterLineStrip> beforeLineStrip = new Vector<clusterLineStrip>();

	private static int loops = 0;
	
	public dbscan(int _densitySize, int _densityRange) {
		densityRange = _densityRange;
		densitySize = _densitySize;
	}

	public static Vector<Vector<clusterPoint>> cluster(
			CopyOnWriteArrayList<Point> _pVector) {

		loops = 0;
		
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

					for (clusterPoint nP : neighbours) {
						if (!nP.isInCluster()) {
							expandCluster(nP, nextCl, cluster);
						}
					}

					clusters.add(nextCl);

				}
			}

		}

		return clusters;
		
	}

	public static void expandCluster(clusterPoint cp,
			Vector<clusterPoint> cluster, Vector<clusterPoint> allPoints) {

		cluster.addElement(cp);
		cp.setToCluster();

		if (cp.getStatus() == NOTVISITED) {
			cp.setStatus(VISITED);
			Vector<clusterPoint> neighbours = new Vector<clusterPoint>();
			neighbours = cp.getNeighbours(allPoints, densityRange);
			if (neighbours.size() >= densitySize) {
				for (clusterPoint nP : neighbours) {
					if (!nP.isInCluster()) {
						expandCluster(nP, cluster, allPoints);
						
					}
					loops++;
				}
			}
		}
	}

	public static Vector<clusterLineStrip> getClustersAsLines(
			CopyOnWriteArrayList<Point> _pVector, int precision) {

		Vector<Vector<clusterPoint>> vvcp = cluster(_pVector);

		Vector<clusterLineStrip> vvL = new Vector<clusterLineStrip>();

		for (Vector<clusterPoint> vCP : vvcp) {

			vvL.add(Line.getClusterLineVectors(vCP, precision));

		}

			for (clusterLineStrip clS : vvL) {
				clusterLineStrip toRemove = null;

				int nID = clS.getNextCluster(lastLineStrip);

				if (nID != -1) {
					clS.setClusterId(nID);
					clS.recognised = true;
				} else {
					int bID = clS.getNextCluster(beforeLineStrip);
					if (bID != -1) {
						clS.setClusterId(bID);
						clS.recognised = true;
					}
				}
			}

		Vector<Integer> numberVector = new Vector<Integer>();
		for (clusterLineStrip cl : vvL) {
			if (cl.recognised) {
				numberVector.add(cl.getClusterId());
			}
		}

		Integer id = 0;

		for (clusterLineStrip c : vvL) {
			if (!c.recognised) {

				while (numberVector.contains(id)) {
					id++;
				}

				c.setClusterId(id);
				numberVector.add(id);
				id++;

			}
		}

		beforeLineStrip.clear();
		beforeLineStrip.addAll(lastLineStrip);

		lastLineStrip.clear();
		lastLineStrip.addAll(vvL);
		System.out.println(vvcp.size());
		return vvL;
	}

	public static int incSize(int inc) {
		densitySize += inc;
		return densitySize;
	}

	public static int incRange(int inc) {
		densityRange += inc;
		return densityRange;
	}

	public static Vector<clusterPoint> mergeVector(Vector<clusterPoint> vc1,
			Vector<clusterPoint> vc2) {
		Vector<clusterPoint> clustVect = new Vector<clusterPoint>();
		clustVect.addAll(vc1);
		for (clusterPoint p : vc2) {
			boolean isIn = false;
			for (clusterPoint pComp : vc1) {
				if (pComp == p) {
					isIn = true;
				}
			}
			if (!isIn) {
				clustVect.add(p);
			}
		}

		return clustVect;
	}

}
