package com.forcetower.uefs.sdk;

/**
 * Created by João Paulo on 17/11/2017.
 */

class SagresMethods {
    private static SagresMethods instance;

    public static void instantiate() {
        if (instance == null) {
            synchronized (SagresMethods.class) {
                if (instance == null)
                    instance = new SagresMethods();
            }
        }
    }
}
