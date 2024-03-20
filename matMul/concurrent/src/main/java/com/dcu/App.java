package com.dcu;

import java.sql.Date;
import java.sql.Time;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        // Define the size of the matrices
        int rowsA = 10000;
        int colsA = 10000;
        int rowsB = 10000;
        int colsB = 10000;

        // Create the matrices
        int[][] matrixA = new int[rowsA][colsA];
        int[][] matrixB = new int[rowsB][colsB];
        int[][] resultMatrix = new int[rowsA][colsB];

        // Initialize the matrices with random values
        initializeMatrix(matrixA);
        initializeMatrix(matrixB);

        // Define the number of threads to use
        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of threads: " + numThreads);

        // Create a thread pool with the specified number of threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Divide the work among the threads
        int chunkSize = rowsA / numThreads;
        System.out.println("Chunk size: " + chunkSize);
        Time start = new Time(System.currentTimeMillis());
        System.out.println("Start time: " + start);
        for (int i = 0; i < numThreads; i++) {
            int startRow = i * chunkSize;
            int endRow = (i == numThreads - 1) ? rowsA : (i + 1) * chunkSize;
            executor.execute(new MatrixMultiplicationTask(matrixA, matrixB, resultMatrix, startRow, endRow));
        }

        // Shutdown the executor and wait for all tasks to complete
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally{
            Time end = new Time(System.currentTimeMillis());
            System.out.println("End time: " + end);
            System.out.println("Time taken: " + (end.getTime() - start.getTime()));
        }

        // Print the result matrix
        //printMatrix(resultMatrix);
    }

    private static void initializeMatrix(int[][] matrix) {
        Random random = new Random();
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
    private final int startRow;
    private final int endRow;

    public MatrixMultiplicationTask(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int startRow, int endRow) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.resultMatrix = resultMatrix;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    public void run() {
        // Perform matrix multiplication for the assigned rows
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
    }
}
