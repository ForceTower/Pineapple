package com.forcetower.uefs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Pair;
import android.view.View;

import com.forcetower.uefs.anim.ChangeBoundsTransition;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public abstract class BaseNavigation {
    protected final FragmentManager manager;
    protected final int containerId;

    public BaseNavigation(FragmentActivity activity) {
        this.manager = activity.getSupportFragmentManager();
        this.containerId = R.id.container;
    }

    protected void switchFragment(Fragment fragment, Bundle arguments) {
        switchFragment(fragment, false, null, arguments, null);
    }

    protected void switchFragment(Fragment fragment, boolean stack, String name) {
        switchFragment(fragment, stack, name, null, null);
    }

    public void back() {
        manager.popBackStack();
    }

    public void backTo(String tag, boolean inclusive) {
        manager.popBackStack(tag, inclusive ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
    }

    protected void switchFragment(Fragment fragment) {
        switchFragment(fragment, false, null, null, null);
    }

    protected void switchFragment(@NonNull Fragment fragment, boolean stack, @Nullable String name,
                                  @Nullable Bundle args, @Nullable List<Pair<String, View>> shared) {
        switchFragment(fragment, stack, name, args, shared, false);
    }

    private void switchFragment(@NonNull Fragment fragment, boolean stack, String name,
                                @Nullable Bundle args, @Nullable List<Pair<String, View>> shared, boolean clearTop) {
        switchFragment(fragment, stack, name, args, shared, clearTop, null, false);
    }

    private void switchFragment(@NonNull Fragment fragment, boolean stack, String name,
                                @Nullable Bundle args, @Nullable List<Pair<String, View>> shared,
                                @Nullable String backName, boolean inclusive) {
        switchFragment(fragment, stack, name, args, shared, false, backName, inclusive);
    }

    private void switchFragment(@NonNull Fragment fragment, boolean stack, String name,
                                @Nullable Bundle args, @Nullable String backName, boolean inclusive) {
        switchFragment(fragment, stack, name, args, null, false, backName, inclusive);
    }

    protected void switchFragment(@NonNull Fragment fragment, boolean stack, @Nullable String name,
                                  @Nullable Bundle args, @Nullable List<Pair<String, View>> shared,
                                  boolean clearTop, @Nullable String backName, boolean inclusive) {
        if(args != null)
            fragment.setArguments(args);

        if(clearTop) {
            for(int i = manager.getBackStackEntryCount(); i > 1; i--) {
                manager.popBackStack();
            }
        }

        if(backName != null) {
            manager.popBackStack(backName, inclusive ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());

        if(stack) {
            if(name == null)
                throw new IllegalArgumentException("name can not be null");
            transaction.addToBackStack(name);
        }
        if(shared != null && shared.size() > 0) {
            fragment.setSharedElementEnterTransition(new ChangeBoundsTransition());
            fragment.setSharedElementReturnTransition(new ChangeBoundsTransition());

            for(Pair<String, View> element : shared)
                transaction.addSharedElement(element.second, element.first);
        }

        transaction.setReorderingAllowed(true);
        transaction.commit();
    }

    public void startActivity(@NonNull Context context, Class<? extends Activity> activity) {
        startActivity(context, activity, false);
    }


    /**
     * Clears all the back stack records, returning the navigation to the primary fragment.
     * Be careful with this usage
     *
     * @param all if true, should clear all the stack including the first one,
     *            if false, let's only one fragment in the stack
     */
    public void explicitClearTop(boolean all) {
        for(int i = manager.getBackStackEntryCount(); i > (all ? 0 : 1); i--) {
            manager.popBackStack();
        }
    }

    public void startActivity(@NonNull Context ctx, Class<? extends Activity> activity, boolean clearTop) {
        Intent intent = new Intent(ctx, activity);
        startActivity(ctx, intent, clearTop);
    }

    public void startActivity(@NonNull Context ctx, Intent intent, boolean clearTop) {
        if(clearTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        ctx.startActivity(intent);
    }
}