package com.forcetower.uefs.anim;

import android.annotation.TargetApi;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

@TargetApi(21)
public class ChangeBoundsTransition extends TransitionSet {

    public ChangeBoundsTransition() {
        setOrdering(ORDERING_TOGETHER);
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setPathMotion(new ArcMotion());
        addTransition(changeBounds);
        addTransition(new ChangeTransform());
        addTransition(new ChangeClipBounds());
        addTransition(new ChangeImageTransform());
    }
}