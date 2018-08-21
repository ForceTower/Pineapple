package com.forcetower.uefs.view;

import androidx.recyclerview.widget.DiffUtil;

import java.lang.reflect.Field;
import java.util.List;

import timber.log.Timber;

/**
 * Created by João Paulo on 23/06/2018.
 * Don't use it in a large set of items.
 * It uses reflection to find uid field :)
 */
public class BaseDiffCallback<T> extends DiffUtil.Callback {
    private final List<T> oldItems;
    private final List<T> newItems;

    public BaseDiffCallback(List<T> oldItems, List<T> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T oldVal = oldItems.get(oldItemPosition);
        T newVal = newItems.get(newItemPosition);

        try {
            Field oldField = oldVal.getClass().getDeclaredField("uid");
            oldField.setAccessible(true);
            Object oldO = oldField.get(oldVal);

            Field newField = newVal.getClass().getDeclaredField("uid");
            newField.setAccessible(true);
            Object newO = newField.get(oldVal);
            Timber.d("Reflection did it: " + oldO + " :: " + newO);
            return oldO.equals(newO);
        } catch (Exception e) {
            return oldVal.equals(newVal);
        }
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }
}
