/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria;

/**
 *
 * @author bmayr
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public final class ReciprocalArraySum {


    private ReciprocalArraySum() {
    }


    protected static double seqArraySum(final double[] input) {
        double sum = 0;

        for (int i = 0; i < input.length; i++) {
            sum += 1/input[i];
        }
       return sum;
    }
  


    private static class ReciprocalArraySumTask extends RecursiveAction {

        private final int startIndexInclusive;

        private final int endIndexExclusive;

        private final double[] input;

        private double value;
        
        private static int SEQUENTIAL_THRESHOLD = 50000;


        ReciprocalArraySumTask(final int setStartIndexInclusive,
                final int setEndIndexExclusive, final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
        }


        public double getValue() {
            return value;
        }

        @Override
        protected void compute() {

            int elemente = endIndexExclusive - startIndexInclusive;

            if(elemente < SEQUENTIAL_THRESHOLD){
                double[] arr = new double[elemente];
                for (int i = 0; i < elemente; i++) {
                    arr[i] = input[startIndexInclusive+i];
                }
                double sum = seqArraySum(arr);
                this.value = sum;
            }else{
                int mid = input.length/2;
                ReciprocalArraySumTask t1 = new ReciprocalArraySumTask(startIndexInclusive, mid,input);
                ReciprocalArraySumTask t2 = new ReciprocalArraySumTask(mid, endIndexExclusive,input);
                invokeAll(t1, t2);
                t1.join();
                t2.join();
                this.value = t1.getValue() + t2.getValue();

            }
            
        }
    }
  


    protected static double parManyTaskArraySum(final double[] input,
            final int numTasks) {
        ForkJoinPool pool = new ForkJoinPool(numTasks);
        ReciprocalArraySumTask task = new ReciprocalArraySumTask(0, input.length, input);
        pool.invoke(task);
        return task.getValue();
    }

    public static void main(String[] args){
        double[] input = new double[10];
        for (int i = 0; i < input.length; i++) {
            input[i] += i+1;
        }
        System.out.println("The reciprocalarry sum of the inputarray is: " + parManyTaskArraySum(input, 2));

    }
}

