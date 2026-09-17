import java.util.*;

public class MCMOptimizer {

    // Matrix Chain Multiplication using Dynamic Programming
    static int[][] dp;
    static int[][] split;

    static int matrixChainOrder(int[] dimensions) {
        int n = dimensions.length - 1;

        dp = new int[n + 1][n + 1];
        split = new int[n + 1][n + 1];

        // Chain length
        for (int length = 2; length <= n; length++) {

            for (int i = 1; i <= n - length + 1; i++) {

                int j = i + length - 1;
                dp[i][j] = Integer.MAX_VALUE;

                for (int k = i; k < j; k++) {

                    int cost = dp[i][k]
                            + dp[k + 1][j]
                            + dimensions[i - 1]
                            * dimensions[k]
                            * dimensions[j];

                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                        split[i][j] = k;
                    }
                }
            }
        }

        return dp[1][n];
    }

    // Print optimal parenthesization
    static String getOptimalOrder(int i, int j) {

        if (i == j) {
            return "A" + i;
        }

        int k = split[i][j];

        return "("
                + getOptimalOrder(i, k)
                + " × "
                + getOptimalOrder(k + 1, j)
                + ")";
    }

    public static void main(String[] args) {

        /*
         * Image-processing matrix chain:
         *
         * A1 = 32 × 32
         * A2 = 32 × 16
         * A3 = 16 × 8
         * A4 = 8 × 4
         *
         * Dimension array:
         * [32, 32, 16, 8, 4]
         */

        int[] dimensions = {32, 32, 16, 8, 4};

        // Calculate optimal MCM cost
        int optimalCost = matrixChainOrder(dimensions);

        // Cost using normal left-to-right multiplication
        int leftToRightCost =
                32 * 32 * 16
                + 32 * 16 * 8
                + 32 * 8 * 4;

        int saved = leftToRightCost - optimalCost;

        System.out.println();
        System.out.println("==============================================");
        System.out.println("   MEDICAL IMAGE OPTIMIZATION USING MCM");
        System.out.println("==============================================");

        System.out.println();
        System.out.println("Matrix Chain:");
        System.out.println("A1 = 32 × 32");
        System.out.println("A2 = 32 × 16");
        System.out.println("A3 = 16 × 8");
        System.out.println("A4 = 8 × 4");

        System.out.println();
        System.out.println("Normal Left-to-Right Cost : " + leftToRightCost);
        System.out.println("Optimal MCM Cost          : " + optimalCost);
        System.out.println("Multiplications Saved     : " + saved);

        System.out.println();
        System.out.println("Optimal Multiplication Order:");
        System.out.println(getOptimalOrder(1, 4));

        System.out.println();
        System.out.println("==============================================");
        System.out.println("MCM OPTIMIZATION COMPLETED");
        System.out.println("==============================================");
    }
}