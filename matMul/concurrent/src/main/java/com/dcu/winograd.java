package com.dcu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class winograd{

        static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        public static int[][] multiply(int[][] matrix1, int[][] matrix2) {
            int n = matrix1.length;
            int[][] result = new int[n][n];
            if (n == 1) {
                result[0][0] = matrix1[0][0] * matrix2[0][0];
            } else {
                int[][] a11 = new int[n / 2][n / 2];
                int[][] a12 = new int[n / 2][n / 2];
                int[][] a21 = new int[n / 2][n / 2];
                int[][] a22 = new int[n / 2][n / 2];
                int[][] b11 = new int[n / 2][n / 2];
                int[][] b12 = new int[n / 2][n / 2];
                int[][] b21 = new int[n / 2][n / 2];
                int[][] b22 = new int[n / 2][n / 2];

                // Divide the matrices into submatrices
                divide(matrix1, a11, 0, 0);
                divide(matrix1, a12, 0, n / 2);
                divide(matrix1, a21, n / 2, 0);
                divide(matrix1, a22, n / 2, n / 2);
                divide(matrix2, b11, 0, 0);
                divide(matrix2, b12, 0, n / 2);
                divide(matrix2, b21, n / 2, 0);
                divide(matrix2, b22, n / 2, n / 2);

                // Create tasks for matrix multiplication
                MatrixMultiplicationTask c11Task = new MatrixMultiplicationTask(a11, b11);
                MatrixMultiplicationTask c12Task = new MatrixMultiplicationTask(a11, b12);
                MatrixMultiplicationTask c21Task = new MatrixMultiplicationTask(a21, b11);
                MatrixMultiplicationTask c22Task = new MatrixMultiplicationTask(a21, b12);

                // Submit tasks to the executor
                executor.submit(c11Task);
                executor.submit(c12Task);
                executor.submit(c21Task);
                executor.submit(c22Task);

                // Wait for all tasks to complete
                try {
                    executor.shutdown();
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Get the results from tasks
                int[][] c11 = c11Task.getResult();
                int[][] c12 = c12Task.getResult();
                int[][] c21 = c21Task.getResult();
                int[][] c22 = c22Task.getResult();

                // Combine the submatrices to get the final result
                combine(c11, result, 0, 0);
                combine(c12, result, 0, n / 2);
                combine(c21, result, n / 2, 0);
                combine(c22, result, n / 2, n / 2);
            }

            return result;
        }

        private static void divide(int[][] matrix, int[][] submatrix, int startRow, int startCol) {
            for (int i = 0; i < submatrix.length; i++) {
                for (int j = 0; j < submatrix.length; j++) {
                    submatrix[i][j] = matrix[startRow + i][startCol + j];
                }
            }
        }

        private static void combine(int[][] submatrix, int[][] matrix, int startRow, int startCol) {
            int n = submatrix.length;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    matrix[startRow + i][startCol + j] = submatrix[i][j];
                }
            }
        }

        public static void main(String[] args) {
            int[][] A = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}};
            int[][] B = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}};
            StrassenMatrixMultiplication.printMatrix(A);
            StrassenMatrixMultiplication.printMatrix(B);
            long startTime = System.currentTimeMillis();

            int[][] result = multiply(A, B);
            int [][] result1 = StrassenMatrixMultiplication.multiplyNaive(A, B);
            if(StrassenMatrixMultiplication.equals(result, result1)){
                System.out.println("Correct");
            }else{
                System.out.println("Incorrect");
            }
            System.out.println("Result:");
            StrassenMatrixMultiplication.printMatrix(result);
            System.out.println("Result1:");
            StrassenMatrixMultiplication.printMatrix(result1);

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Start Time: " + startTime);
            System.out.println("End Time: " + endTime);
            System.out.println("Total Time: " + totalTime + " milliseconds"); // Print the result
        }

        static class MatrixMultiplicationTask implements Runnable {
            private int[][] matrix1;
            private int[][] matrix2;
            private int[][] result;

            public MatrixMultiplicationTask(int[][] matrix1, int[][] matrix2) {
                this.matrix1 = matrix1;
                this.matrix2 = matrix2;
                this.result = new int[matrix1.length][matrix2[0].length];
            }

            public int[][] getResult() {
                return result;
            }

            @Override
            public void run() {
                int n = matrix1.length;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < matrix2[0].length; j++) {
                        for (int k = 0; k < n; k++) {
                            result[i][j] += matrix1[i][k] * matrix2[k][j];
                        }
                    }
                }
            }
        }
    }
