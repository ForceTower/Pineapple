package com.forcetower.uefs.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import androidx.annotation.IntRange;
import androidx.annotation.RequiresApi;
import android.util.Base64;

import com.forcetower.uefs.AppExecutors;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import timber.log.Timber;

/**
 * Created by João Paulo on 05/03/2018.
 */

public class ImageUtils {

    @RequiresApi(17)
    public static Bitmap blurImage(Context context, Bitmap originalBitmap, @IntRange(from = 0, to = 25) int radius) {
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

    @RequiresApi(17)
    public static LiveData<Bitmap> blurImageAsync(Context context, Bitmap originalBitmap, @IntRange(from = 0, to = 25) int radius, AppExecutors executors) {
        MutableLiveData<Bitmap> bitmapSrc = new MutableLiveData<>();
        executors.others().execute(() -> {
            Bitmap bitmap = blurImage(context, originalBitmap, radius);
            bitmapSrc.postValue(bitmap);
        });
        return bitmapSrc;
    }

    public static String encode(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static LiveData<String> encodeImage(Bitmap bitmap, AppExecutors executors) {
        MutableLiveData<String> encoderData = new MutableLiveData<>();
        executors.diskIO().execute(() -> {
            int height = bitmap.getHeight();
            int width  = bitmap.getWidth();

            int proportion = height/512;
            if (proportion == 0) proportion = 1;
            Bitmap save = Bitmap.createScaledBitmap(bitmap, width/proportion, height/proportion, false);
            String encodedImage = encode(save);
            encoderData.postValue(encodedImage);
        });
        return encoderData;
    }

    public static LiveData<Bitmap> getImageBitmap(File file, AppExecutors executors) {
        MutableLiveData<Bitmap> data = new MediatorLiveData<>();
        executors.diskIO().execute(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                data.postValue(bitmap);
            }
            catch (FileNotFoundException e) {
                Timber.d("File doesn't exists");
                data.postValue(null);
            }
        });
        return data;
    }
}
