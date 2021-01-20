package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    /*
          Average single thread time, sec: 1,509
          Average concurrent thread time, sec: 1,465

          1 opt
          Average single thread time, sec: 1,523
          Average concurrent thread time, sec: 0,486

           Multithreading
           Average single thread time, sec: 0,284
           Average concurrent thread time, sec: 0,051
        * */

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int[][] matrixBTransp = new int[matrixSize][matrixSize];
        for(int i = 0; i < matrixSize; i++) {
            for(int j = 0; j < matrixSize; j++) {
                matrixBTransp[j][i] = matrixB[i][j];
            }
        }

        final CompletionService<CalcOneRowResultWrapper> completionService = new ExecutorCompletionService<>(executor);

        List<Future<CalcOneRowResultWrapper>> futureTasks = new ArrayList<>();

        for (int i = 0; i < matrixSize; i++) {
            int finalI = i;
            futureTasks.add(completionService.submit(()-> calcForOneRowAndWrap(matrixA, matrixBTransp, finalI)));
        }

        while (!futureTasks.isEmpty()) {
            Future<CalcOneRowResultWrapper> future = completionService.poll(1, TimeUnit.SECONDS);
            CalcOneRowResultWrapper wrapper = future.get();
            matrixC[wrapper.getRowNum()] = wrapper.getResult();
            futureTasks.remove(future);
        }

        return matrixC;
    }


    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int[][] matrixBTransp = new int[matrixSize][matrixSize];
        for(int i = 0; i < matrixSize; i++) {
            for(int j = 0; j < matrixSize; j++) {
                matrixBTransp[j][i] = matrixB[i][j];
            }
        }

        for (int i = 0; i < matrixSize; i++) {
            matrixC[i]  = calcForOneRow(matrixA, matrixBTransp, i);
        }
        return matrixC;
    }

    private static int[] calcForOneRow(int[][] matrixA, int[][] matrixBT, int rowNumA) {
        final int matrixSize = matrixA.length;
        final int[] calculatedRow = new int[matrixSize];

        for (int j = 0; j < matrixSize; j++) {
            int sum = 0;
            for (int k = 0; k < matrixSize; k++) {
                sum += matrixA[rowNumA][k] * matrixBT[j][k];
            }
            calculatedRow[j] = sum;
        }

        return calculatedRow;
    }

    private static class CalcOneRowResultWrapper {
        private final int rowNum;
        private final int[] result;

        public CalcOneRowResultWrapper(int rowNum, int[] result) {
            this.rowNum = rowNum;
            this.result = result;
        }

        public int getRowNum() {
            return rowNum;
        }

        public int[] getResult() {
            return result;
        }
    }

    private static CalcOneRowResultWrapper calcForOneRowAndWrap(int[][] matrixA, int[][] matrixBT, int rowNumA) {
        return new CalcOneRowResultWrapper(rowNumA, calcForOneRow(matrixA, matrixBT, rowNumA));
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
