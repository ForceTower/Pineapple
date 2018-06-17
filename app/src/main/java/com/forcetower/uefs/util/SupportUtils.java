package com.forcetower.uefs.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.transition.Slide;
import android.support.v4.view.GravityCompat;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class SupportUtils {
    @RequiresApi(17)
    public static int getGravityCompat(@NonNull Context ctx, @Slide.GravityFlag int gravityEdge) {
        return GravityCompat.getAbsoluteGravity(gravityEdge, ctx.getResources().getConfiguration().getLayoutDirection());
    }
}
