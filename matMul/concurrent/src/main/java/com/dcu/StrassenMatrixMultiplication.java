package com.dcu;

import java.util.Random;

public class StrassenMatrixMultiplication {

    public static int[][] multiply(int[][] A, int[][] B) {
        int n = A.length;
        int[][] result = new int[n][n];

        // Check if matrices are square and have the same size
        if (n == B.length && (n & (n - 1)) == 0) {
            // Create tasks for each quadrant of the matrices
            StrassenTask task = new StrassenTask(A, B, result, 0, 0, 0, 0, n);
            Thread thread = new Thread(task);
            thread.start();
        } else {
            throw new IllegalArgumentException("Matrices must be square and have the same size");
        }

        return result;
    }

    private static class StrassenTask implements Runnable {
        private int[][] A;
        private int[][] B;
        private int[][] C;
        private int rowA;
        private int colA;
        private int rowB;
        private int colB;
        private int size;

        public StrassenTask(int[][] A, int[][] B, int[][] C, int rowA, int colA, int rowB, int colB, int size) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.rowA = rowA;
            this.colA = colA;
            this.rowB = rowB;
            this.colB = colB;
            this.size = size;
        }

        @Override
        public void run() {
            if (size == 1) {
                C[rowA][colB] += A[rowA][colA] * B[rowB][colB];
            } else {
                int newSize = size / 2;

                // Create tasks for each quadrant of the matrices
                StrassenTask[] tasks = new StrassenTask[7];
                tasks[0] = new StrassenTask(A, B, C, rowA, colA, rowB, colB, newSize);
                tasks[1] = new StrassenTask(A, B, C, rowA, colA + newSize, rowB + newSize, colB, newSize);
                tasks[2] = new StrassenTask(A, B, C, rowA, colA, rowB, colB + newSize, newSize);
                tasks[3] = new StrassenTask(A, B, C, rowA, colA + newSize, rowB + newSize, colB + newSize, newSize);
                tasks[4] = new StrassenTask(A, B, C, rowA + newSize, colA, rowB, colB, newSize);
                tasks[5] = new StrassenTask(A, B, C, rowA + newSize, colA + newSize, rowB + newSize, colB, newSize);
                tasks[6] = new StrassenTask(A, B, C, rowA + newSize, colA, rowB, colB + newSize, newSize);

                // Start the tasks
                for (StrassenTask task : tasks) {
                    task.run();
                }
            }
        }
    }

    private static void initializeMatrix(int[][] matrix) {
        Random random = new Random();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = random.nextInt(10000); // Change 10000 to the desired range of random values
            }
        }
    }
    //Print the matrix
    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int i : row) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        int size = 4096; // Change the size of the matrices here
        int[][] A = new int[size][size];
        int[][] B = new int[size][size];
        //Initialize the matrices
        initializeMatrix(A);
        initializeMatrix(B);
        // int [][] A ={ {1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16} };
        // int [][] B ={ {1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16} };
        // printMatrix(A);
        // printMatrix(B);
        long startTime = System.currentTimeMillis();

        int[][] result = multiply(A, B);

        long endTime = System.currentTimeMillis();

        long totalTime = endTime - startTime;
        System.out.println("Start Time: " + startTime);
        System.out.println("End Time: " + endTime);
        System.out.println("Total Time: " + totalTime + " milliseconds"); // Print the result
        //printMatrix(result);

    }
}
