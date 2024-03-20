package com.dcu;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        // Define the size of the matrices
        long rowsA = 10000;
        long colsA = 10000;
        long rowsB = 10000;
        long colsB = 10000;

        // Create the matrices
        int[][] matrixA = new int[(int) rowsA][(int) colsA];
        int[][] matrixB = new int[(int) rowsB][(int) colsB];
        int[][] resultMatrix = new int[(int) rowsA][(int) colsB];

        // Initialize the matrices with random values
        initializeMatrix(matrixA);
        initializeMatrix(matrixB);

        // Define the number of threads to use
        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of threads: " + numThreads);

        // Divide the work among the threads using CompletableFuture
        long chunkSize = rowsA / numThreads;
        System.out.println("Chunk size: " + chunkSize);
        long start = System.nanoTime();
        System.out.println("Start time: " + start);
        CompletableFuture<?>[] futures = new CompletableFuture[numThreads];
        for (int i = 0; i < numThreads; i++) {
            long startRow = i * chunkSize;
            long endRow = (i == numThreads - 1) ? rowsA : (i + 1) * chunkSize;
            futures[i] = CompletableFuture.runAsync(
                    new MatrixMultiplicationTask(matrixA, matrixB, resultMatrix, startRow, endRow));
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
        // Code to print the matrix
        // ...
    }
}

class MatrixMultiplicationTask implements Runnable {
    private final int[][] matrixA;
    private final int[][] matrixB;
    private final int[][] resultMatrix;
    private final long startRow;
    private final long endRow;

    public MatrixMultiplicationTask(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, long startRow, long endRow) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.resultMatrix = resultMatrix;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    public void run() {
        // Perform matrix multiplication for the assigned rows
        for (long i = startRow; i < endRow; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    resultMatrix[(int) i][j] += matrixA[(int) i][k] * matrixB[k][j];
                }
            }
        }
    }
}
