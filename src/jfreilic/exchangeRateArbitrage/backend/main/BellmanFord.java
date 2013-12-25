package jfreilic.exchangeRateArbitrage.backend.main;

import java.util.LinkedList;
import java.util.List;

public class BellmanFord {
	
	/*
	   for each vertex v in vertices:
	       if v is source then weight[v] := 0
	       else weight[v] := infinity
	       predecessor[v] := null

	   // Step 2: relax edges repeatedly
	   for i from 1 to size(vertices)-1:
  	       for each edge (u, v) with weight w in edges:
           if weight[u] + w < weight[v]:
               weight[v] := weight[u] + w
               predecessor[v] := u

	   // Step 3: check for negative-weight cycles
	   for each edge (u, v) with weight w in edges:
	       if weight[u] + w < weight[v]:
	           error "Graph contains a negative-weight cycle"
	   return weight[],predecessor[]
	   */
	
	public static List<Integer> negativeWeightCycle(double[][] adjacenyMatrix, int source) throws IllegalArgumentException {
		if (adjacenyMatrix.length == 0 ||  adjacenyMatrix.length != adjacenyMatrix[0].length) {
			throw new IllegalArgumentException("Adjaceny Matrix is not a square matrix!");
		}
		
		int[] predecessors = new int[adjacenyMatrix.length];
		double[] distance = new double[adjacenyMatrix.length];
		double[][] logValMat = createLogValueMatrix(adjacenyMatrix);
		
		// set initial distances to infinity for all except source
		for (int j = 0; j < adjacenyMatrix.length; j++) {
			distance[j] = Double.MAX_VALUE;
		}
		distance[source] = 0;
		
		// relax the edges vertices - 1 number of times
		for (int i = 0; i < logValMat.length - 1; i++) {
			relaxEdges(logValMat, distance, predecessors);
		}
		
		return findNegativeWeightCycle(logValMat, distance, predecessors);
	}

	private static double[][] createLogValueMatrix(double[][] adjacenyMatrix){
		double[][] logValMat = adjacenyMatrix.clone();
		for (int i=0;i<adjacenyMatrix.length;i++){
			for (int j=0;j<adjacenyMatrix[0].length;j++){
				double weight = adjacenyMatrix[i][j];
				if (weight > 0) {
					logValMat[i][j] = -Math.log(weight);
				} else {
					logValMat[i][j] = Double.MAX_VALUE;
				}
			}
		}
		return logValMat;
	}
	
	private static void relaxEdges(double[][] logValMat,
			double[] distance, int[] predecessors) {
		
		for (int v = 0; v < logValMat.length; v++) {
			for (int j = 0; j < logValMat.length; j++) {
				double weight = logValMat[v][j];
				if (weight < Double.MAX_VALUE) {
					if (weight + distance[v] < distance[j]) {
						distance[j] = distance[v] + weight;
						predecessors[j] = v;
					}
				}
			}
		}
	}
		   
	private static List<Integer> findNegativeWeightCycle(double[][] logValMat,
			double[] distance, int[] predecessors) {
		
		for (int v = 0; v < logValMat.length; v++) {
			for (int j = 0; j < logValMat[0].length; j++) {
				double weight = logValMat[v][j];
				if( weight < Double.MAX_VALUE) {
					if (weight + distance[v] < distance[j]){
						predecessors[j] = v;
						return createCycleFromPredecessors(predecessors, j);
					}
				}
			}
		}
		return new LinkedList<Integer>();
	}
	
	public static List<Integer> createCycleFromPredecessors(int[] predecessors, int end) {
		LinkedList<Integer> path = new LinkedList<>();
		boolean[] visited = new boolean[predecessors.length];
		int current = end;
		while (true){
			if (visited[current]){
				LinkedList<Integer> cycle = new LinkedList<>();
				cycle.addFirst(current);
				for (Integer item : path) {
					cycle.add(item);
					if (item.intValue() == current) {
						break;
					}	
				}
				return cycle;
			}
			path.addFirst(current);
			visited[current] = true;
			current = predecessors[current];
		}
	}
}