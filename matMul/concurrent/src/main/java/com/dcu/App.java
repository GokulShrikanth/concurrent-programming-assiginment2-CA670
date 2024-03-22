package com.dcu;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        // Define the size of the matrices
        int size = 3000;

        // Create the matrices
        int[][] matrixA = new int[size][size];
        int[][] matrixB = new int[size][size];
        int[][] resultMatrix = new int[size][size];

        // Initialize the matrices with random values
        initializeMatrix(matrixA);
        initializeMatrix(matrixB);

        // Define the number of threads to use
        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of threads: " + numThreads);

        // Divide the work among the threads using CompletableFuture
        long start = System.nanoTime();
        System.out.println("Start time: " + start);

        CompletableFuture<?>[] futures = new CompletableFuture[numThreads];
        int chunkSize = size / numThreads;
        for (int i = 0; i < numThreads; i++) {
            int startRow = i * chunkSize;
            int endRow = (i + 1) * chunkSize;
            futures[i] = CompletableFuture.runAsync(
                    new StrassenMultiplicationTask(matrixA, matrixB, resultMatrix, size, startRow, endRow));
        }

        // Wait for all tasks to complete
        CompletableFuture.allOf(futures).join();
        long end = System.nanoTime();
        System.out.println("End time: " + end);
        System.out.println("Time taken: " + TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");

        // Print the result matrix
        //printMatrix(resultMatrix);
    }

    private static void initializeMatrix(int[][] matrix) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = random.nextInt(10000); // Change 10000 to the desired range of random values
            }
        }
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int i : row) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }
}

class StrassenMultiplicationTask implements Runnable {
    private final int[][] matrixA;
    private final int[][] matrixB;
    private final int[][] resultMatrix;
    private final int size;
    private final int startRow;
    private final int endRow;

    public StrassenMultiplicationTask(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int size,
                                      int startRow, int endRow) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.resultMatrix = resultMatrix;
        this.size = size;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    public void run() {
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
    }
}
