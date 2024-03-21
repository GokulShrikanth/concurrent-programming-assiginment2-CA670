package com.dcu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class coppersmithWinograd {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    public static int[][] multiply(int[][] matrixA, int[][] matrixB) {
        int numRowsA = matrixA.length;
        int numColsA = matrixA[0].length;
        int numRowsB = matrixB.length;
        int numColsB = matrixB[0].length;

        if (numColsA != numRowsB) {
            throw new IllegalArgumentException("Invalid matrix dimensions");
        }

        int[][] result = new int[numRowsA][numColsB];

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // Apply Coppersmith-Winograd algorithm
        int blockSize = 2;
        int paddedSizeA = numRowsA + (blockSize - numRowsA % blockSize) % blockSize;
        int paddedSizeB = numColsB + (blockSize - numColsB % blockSize) % blockSize;
        int[][] paddedMatrixA = padMatrix(matrixA, paddedSizeA, paddedSizeB);
        int[][] paddedMatrixB = padMatrix(matrixB, paddedSizeA, paddedSizeB);
        int[][] paddedResult = new int[paddedSizeA][paddedSizeB];

        for (int i = 0; i < paddedSizeA; i += blockSize) {
            for (int j = 0; j < paddedSizeB; j += blockSize) {
                for (int k = 0; k < paddedSizeA; k += blockSize) {
                    executor.execute(new MultiplyTask(paddedMatrixA, paddedMatrixB, paddedResult, i, j, k, blockSize));
                }
            }
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Copy the result to the original size matrix
        for (int i = 0; i < numRowsA; i++) {
            for (int j = 0; j < numColsB; j++) {
                result[i][j] = paddedResult[i][j];
            }
        }

        return result;
    }

    private static int[][] padMatrix(int[][] matrix, int paddedSizeA, int paddedSizeB) {
        int[][] paddedMatrix = new int[paddedSizeA][paddedSizeB];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                paddedMatrix[i][j] = matrix[i][j];
            }
        }
        return paddedMatrix;
    }

    private static class MultiplyTask implements Runnable {
        private final int[][] matrixA;
        private final int[][] matrixB;
        private final int[][] result;
        private final int row;
        private final int col;
        private final int start;
        private final int blockSize;

        public MultiplyTask(int[][] matrixA, int[][] matrixB, int[][] result, int row, int col, int start, int blockSize) {
            this.matrixA = matrixA;
            this.matrixB = matrixB;
            this.result = result;
            this.row = row;
            this.col = col;
            this.start = start;
            this.blockSize = blockSize;
        }

        @Override
        public void run() {
            int sum;
            for (int i = row; i < row + blockSize; i++) {
                for (int j = col; j < col + blockSize; j++) {
                    sum = 0;
                    for (int k = start; k < start + blockSize; k++) {
                        sum += matrixA[i][k] * matrixB[k][j];
                    }
                    result[i][j] += sum;
                }
            }
        }
    }
}
