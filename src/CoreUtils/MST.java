package CoreUtils;

import CoreUtils.UsefulContainers.Edge;
import CoreUtils.UsefulContainers.iPair;

import java.util.List;

/**
 * Class containing Minimum Spanning Tree (MST) utils.  No interface provided because functions are static.
 *
 * <bold>251 students: You may only use java.util.List and java.util.ArrayList from the standard library.
 *   Any other containers used must be ones you created.</bold>
 */
public class MST {
    /**
     * Returns the MST of the given graph, optimized for a dense graph.  Assumes a connected graph.
     *
     * @param weights square matrix representing positive edge weights between every vertex
     * @return MST: list of pairs of indices each indicating an edge between those two indices
     * @throws IllegalArgumentException if weights is not square or edges are not positive
     */
    public static List<iPair> denseMST(double[][] weights) throws IllegalArgumentException {
        //validate weighs matrix (already done)
        int n = weights.length;
        for(int i=0; i<n; i++){
            if(weights[i].length != n)
                throw new IllegalArgumentException("Weights graph not square in row " +
                        i + ", expected " + n + ", actual is " + weights[i].length);
            for(int j=0; j<n; j++){
                if(weights[i][j] < 0)
                    throw new IllegalArgumentException("Edge weight < 0 (" +
                            weights[i][j] + ") at y, x=" + i + ", " + j);
            }
        }

        //todo
        return null;
    }

    /**
     * Returns the MST of the given graph, optimized for a sparse graph.  Assumes a connected graph.
     *
     * @param edgeList edge list
     * @param n number of vertices
     * @return MST: list of pairs of indices each indicating an edge between those two indices
     * @throws IllegalArgumentException if edges are not positive
     */
    public static List<iPair> sparseMST(List<Edge> edgeList, int n) throws IllegalArgumentException {
        //validate edge weighs (already done)
        for(var e : edgeList){
            if(e.w < 0)
                throw new IllegalArgumentException("Edge weight < 0 (" +
                        e.w + ") between " + e.a + " and " + e.b);
        }

        //todo
        return null;
    }
}
