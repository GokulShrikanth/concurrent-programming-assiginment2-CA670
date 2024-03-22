package com.dcu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class coppersmithWinograd {
    public static int[][] multiply(int[][] matrixA, int[][] matrixB) {
        int rowsA = matrixA.length;
        final int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        int[][] result = new int[rowsA][colsB];

        // Check if the matrices can be multiplied
        if (colsA != matrixB.length) {
            throw new IllegalArgumentException("Matrices cannot be multiplied");
        }

        // Perform matrix multiplication using Coppersmith-Winograd algorithm
        final int[][] intermediate = new int[rowsA][colsB];
        final int[][] rowFactor = new int[rowsA][colsA];
        final int[][] colFactor = new int[colsA][colsB];

        // Calculate row factors
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA / 2; j++) {
                rowFactor[i][2 * j] = matrixA[i][2 * j] + matrixA[i][2 * j + 1];
                rowFactor[i][2 * j + 1] = matrixA[i][2 * j] - matrixA[i][2 * j + 1];
            }
        }

        // Calculate column factors
        for (int i = 0; i < colsA; i++) {
            for (int j = 0; j < colsB / 2; j++) {
                colFactor[i][2 * j] = matrixB[2 * j][i] + matrixB[2 * j + 1][i];
                colFactor[i][2 * j + 1] = matrixB[2 * j][i] - matrixB[2 * j + 1][i];
            }
        }

        // Create a thread pool with the number of available processors
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Perform intermediate multiplication using multiple threads
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                final int row = i;
                final int col = j;
                executor.submit(() -> {
                    for (int k = 0; k < colsA / 2; k++) {
                        intermediate[row][col] += (rowFactor[row][2 * k] * colFactor[2 * k + 1][col])
                                + (rowFactor[row][2 * k + 1] * colFactor[2 * k][col]);
                    }
                });
            }
        }

        // Shutdown the executor and wait for all tasks to complete
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Calculate final result
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                result[i][j] = intermediate[i][j];
                for (int k = 0; k < colsA / 2; k++) {
                    result[i][j] += (rowFactor[i][2 * k] * colFactor[2 * k + 1][j])
                            + (rowFactor[i][2 * k + 1] * colFactor[2 * k][j]);
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[][] matrixA = {{1, 2}, {3, 4}};
        int[][] matrixB = {{5, 6}, {7, 8}};

        int[][] result = multiply(matrixA, matrixB);

        // Print the result
        for (int[] row : result) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }
}
