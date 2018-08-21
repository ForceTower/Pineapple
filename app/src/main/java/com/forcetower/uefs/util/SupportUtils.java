package com.forcetower.uefs.util;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.transition.Slide;
import androidx.core.view.GravityCompat;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class SupportUtils {
    @RequiresApi(17)
    public static int getGravityCompat(@NonNull Context ctx, @Slide.GravityFlag int gravityEdge) {
        return GravityCompat.getAbsoluteGravity(gravityEdge, ctx.getResources().getConfiguration().getLayoutDirection());
    }
}
