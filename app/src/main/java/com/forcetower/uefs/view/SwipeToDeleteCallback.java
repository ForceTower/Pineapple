package com.forcetower.uefs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.R;

/**
 * Created by João Paulo on 22/06/2018.
 */
public abstract class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final ColorDrawable background;
    private final Drawable deleteIcon;
    private final int intrinsicWidth;
    private final int intrinsicHeight;
    
    public SwipeToDeleteCallback(Context context) {
        super(0, ItemTouchHelper.LEFT);
        this.background = new ColorDrawable();
        this.deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24dp);
        if (deleteIcon != null) {
            this.intrinsicWidth = deleteIcon.getIntrinsicWidth();
            this.intrinsicHeight = deleteIcon.getIntrinsicHeight();
        } else {
            this.intrinsicWidth = 24;
            this.intrinsicHeight = 24;
            Crashlytics.logException(new Exception("Delete Icon returned null"));
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();

        background.setColor(Color.RED);
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;

        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteIcon.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }
}
