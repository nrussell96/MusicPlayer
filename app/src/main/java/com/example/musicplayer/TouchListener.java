package com.example.musicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Handles touch events in-app on the phone
 */

public class TouchListener implements RecyclerView.OnItemTouchListener {

    //intercept touch events
    GestureDetector gestureDetector;
    private onItemClickListener clickListener;

    public TouchListener(Context context, final onItemClickListener clickListener){
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }
        });
    }
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        //TODO
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if(child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onItemClick(child, recyclerView.getChildLayoutPosition(child));
        }
        return false;
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        //TODO
    }
}
