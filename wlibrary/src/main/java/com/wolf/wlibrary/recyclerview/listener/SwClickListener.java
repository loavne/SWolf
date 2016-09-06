package com.wolf.wlibrary.recyclerview.listener;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.wolf.wlibrary.recyclerview.SwAdapter;
import com.wolf.wlibrary.recyclerview.SwViewHolder;

import java.util.Iterator;
import java.util.Set;

/**
 * <p>Description: </p>
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-05 16:39
 */
public abstract class SwClickListener implements RecyclerView.OnItemTouchListener{
    private GestureDetectorCompat mGestureDetector;
    private RecyclerView recyclerView;
    private Set<Integer> childClickViewIds;
    private Set<Integer> longClickViewIds;
    protected SwAdapter mSwAdapter;
    public static String TAG = "SwClickListener";
    private boolean mIsPrepressed = false;
    private boolean mIsShowPress = false;
    private View mPressedView = null;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        if (recyclerView == null) {
            this.recyclerView = rv;
            this.mSwAdapter = (SwAdapter) recyclerView.getAdapter();
            mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener(recyclerView));
        }
        if (!mGestureDetector.onTouchEvent(e) && e.getActionMasked() == MotionEvent.ACTION_UP && mIsShowPress) {
            mPressedView.setPressed(false);
            mIsShowPress = false;
            mIsPrepressed = false;
            mPressedView = null;

        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.e(TAG, "onTouchEvent: ");
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {

        private RecyclerView recyclerView;

        @Override
        public boolean onDown(MotionEvent e) {
            mIsPrepressed = true;
            mPressedView = recyclerView.findChildViewUnder(e.getX(), e.getY());
            super.onDown(e);
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            if (mIsPrepressed && mPressedView != null) {
                mPressedView.setPressed(true);
                mIsShowPress = true;
            }
            super.onShowPress(e);
        }

        public ItemTouchHelperGestureListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mIsPrepressed && mPressedView != null) {
                mPressedView.setPressed(true);
                final View pressedView = mPressedView;
                SwViewHolder vh = (SwViewHolder) recyclerView.getChildViewHolder(pressedView);

                if (isHeaderOrFooterPosition(vh.getLayoutPosition())) {
                    return false;
                }
                childClickViewIds = vh.getChildClickViewIds();

                if (childClickViewIds != null && childClickViewIds.size() > 0) {
                    for (Iterator it = childClickViewIds.iterator(); it.hasNext(); ) {
                        View childView = pressedView.findViewById((Integer) it.next());
                        if (inRangeOfView(childView, e)) {
                            onItemChildClick(mSwAdapter, childView, vh.getLayoutPosition() - mSwAdapter.getHeaderLayoutCount());
                            resetPressedView(pressedView);
                            return true;
                        }
                    }


                    onItemClick(mSwAdapter, pressedView, vh.getLayoutPosition() - mSwAdapter.getHeaderLayoutCount());
                } else {
                    onItemClick(mSwAdapter, pressedView, vh.getLayoutPosition() - mSwAdapter.getHeaderLayoutCount());
                }
                resetPressedView(pressedView);

            }
            return true;
        }

        private void resetPressedView(final View pressedView) {
            pressedView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pressedView.setPressed(false);
                }
            }, 100);
            mIsPrepressed = false;
            mPressedView = null;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            boolean isChildLongClick =false;
            if (mIsPrepressed && mPressedView != null) {
                SwViewHolder vh = (SwViewHolder) recyclerView.getChildViewHolder(mPressedView);
                if (!isHeaderOrFooterPosition(vh.getLayoutPosition())) {
                    longClickViewIds = vh.getItemChildLongClickViewIds();
                    if (longClickViewIds != null && longClickViewIds.size() > 0) {
                        for (Iterator it = longClickViewIds.iterator(); it.hasNext(); ) {
                            View childView = mPressedView.findViewById((Integer) it.next());
                            if (inRangeOfView(childView, e)) {
                                onItemChildLongClick(mSwAdapter, childView, vh.getLayoutPosition() - mSwAdapter.getHeaderLayoutCount());
                                mPressedView.setPressed(true);
                                mIsShowPress = true;
                                isChildLongClick =true;
                                break;
                            }
                        }
                    }
                    if (!isChildLongClick){
                        onItemLongClick(mSwAdapter, mPressedView, vh.getLayoutPosition() - mSwAdapter.getHeaderLayoutCount());
                        mPressedView.setPressed(true);
                        mIsShowPress = true;
                    }

                }

            }
        }


    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    public abstract void onItemClick(SwAdapter adapter, View view, int position);

    /**
     * callback method to be invoked when an item in this view has been
     * click and held
     *
     * @param view     The view whihin the AbsListView that was clicked
     * @param position The position of the view int the adapter
     * @return true if the callback consumed the long click ,false otherwise
     */
    public abstract void onItemLongClick(SwAdapter adapter, View view, int position);

    public abstract void onItemChildClick(SwAdapter adapter, View view, int position);

    public abstract void onItemChildLongClick(SwAdapter adapter, View view, int position);

    public boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getRawX() < x
                || ev.getRawX() > (x + view.getWidth())
                || ev.getRawY() < y
                || ev.getRawY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    private boolean isHeaderOrFooterPosition(int position) {
        /**
         *  have a headview and EMPTY_VIEW FOOTER_VIEW LOADING_VIEW
         */
        int type = mSwAdapter.getItemViewType(position);
        return (type == mSwAdapter.EMPTY_VIEW || type == mSwAdapter.HEADER_VIEW || type == mSwAdapter.FOOTER_VIEW || type == mSwAdapter.LOADING_VIEW);
    }
}
