package com.xandria.tech.util;

public class Points {
    private static final int POINTS_PER_RUPEE = 10;

    public static Double rupeesToPoints(Double rs){
        return rs * POINTS_PER_RUPEE;
    }

    public static Double rupeesFromPoints(Double pts){
        return pts / POINTS_PER_RUPEE;
    }
}
