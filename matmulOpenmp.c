#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

#define SIZE 300

void generateRandomMatrix(int matrix[SIZE][SIZE]) {
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            matrix[i][j] = rand() % 100;
        }
    }
}

void multiplyMatrices(int matrixA[SIZE][SIZE], int matrixB[SIZE][SIZE], int result[SIZE][SIZE]) {
    #pragma omp parallel for
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            result[i][j] = 0;
            for (int k = 0; k < SIZE; k++) {
                result[i][j] += matrixA[i][k] * matrixB[k][j];
            }
        }
    }
}

void multiplyMatricesNaive(int matrixA[SIZE][SIZE], int matrixB[SIZE][SIZE], int result[SIZE][SIZE]) {
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            result[i][j] = 0;
            for (int k = 0; k < SIZE; k++) {
                result[i][j] += matrixA[i][k] * matrixB[k][j];
            }
        }
    }
}

void print(int result[SIZE][SIZE])
{
    for (int i = 0; i < SIZE; i++)
    {
        for (int j = 0; j < SIZE; j++)
        {
            printf("%d ", result[i][j]);
        }
        printf("\n");
    }
}

int main() {
    int matrixA[SIZE][SIZE];
    int matrixB[SIZE][SIZE];
    int result[SIZE][SIZE];
    int resultNaive[SIZE][SIZE];

    // Generate random matrices
    generateRandomMatrix(matrixA);
    generateRandomMatrix(matrixB);

    // Multiply matrices
    multiplyMatrices(matrixA, matrixB, result);
    multiplyMatricesNaive(matrixA, matrixB, result);

    // check if the results are the same
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            if (result[i][j] != resultNaive[i][j]) {
                printf("Results are not the same\n");
                return 1;
            }
        }
    }
    return 0;
}
