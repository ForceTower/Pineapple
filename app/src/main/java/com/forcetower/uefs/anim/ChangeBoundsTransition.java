package com.forcetower.uefs.anim;

import android.annotation.TargetApi;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

@TargetApi(21)
public class ChangeBoundsTransition extends TransitionSet {
    public ChangeBoundsTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}