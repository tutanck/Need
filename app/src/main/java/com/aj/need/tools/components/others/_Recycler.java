package com.aj.need.tools.components.others;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by joan on 03/10/2017.
 */

public class _Recycler {

    public static class ItemTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public ItemTouchListener(Context context, final RecyclerView rv, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null)
                        clicklistener.onLongClick
                                (rv.getChildViewHolder(child), rv.getChildAdapterPosition(child));
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e))
                clicklistener.onClick(rv.getChildViewHolder(child), rv.getChildAdapterPosition(child));
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }


    public static interface ClickListener {
        public void onClick(RecyclerView.ViewHolder viewHolder, int position);

        public void onLongClick(RecyclerView.ViewHolder viewHolder, int position);
    }


}
