import java.util.*;


public class MCMOptimizer {

    // ========================================================
    // CO3 - DYNAMIC PROGRAMMING
    // ========================================================

    /*
     * dp[i][j] stores the minimum number of scalar
     * multiplications needed to multiply matrices
     * Ai through Aj.
     */
    static int[][] dp;

    /*
     * split[i][j] stores the position where the optimal
     * matrix chain should be divided.
     */
    static int[][] split;


    // ========================================================
    // CO3 - MATRIX CHAIN MULTIPLICATION
    // INTERVAL DYNAMIC PROGRAMMING
    // ========================================================

    static int matrixChainOrder(int[] dimensions) {

        /*
         * If dimensions = [32,32,16,8,4]
         *
         * Number of matrices = dimensions.length - 1
         */
        int n = dimensions.length - 1;

        dp = new int[n + 1][n + 1];

        split = new int[n + 1][n + 1];


        /*
         * CO3:
         *
         * Interval DP considers matrix chains
         * of increasing length.
         *
         * length = 2 -> two matrices
         * length = 3 -> three matrices
         * length = 4 -> four matrices
         */
        for (int length = 2; length <= n; length++) {

            /*
             * Starting matrix
             */
            for (int i = 1;
                 i <= n - length + 1;
                 i++) {

                /*
                 * Ending matrix
                 */
                int j = i + length - 1;

                /*
                 * Initially set the cost to maximum.
                 */
                dp[i][j] = Integer.MAX_VALUE;


                /*
                 * CO3:
                 *
                 * Try every possible split point.
                 *
                 * Example:
                 *
                 * A1 A2 A3
                 *
                 * Possible splits:
                 *
                 * A1 | A2 A3
                 * A1 A2 | A3
                 */
                for (int k = i; k < j; k++) {

                    /*
                     * Matrix Chain Multiplication recurrence:
                     *
                     * Cost =
                     *
                     * Left subproblem
                     * +
                     * Right subproblem
                     * +
                     * Cost of multiplying both results
                     */
                    int cost =
                            dp[i][k]
                            + dp[k + 1][j]
                            + dimensions[i - 1]
                            * dimensions[k]
                            * dimensions[j];


                    /*
                     * CO3:
                     *
                     * Select the minimum cost.
                     *
                     * This is the optimization step
                     * of Dynamic Programming.
                     */
                    if (cost < dp[i][j]) {

                        dp[i][j] = cost;

                        /*
                         * Store the best split position.
                         */
                        split[i][j] = k;
                    }
                }
            }
        }

        /*
         * Minimum cost for the complete matrix chain.
         */
        return dp[1][n];
    }


    // ========================================================
    // CO3 - OPTIMAL ORDER RECONSTRUCTION
    // ========================================================

    static String getOptimalOrder(int i, int j) {

        /*
         * Base case:
         *
         * A single matrix does not require multiplication.
         */
        if (i == j) {
            return "A" + i;
        }

        /*
         * Get the best split position.
         */
        int k = split[i][j];

        /*
         * Recursively construct the optimal
         * parenthesization.
         */
        return "("
                + getOptimalOrder(i, k)
                + " × "
                + getOptimalOrder(k + 1, j)
                + ")";
    }


    // ========================================================
    // CO4 - NETWORK FLOW
    // ========================================================
    //
    // Edmonds-Karp Algorithm
    //
    // Edmonds-Karp is an implementation of the
    // Ford-Fulkerson method where BFS is used to
    // find augmenting paths.
    //
    // It demonstrates:
    //
    // 1. Network representation
    // 2. Capacity constraints
    // 3. BFS
    // 4. Residual graph
    // 5. Augmenting paths
    // 6. Maximum flow
    // ========================================================


    /*
     * CO4:
     *
     * BFS searches for an augmenting path from
     * source to sink in the residual network.
     */
    static boolean bfs(
            int[][] capacity,
            int[][] flow,
            int source,
            int sink,
            int[] parent) {


        int n = capacity.length;

        boolean[] visited = new boolean[n];

        Queue<Integer> queue =
                new LinkedList<>();


        /*
         * Start BFS from source.
         */
        queue.add(source);

        visited[source] = true;

        parent[source] = -1;


        while (!queue.isEmpty()) {

            int u = queue.poll();


            /*
             * Check all possible neighbouring nodes.
             */
            for (int v = 0; v < n; v++) {

                /*
                 * Residual capacity:
                 *
                 * capacity - current flow
                 */
                int residualCapacity =
                        capacity[u][v]
                        - flow[u][v];


                /*
                 * If the node has not been visited
                 * and there is available capacity,
                 * it can be part of an augmenting path.
                 */
                if (!visited[v]
                        && residualCapacity > 0) {

                    parent[v] = u;

                    visited[v] = true;


                    /*
                     * Sink reached.
                     */
                    if (v == sink) {
                        return true;
                    }


                    queue.add(v);
                }
            }
        }

        return false;
    }


