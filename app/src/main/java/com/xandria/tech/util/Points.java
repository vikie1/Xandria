package com.xandria.tech.util;

public class Points {
    private static final double POINTS_PER_RUPEE = 1.00; // change this value if you want to change number of points for a rupee

    public static double rupeesToPoints(double rs){
        return rs * POINTS_PER_RUPEE;
    }

    public static double rupeesFromPoints(double pts){
        return pts / POINTS_PER_RUPEE;
    }
}
