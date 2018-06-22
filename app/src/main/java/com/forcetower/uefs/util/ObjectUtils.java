package com.forcetower.uefs.util;

import java.util.Arrays;

/**
 * Created by João Paulo on 30/04/2018.
 */
public class ObjectUtils {
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }
}