    /*
     * CO4:
     *
     * Edmonds-Karp calculates the maximum flow
     * from source to sink.
     */
    static int edmondsKarp(
            int[][] capacity,
            int source,
            int sink) {


        int n = capacity.length;


        /*
         * Flow matrix.
         *
         * flow[u][v] represents the current flow
         * from node u to node v.
         */
        int[][] flow =
                new int[n][n];


        /*
         * parent[] stores the augmenting path
         * discovered by BFS.
         */
        int[] parent =
                new int[n];


        /*
         * Total maximum flow.
         */
        int maxFlow = 0;


        /*
         * Continue finding augmenting paths
         * until no path exists.
         */
        while (bfs(
                capacity,
                flow,
                source,
                sink,
                parent)) {


            /*
             * Find the minimum residual capacity
             * along the augmenting path.
             *
             * This is called the bottleneck capacity.
             */
            int pathFlow =
                    Integer.MAX_VALUE;


            int current = sink;


            while (current != source) {

                int previous =
                        parent[current];


                int residual =
                        capacity[previous][current]
                        - flow[previous][current];


                pathFlow =
                        Math.min(
                                pathFlow,
                                residual);


                current = previous;
            }


            /*
             * Update flow along the augmenting path.
             */
            current = sink;


            while (current != source) {

                int previous =
                        parent[current];


                /*
                 * Increase forward flow.
                 */
                flow[previous][current]
                        += pathFlow;


                /*
                 * Decrease reverse flow.
                 *
                 * This creates the residual graph.
                 */
                flow[current][previous]
                        -= pathFlow;


                current = previous;
            }


            /*
             * Add the path flow to total maximum flow.
             */
            maxFlow += pathFlow;
        }


        return maxFlow;
    }


    // ========================================================
    // MAIN METHOD
    // ========================================================

    public static void main(String[] args) {


        // ====================================================
        // CO1 - PROBLEM CLASSIFICATION
        // ====================================================

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "     MEDICAL IMAGE OPTIMIZATION PROJECT"
        );

        System.out.println(
                "=================================================="
        );


        System.out.println();

        System.out.println(
                "COURSE OUTCOME IMPLEMENTATION"
        );

        System.out.println(
                "--------------------------------------------------"
        );

        System.out.println(
                "CO1 : Problem Classification & Strategy Selection"
        );

        System.out.println(
                "CO3 : Interval Dynamic Programming"
        );

        System.out.println(
                "CO4 : Network Flow - Edmonds-Karp"
        );


