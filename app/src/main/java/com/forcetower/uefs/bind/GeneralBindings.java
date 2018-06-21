package com.forcetower.uefs.bind;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static com.forcetower.uefs.Constants.DEFAULT_COURSE_IMAGE;

/**
 * Created by João Paulo on 15/06/2018.
 */
public class GeneralBindings {
    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        Picasso.with(imageView.getContext())
                .load(url != null ? url : DEFAULT_COURSE_IMAGE)
                .into(imageView);
    }
}
