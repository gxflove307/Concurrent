
#include <iostream>
#include <omp.h>
#include <time.h>
#include "ctime"
#include "chrono"
#include "random"
#include <stdlib.h>

using namespace std;
using namespace std::chrono;


#define M 6000
#define N 6000
#define ThreadNumber 8

double matrix_a[M][N], matrix_b[N][M], result[M][M];//init matrix

//random the value of matrix
void init_matrix() {
    default_random_engine engine;
    uniform_real_distribution<double> u(0.0, 1.0);
    for (int i = 0; i < M; ++i) {
        for (int j = 0; j < N; ++j) {
            matrix_a[i][j] = u(engine);
        }
    }
    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < M; ++j) {
            matrix_b[i][j] = u(engine);
        }
    }
}
//small matrix computing
void smallMatrixMult (int upperOfRow , int bottomOfRow ,
                      int leftOfCol , int rightOfCol ,
                      int transLeft ,int transRight )
{
    #pragma omp parallel for num_threads(ThreadNumber)
    for(int row = upperOfRow ; row <= bottomOfRow ; row++){
        for(int col = leftOfCol ; col < rightOfCol ; col++){
            for(int trans = transLeft ; trans <= transRight ; trans++){
               result [row] [col] += matrix_a [row] [trans] * matrix_b [trans] [col] ;
            }
        }
    }
    //#pragma omp barrier
}

//partition matrix
void matrixMulti(int upperOfRow , int bottomOfRow ,
                 int leftOfCol , int rightOfCol ,
                 int transLeft ,int transRight )
{
    if ( ( bottomOfRow - upperOfRow ) < 600 )
        smallMatrixMult ( upperOfRow , bottomOfRow ,
                          leftOfCol , rightOfCol ,
                          transLeft , transRight );

    else
    {
        #pragma omp task
        {
            matrixMulti( upperOfRow , ( upperOfRow + bottomOfRow ) / 2 ,
                         leftOfCol , ( leftOfCol + rightOfCol ) / 2 ,
                         transLeft , ( transLeft + transRight ) / 2 );
            matrixMulti( upperOfRow , ( upperOfRow + bottomOfRow ) / 2 ,
                         leftOfCol , ( leftOfCol + rightOfCol ) / 2 ,
                         ( transLeft + transRight ) / 2 + 1 , transRight );
        }

        #pragma omp task
        {
            matrixMulti( upperOfRow , ( upperOfRow + bottomOfRow ) / 2 ,
                         ( leftOfCol + rightOfCol ) / 2 + 1 , rightOfCol ,
                         transLeft , ( transLeft + transRight ) / 2 );
            matrixMulti( upperOfRow , ( upperOfRow + bottomOfRow ) / 2 ,
                         ( leftOfCol + rightOfCol ) / 2 + 1 , rightOfCol ,
                         ( transLeft + transRight ) / 2 + 1 , transRight );
        }


        #pragma omp task
        {
            matrixMulti( ( upperOfRow + bottomOfRow ) / 2 + 1 , bottomOfRow ,
                         leftOfCol , ( leftOfCol + rightOfCol ) / 2 ,
                         transLeft , ( transLeft + transRight ) / 2 );
            matrixMulti( ( upperOfRow + bottomOfRow ) / 2 + 1 , bottomOfRow ,
                         leftOfCol , ( leftOfCol + rightOfCol ) / 2 ,
                         ( transLeft + transRight ) / 2 + 1 , transRight );
        }

        #pragma omp task
        {
            matrixMulti( ( upperOfRow + bottomOfRow ) / 2 + 1 , bottomOfRow ,
                         ( leftOfCol + rightOfCol ) / 2 + 1 , rightOfCol ,
                         transLeft , ( transLeft + transRight ) / 2 );
            matrixMulti( ( upperOfRow + bottomOfRow ) / 2 + 1 , bottomOfRow ,
                         ( leftOfCol + rightOfCol ) / 2 + 1 , rightOfCol ,
                         ( transLeft + transRight ) / 2 + 1 , transRight );
        }

        #pragma omp taskwait
    }

}
//serial matrix computing as control group
void serial_matrixMulti() {
    for (int i = 0; i < M; ++i) {
        for (int j = 0; j < M; ++j) {
            double temp = 0;
            for (int k = 0; k < N; ++k) {
                temp += matrix_a[i][k] * matrix_b[k][j];
            }
            result[i][j] = temp;
        }
    }
}

int main()
{
    init_matrix();
    auto start = system_clock::now();
    matrixMulti( 0 , M - 1 , 0 , N -1 , 0 , M -1 );
    auto end = system_clock::now();
    auto duration = duration_cast<microseconds>(end - start);
    cout << "parallel multiplication takes "
         << double(duration.count()) * microseconds::period::num / microseconds::period::den << " seconds" << endl;
    start = system_clock::now();
    serial_matrixMulti();
    end = system_clock::now();
    duration = duration_cast<microseconds>(end - start);
    cout << "Serial multiplication takes "
         << double(duration.count()) * microseconds::period::num / microseconds::period::den << " seconds" << endl;


    return 0;
}