        // ====================================================
        // CO1 - PROBLEM MODELING
        // ====================================================

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "CO1 - PROBLEM CLASSIFICATION"
        );

        System.out.println(
                "=================================================="
        );


        System.out.println();

        System.out.println(
                "Problem:"
        );

        System.out.println(
                "Medical image processing optimization"
        );


        System.out.println();

        System.out.println(
                "Selected Strategy:"
        );

        System.out.println(
                "Matrix Chain Multiplication using Dynamic Programming"
        );


        System.out.println();

        System.out.println(
                "Reason:"
        );

        System.out.println(
                "The problem has optimal substructure"
        );

        System.out.println(
                "and overlapping subproblems."
        );


        // ====================================================
        // CO3 - MATRIX CHAIN MULTIPLICATION
        // ====================================================

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "CO3 - INTERVAL DYNAMIC PROGRAMMING"
        );

        System.out.println(
                "=================================================="
        );


        /*
         * Medical image-processing matrix chain.
         *
         * A1 = 32 × 32
         * A2 = 32 × 16
         * A3 = 16 × 8
         * A4 = 8 × 4
         *
         * Dimension array:
         *
         * [32, 32, 16, 8, 4]
         */

        int[] dimensions = {
                32, 32, 16, 8, 4
        };


        System.out.println();

        System.out.println(
                "Matrix Chain:"
        );

        System.out.println(
                "A1 = 32 × 32"
        );

        System.out.println(
                "A2 = 32 × 16"
        );

        System.out.println(
                "A3 = 16 × 8"
        );

        System.out.println(
                "A4 = 8 × 4"
        );


        /*
         * Execute Matrix Chain Multiplication.
         */
        int optimalCost =
                matrixChainOrder(dimensions);


        /*
         * Cost of normal left-to-right multiplication:
         *
         * ((A1 × A2) × A3) × A4
         */
        int leftToRightCost =
                32 * 32 * 16
                + 32 * 16 * 8
                + 32 * 8 * 4;


        /*
         * Calculate saved scalar multiplications.
         */
        int saved =
                leftToRightCost
                - optimalCost;


        System.out.println();

        System.out.println(
                "Normal Left-to-Right Cost : "
                + leftToRightCost
        );

        System.out.println(
                "Optimal MCM Cost          : "
                + optimalCost
        );

        System.out.println(
                "Multiplications Saved     : "
                + saved
        );


        System.out.println();

        System.out.println(
                "Optimal Multiplication Order:"
        );

        System.out.println(
                getOptimalOrder(1, 4)
        );


        // ====================================================
        // CO4 - NETWORK FLOW
        // ====================================================

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "CO4 - NETWORK FLOW"
        );

        System.out.println(
                "=================================================="
        );


        System.out.println();

        System.out.println(
                "Algorithm: Edmonds-Karp"
        );

        System.out.println(
                "Based on: Ford-Fulkerson Method"
        );

        System.out.println(
                "Path Search: Breadth First Search (BFS)"
        );


        /*
         * ----------------------------------------------------
         * MEDICAL IMAGE PROCESSING NETWORK
         * ----------------------------------------------------
         *
         * Node 0 = Source
         * Node 1 = MRI Processing
         * Node 2 = X-Ray Processing
         * Node 3 = Image Optimization
         * Node 4 = Output Processing
         * Node 5 = Sink
         *
         *
         * Example:
         *
         *              MRI
         *             /   \
         * Source ----     ---- Optimization ---- Sink
         *             \   /
         *             X-Ray
         *
         * Edge values represent processing capacities.
         */


        int[][] capacity = {

                // S    MRI  XRY  OPT  OUT  T

                { 0,   10,   8,   0,   0,   0 }, // Source

                { 0,    0,   0,   6,   4,   0 }, // MRI

                { 0,    0,   0,   4,   6,   0 }, // X-Ray

                { 0,    0,   0,   0,   0,   8 }, // Optimization

                { 0,    0,   0,   0,   0,   8 }, // Output

                { 0,    0,   0,   0,   0,   0 }  // Sink
        };


        /*
         * Source node.
         */
        int source = 0;


        /*
         * Sink node.
         */
        int sink = 5;


        /*
         * Execute Edmonds-Karp.
         */
        int maxFlow =
                edmondsKarp(
                        capacity,
                        source,
                        sink);


        System.out.println();

        System.out.println(
                "Network:"
        );

        System.out.println(
                "Source → MRI/X-Ray → Processing → Output → Sink"
        );


        System.out.println();

        System.out.println(
                "Maximum Processing Flow : "
                + maxFlow
        );


        System.out.println();

        System.out.println(
                "CO4 Concepts Demonstrated:"
        );

        System.out.println(
                "1. Capacity-constrained network"
        );

        System.out.println(
                "2. BFS-based augmenting path"
        );

        System.out.println(
                "3. Residual graph"
        );

        System.out.println(
                "4. Maximum flow"
        );

        System.out.println(
                "5. Ford-Fulkerson / Edmonds-Karp"
        );


        // ====================================================
        // FINAL SUMMARY
        // ====================================================

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "              PROJECT CO SUMMARY"
        );

        System.out.println(
                "=================================================="
        );

        System.out.println();

        System.out.println(
                "CO1 → Problem Classification & Algorithm Selection"
        );

        System.out.println(
                "CO3 → Matrix Chain Multiplication / Interval DP"
        );

        System.out.println(
                "CO4 → Edmonds-Karp / Maximum Network Flow"
        );

        System.out.println();

        System.out.println(
                "CO2 → Not used"
        );

        System.out.println(
                "CO5 → Not used"
        );

        System.out.println(
                "CO6 → Not used"
        );

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "           PROGRAM COMPLETED SUCCESSFULLY"
        );

        System.out.println(
                "=================================================="
        );
    }
}