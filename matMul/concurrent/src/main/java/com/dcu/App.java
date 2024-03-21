package com.dcu;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        // Define the size of the matrices
        int size = 5000;

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
        for (int i = 0; i < numThreads; i++) {
            futures[i] = CompletableFuture.runAsync(
                    new StrassenMultiplicationTask(matrixA, matrixB, resultMatrix, size, 0, 0, 0, 0, 0));
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

class StrassenMultiplicationTask implements Runnable {
    private final int[][] matrixA;
    private final int[][] matrixB;
    private final int[][] resultMatrix;
    private final int size;
    private final int startRowA;
    private final int startColA;
    private final int startRowB;
    private final int startColB;
    private final int startRowC;

    public StrassenMultiplicationTask(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int size,
                                      int startRowA, int startColA, int startRowB, int startColB, int startRowC) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.resultMatrix = resultMatrix;
        this.size = size;
        this.startRowA = startRowA;
        this.startColA = startColA;
        this.startRowB = startRowB;
        this.startColB = startColB;
        this.startRowC = startRowC;
    }

    @Override
    public void run() {
        strassenMultiply(matrixA, matrixB, resultMatrix, size, startRowA, startColA, startRowB, startColB, startRowC);
    }

    private void strassenMultiply(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int size,
                                  int startRowA, int startColA, int startRowB, int startColB, int startRowC) {
        if (size <= 128) {
            // Base case: Use regular matrix multiplication
            regularMultiply(matrixA, matrixB, resultMatrix, size, startRowA, startColA, startRowB, startColB, startRowC);
        } else {
            int newSize = size / 2;

            // Divide matrixA into quadrants
            int[][] a11 = new int[newSize][newSize];
            int[][] a12 = new int[newSize][newSize];
            int[][] a21 = new int[newSize][newSize];
            int[][] a22 = new int[newSize][newSize];
            divideMatrix(matrixA, a11, a12, a21, a22, startRowA, startColA);

            // Divide matrixB into quadrants
            int[][] b11 = new int[newSize][newSize];
            int[][] b12 = new int[newSize][newSize];
            int[][] b21 = new int[newSize][newSize];
            int[][] b22 = new int[newSize][newSize];
            divideMatrix(matrixB, b11, b12, b21, b22, startRowB, startColB);

            // Calculate the 7 intermediate matrices
            int[][] m1 = new int[newSize][newSize];
            int[][] m2 = new int[newSize][newSize];
            int[][] m3 = new int[newSize][newSize];
            int[][] m4 = new int[newSize][newSize];
            int[][] m5 = new int[newSize][newSize];
            int[][] m6 = new int[newSize][newSize];
            int[][] m7 = new int[newSize][newSize];

            strassenMultiply(addMatrices(a11, a22, newSize), addMatrices(b11, b22, newSize), m1, newSize, 0, 0, 0, 0, 0);
            strassenMultiply(addMatrices(a21, a22, newSize), b11, m2, newSize, 0, 0, 0, 0, 0);
            strassenMultiply(a11, subtractMatrices(b12, b22, newSize), m3, newSize, 0, 0, 0, 0, 0);
            strassenMultiply(a22, subtractMatrices(b21, b11, newSize), m4, newSize, 0, 0, 0, 0, 0);
            strassenMultiply(addMatrices(a11, a12, newSize), b22, m5, newSize, 0, 0, 0, 0, 0);
            strassenMultiply(subtractMatrices(a21, a11, newSize), addMatrices(b11, b12, newSize), m6, newSize, 0, 0, 0, 0, 0);
            strassenMultiply(subtractMatrices(a12, a22, newSize), addMatrices(b21, b22, newSize), m7, newSize, 0, 0, 0, 0, 0);

            // Calculate the result matrix quadrants
            int[][] c11 = addMatrices(subtractMatrices(addMatrices(m1, m4, newSize), m5, newSize), m7, newSize);
            int[][] c12 = addMatrices(m3, m5, newSize);
            int[][] c21 = addMatrices(m2, m4, newSize);
            int[][] c22 = addMatrices(subtractMatrices(addMatrices(m1, m3, newSize), m2, newSize), m6, newSize);

            // Combine the result matrix quadrants
            combineMatrix(resultMatrix, c11, c12, c21, c22, startRowC);
        }
    }

    private void regularMultiply(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int size,
                                 int startRowA, int startColA, int startRowB, int startColB, int startRowC) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    resultMatrix[startRowC + i][startColB + j] += matrixA[startRowA + i][startColA + k] * matrixB[startRowB + k][startColB + j];
                }
            }
        }
    }

    private void divideMatrix(int[][] matrix, int[][] topLeft, int[][] topRight, int[][] bottomLeft, int[][] bottomRight,
                              int startRow, int startCol) {
        int newSize = matrix.length / 2;
        for (int i = 0; i < newSize; i++) {
            for (int j = 0; j < newSize; j++) {
                topLeft[i][j] = matrix[startRow + i][startCol + j];
                topRight[i][j] = matrix[startRow + i][startCol + j + newSize];
                bottomLeft[i][j] = matrix[startRow + i + newSize][startCol + j];
                bottomRight[i][j] = matrix[startRow + i + newSize][startCol + j + newSize];
            }
        }
    }

    private int[][] addMatrices(int[][] matrixA, int[][] matrixB, int size) {
        int[][] result = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }
        return result;
    }

    private int[][] subtractMatrices(int[][] matrixA, int[][] matrixB, int size) {
        int[][] result = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result[i][j] = matrixA[i][j] - matrixB[i][j];
            }
        }
        return result;
    }

    private void combineMatrix(int[][] matrix, int[][] topLeft, int[][] topRight, int[][] bottomLeft, int[][] bottomRight,
                               int startRow) {
        int newSize = matrix.length / 2;
        for (int i = 0; i < newSize; i++) {
            for (int j = 0; j < newSize; j++) {
                matrix[startRow + i][j] = topLeft[i][j];
                matrix[startRow + i][j + newSize] = topRight[i][j];
                matrix[startRow + i + newSize][j] = bottomLeft[i][j];
                matrix[startRow + i + newSize][j + newSize] = bottomRight[i][j];
            }
        }
    }
}
