package com.bboyairwreck.swipecard;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by eric on 1/3/16.
 */
public class CardAnimationListener implements Animation.AnimationListener {
    View v;
    ViewGroup _root;
    ImageButton btnNope;
    ImageButton btnYeah;
    Runnable onAnimationEndRunnable;

    public CardAnimationListener(View v, ViewGroup _root, ImageButton btnYeah, ImageButton btnNope, Runnable onAnimationEndRunnable) {
        this.v = v;
        this._root = _root;
        this.btnYeah = btnYeah;
        this.btnNope = btnNope;
        this.onAnimationEndRunnable = onAnimationEndRunnable;
    }

    /*
     * Fired when Animation finishes
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        Log.i("onAnimationEnd", "Animation ended. Removing card");
        v.setOnTouchListener(null);     // unregister onTouchListener



        // Can only remove a view in Runnable UI Thread in AnimationListener
        _root.post(onAnimationEndRunnable);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        btnNope.setEnabled(false);
        btnYeah.setEnabled(false);
    }
    @Override
    public void onAnimationRepeat(Animation animation) {}
}
