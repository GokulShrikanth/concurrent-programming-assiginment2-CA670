#define _CRT_SECURE_NO_WARNINGS
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <omp.h>
#include <math.h>

#define THREADS 8

using namespace std;

// Default sample size is 1, unless the user specifies a value
int samples = 1;

// MatrixA and MatrixB will be the input matrices - maximum size 2000
double matrixA[2000][2000];
double matrixB[2000][2000];
double transposeB[2000][2000];

// This matrix will be the multiplication of Matrix A and B
double matrixC[2000][2000];

// Method header initialization
int min(int a, int b);
void transpose(int n);
void populateMatrix(int n);
double serialMultiplication(int n);
double parallelForMultiplication(int n);
double parallelForMultiplicationOptimized(int n);

void step4_1();
void step4_2();
void step8();

/*
This is the main method of the program
*/
int main()
{
    char method;

    // Enter method that needs to be run
    printf("Enter one of the options given below\n \ts - for serial program \n\tp - for parallel program \n\to - for optimized program\n");
    scanf("%c", &method);

    // Enter sample size for the above selected method
    printf("Enter sample size: ");
    scanf("%d", &samples);

    switch (method)
    {
    case 's':
        step4_1();
        break;

    case 'p':
        step4_2();
        break;

    case 'o':
        step8();
        break;
    }

    return 0;
}

/*
This method will return the minimum out of two numbers (integers)
*/
int min(int a, int b)
{
    return a < b ? a : b;
}

/*
This method will get the transpose of matrixB
*/
void transpose(int n)
{
    int i, j;
    for (i = 0; i < n; i++)
    {
        for (j = 0; j < n; j++)
        {
            transposeB[i][j] = matrixB[j][i];
        }
    }
}

/*
This method will populate the matrix with random numbers
*/
void populateMatrix(int n)
{
    // Different seed for each experiment
    srand(time(NULL));

    // Populate matrices
    for (int i = 0; i < n; i++)
    {
        for (int j = 0; j < n; j++)
        {
            matrixA[i][j] = rand();
            matrixB[i][j] = rand();
        }
    }
}

/*
This method will do serial multiplication
*/
double serialMultiplication(int n)
{
    // Required variable initialization
    int i, j, k;

    // Start time from the wall clock
    double startTime = omp_get_wtime();

    for (i = 0; i < n; i++)
    {
        for (j = 0; j < n; j++)
        {
            matrixC[i][j] = 0;
            for (k = 0; k < n; k++)
            {
                matrixC[i][j] = matrixC[i][j] + matrixA[i][k] * matrixB[k][j];
            }
        }
    }

    // Get the end time from the wall clock
    double endTime = omp_get_wtime();

    // Return the execution time
    return endTime - startTime;
}

/*
This method will do parallel for loop multiplication
*/
double parallelForMultiplication(int n)
{
    // Start time from the wall clock
    double startTime = omp_get_wtime();

// For loop specification with shared, private variables and number of threads
#pragma omp parallel for shared(matrixA, matrixB, matrixC) schedule(static) num_threads(THREADS)
    for (int i = 0; i < n; i++)
    {
        for (int j = 0; j < n; j++)
        {
            matrixC[i][j] = 0;
            for (int k = 0; k < n; k++)
            {
                matrixC[i][j] = matrixC[i][j] + matrixA[i][k] * matrixB[k][j];
            }
        }
    }

    // Get the end time from wall clock
    double endTime = omp_get_wtime();

    // Return the execution time
    return endTime - startTime;
}

/*
This method will do parallel for loop multiplication with optimization using block algorithms
*/
double parallelForMultiplicationOptimized(int n)
{
    int i, j, k;

    // Get the transpose of matrixB
    transpose(n);

    // Start time from the wall clock
    double startTime = omp_get_wtime();

// Calculate using transpose
#pragma omp parallel for shared(matrixA, matrixB, matrixC) private(i, j, k) schedule(static) num_threads(THREADS)
    for (i = 0; i < n; i++)
    {
        for (j = 0; j < n; j++)
        {
            double sum = 0;
            for (k = 0; k < n; k++)
            {
                sum += matrixA[i][k] * transposeB[j][k];
            }
            matrixC[i][j] = sum;
        }
    }

    // Get the end time from wall clock
    double endTime = omp_get_wtime();

    // Return the execution time
    return (endTime - startTime);
}

/*
This method will calculate the times taken to multiply matrices of different sizes using serial code
*/
void step4_1()
{
    for (int n = 200; n <= 2000; n = n + 200)
    {
        printf("\nStarting for n = %d\n\n", n);

        double sum = 0;

        for (int i = 0; i < samples; i++)
        {
            populateMatrix(n);
            sum += serialMultiplication(n);
        }

        double average = sum / samples;

        printf("Serial: Time taken for a %d x %d matrix is %f s\n", n, n, average);
    }
}

/*
This method will calculate the times taken to multiply matrices of different sizes using parallel code
*/
void step4_2()
{
    for (int n = 200; n <= 2000; n = n + 200)
    {
        printf("\nStarting for n = %d\n\n", n);

        double sum = 0;

        for (int i = 0; i < samples; i++)
        {
            populateMatrix(n);
            sum += parallelForMultiplication(n);
        }

        double average = sum / samples;

        printf("Parallel: Time taken for a %d x %d matrix is %f s\n", n, n, average);
    }
}

/*
This method will calculate the times taken to multiply matrices of different sizes using optimized code
*/
void step8()
{
    for (int n = 200; n <= 2000; n = n + 200)
    {
        printf("\nStarting for n = %d\n\n", n);

        double sum = 0;

        for (int i = 0; i < samples; i++)
        {
            populateMatrix(n);
            sum += parallelForMultiplicationOptimized(n);
        }

        double average = sum / samples;

        printf("Optimized: Time taken for a %d x %d matrix is %f s\n", n, n, average);
    }
}
