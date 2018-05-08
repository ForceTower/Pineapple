package com.forcetower.uefs.util;

/**
 * Created by João Paulo on 30/04/2018.
 */
public class ObjectUtils {
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}