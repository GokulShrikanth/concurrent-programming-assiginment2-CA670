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

        // int size = 1024; // Change the size of the matrices here
        // int[][] A = new int[size][size];
        // int[][] B = new int[size][size];
        // //Initialize the matrices
        // initializeMatrix(A);
        // initializeMatrix(B);
        int [][] A ={ {1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16} };
        int [][] B ={ {1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16} };
        printMatrix(A);
        printMatrix(B);
        long startTime = System.currentTimeMillis();

        int[][] result = multiply(A, B);
        int[][] result1 = multiplyNaive(A, B);
        if(result == result1)
            System.out.println("Both results are same");
        else
            System.out.println("Both results are different");

        long endTime = System.currentTimeMillis();

        long totalTime = endTime - startTime;
        System.out.println("Start Time: " + startTime);
        System.out.println("End Time: " + endTime);
        System.out.println("Total Time: " + totalTime + " milliseconds"); // Print the result
        printMatrix(result);
        printMatrix(result1);

        int [][] A1 ={ {1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16} };
        int [][] B1 ={ {1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16} };
        printMatrix(A1);
        printMatrix(B1);
        long startTime1 = System.currentTimeMillis();

        int[][] result2 = coppersmithWinograd.multiply(A1, B1);
        if(result == result1)
            System.out.println("Both results are same");
        else
            System.out.println("Both results are different");

        long endTime1 = System.currentTimeMillis();

        long totalTime1 = endTime - startTime;
        System.out.println("Start Time: " + startTime1);
        System.out.println("End Time: " + endTime1);
        System.out.println("Total Time: " + totalTime1 + " milliseconds"); // Print the result
        printMatrix(result);
        printMatrix(result2);

    }

    public static int[][] multiplyNaive(int[][] A, int[][] B) {
        int n = A.length;
        int[][] result = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return result;
    }
}
