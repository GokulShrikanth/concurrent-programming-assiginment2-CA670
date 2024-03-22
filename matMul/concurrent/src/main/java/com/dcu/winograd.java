package com.dcu;

public class winograd {
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

            // Recursive calls for matrix multiplication
            int[][] c11 = add(multiply(a11, b11), multiply(a12, b21));
            int[][] c12 = add(multiply(a11, b12), multiply(a12, b22));
            int[][] c21 = add(multiply(a21, b11), multiply(a22, b21));
            int[][] c22 = add(multiply(a21, b12), multiply(a22, b22));

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

    private static int[][] add(int[][] matrix1, int[][] matrix2) {
        int n = matrix1.length;
        int[][] result = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }

        return result;
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
        int [][] A ={ {1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16} };
        int [][] B ={ {1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16} };
        StrassenMatrixMultiplication.printMatrix(A);
        StrassenMatrixMultiplication.printMatrix(B);
        long startTime = System.currentTimeMillis();

        int[][] result = multiply(A, B);
        int[][] result1 = StrassenMatrixMultiplication.multiplyNaive(A, B);
        if(StrassenMatrixMultiplication.equals(result, result1))
            System.out.println("Both results are same");
        else
            System.out.println("Both results are different");
        long endTime = System.currentTimeMillis();

        long totalTime = endTime - startTime;
        System.out.println("Start Time: " + startTime);
        System.out.println("End Time: " + endTime);
        System.out.println("Total Time: " + totalTime + " milliseconds"); // Print the result
        System.out.println("Winograd Multiplication");
        StrassenMatrixMultiplication.printMatrix(result);
        System.out.println("Naive Multiplication");
        StrassenMatrixMultiplication.printMatrix(result1);
    }
}
