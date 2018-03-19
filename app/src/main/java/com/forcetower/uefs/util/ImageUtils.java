package com.forcetower.uefs.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by João Paulo on 05/03/2018.
 */

public class ImageUtils {

    @TargetApi(17)
    public static Bitmap blurImage(Context context, Bitmap originalBitmap, int radius) {
        Bitmap blurredBitmap = Bitmap.createBitmap(originalBitmap);

        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs, originalBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4 (rs));
        script.setInput(input);
        script.setRadius(radius);
        script.forEach(output);
        output.copyTo(blurredBitmap);
        return blurredBitmap;
    }

    public static String encode(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
