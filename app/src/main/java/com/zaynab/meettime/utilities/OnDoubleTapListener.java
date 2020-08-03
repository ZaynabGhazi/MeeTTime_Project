package com.zaynab.meettime.utilities;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/*
 * Copied from  a codePath guide
 */
public class OnDoubleTapListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    public OnDoubleTapListener(Context c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            OnDoubleTapListener.this.onDoubleTap(e);
            return super.onDoubleTap(e);
        }
    }

    public void onDoubleTap(MotionEvent e) {
    }
}