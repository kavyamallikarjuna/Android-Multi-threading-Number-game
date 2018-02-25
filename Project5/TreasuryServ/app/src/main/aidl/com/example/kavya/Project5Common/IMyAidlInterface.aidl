// IMyAidlInterface.aidl
package com.example.kavya.Project5Common;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int[] monthlyCash(int year);
    int[] dailyCash(int day, int month,int year, int wDays);
    int yearlyAvg(int year);
    String status(String x);
}
