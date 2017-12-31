package com.forcetower.uefs.view.class_details;

import android.support.v7.widget.RecyclerView;

import com.forcetower.uefs.database.entities.ATodoItem;

/**
 * Created by João Paulo on 30/12/2017.
 */

public interface ClassDetailsCallback {
    void onScrolled(RecyclerView recyclerView, int dx, int dy);
    void onTodoItemUpdated(ATodoItem item, int position);
    void onTodoItemDeleted(ATodoItem item, int position);
}
