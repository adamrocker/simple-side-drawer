/*
 * Copyright (C) 2012 Masahiko Adachi(http://www.adamrocker.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navdrawer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * <p>This class enables to add a NavDrawer simply.</p>
 * <p>How to use:</p>
 * <p>After calling setContentView method in onCreate method, call 2 methods bellow.</p>
 * <ul>
 *     <li>- New SimpleNavDrawer instance</li>
 *     <li>- Set a layout file which will be set in the NavDrawer.</li>
 * </ul> 
 * <pre class="prettyprint">
 * public void onCreate(Bundle data) {
 *     super.onCreate(data);
 *     setContentView(R.layout.main);
 *     SimpleNavDrawer nav = new SimpleNavDrawer(this);
 *     nav.setBehindContentView(R.layout.manu);
 * }
 * </pre>
 * @author Masahiko Adachi
  */
public class SimpleSideDrawer extends FrameLayout {
    private final Window mWindow;
    private Scroller mScroller;
    private ViewGroup mAboveView;
    private BehindFrameLayout mBehindView;
    private Rect mPaddingRect;
    private View mOverlay;
    private float mLastMotionX;
    private int mDuration;
    private int mBehindViewWidth;
    private boolean mDraggable;
    private boolean mOpening = false;
    
    
    /**
     * <p>The default Interpolator of drawer animation is AccelerateDecelerateInterpolator.</p>
     * <p>The default animation duration is 230msec.</p>
     * @see SimpleNavDrawer(Activity act, Interpolator ip, int duration);
     * @param act
     */
    public SimpleSideDrawer(Activity act) {
        this(act, new AccelerateDecelerateInterpolator(), 230);
    }
    
    public SimpleSideDrawer(Activity act, Interpolator ip, int duration) {
        super(act.getApplicationContext());
        final Context context = act.getApplicationContext();
        mDuration = duration;
        mWindow = act.getWindow();
        mScroller = new Scroller(context, ip);
        
        final int fp = LayoutParams.FILL_PARENT;
        final int wp = LayoutParams.WRAP_CONTENT;
        //behind
        mBehindView = new BehindFrameLayout(context);
        mBehindView.setLayoutParams(new FrameLayout.LayoutParams(wp, fp));
        addView(mBehindView);
        
        //above
        mAboveView = new FrameLayout(context);
        mAboveView.setLayoutParams(new FrameLayout.LayoutParams(fp, fp));
        mOverlay = new OverlayView(getContext());
        mOverlay.setLayoutParams(new FrameLayout.LayoutParams(fp, fp, Gravity.BOTTOM));
        mOverlay.setEnabled(true);
        mOverlay.setVisibility(View.GONE);
        mOverlay.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                close();
            }
        });
        
        ViewGroup decor = (ViewGroup) mWindow.getDecorView();
        ViewGroup above = (ViewGroup) decor.getChildAt(0);//including actionbar
        decor.removeView(above);
        above.setBackgroundDrawable(decor.getBackground());
        mAboveView.removeAllViews();
        mAboveView.addView(above);
        mAboveView.addView(mOverlay);
        decor.addView(this);
        
        addView(mAboveView);
    }
    
    /**
     * <p>Set the behind view layout.</p>
     * <p>Call this method after setting the main content view by calling setContentView().</p>
     * @param behindLayout The layout id which is under the res/layout directory
     * @return The view which will be created from the layout id.
     * {@hide}
     */
    public View setBehindContentView(int behindLayout) {
        FrameLayout framelayout = new FrameLayout(getContext());
        View behindMenu = new View(getContext());
        
        ViewGroup decor = (ViewGroup) mWindow.getDecorView();
        ViewGroup above = (ViewGroup) decor.getChildAt(0);
        decor.removeView(above);
        framelayout.addView(behindMenu);
        framelayout.addView(above);
        
        decor.addView(framelayout);
        
        above.setBackgroundDrawable(decor.getBackground());
        mAboveView.removeAllViews();
        mAboveView.addView(above);
        mAboveView.addView(mOverlay);
        decor.addView(this);
        
        
        
        mBehindView.removeAllViews();
        View content = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(behindLayout, mBehindView);
        mPaddingRect = new Rect(content.getPaddingLeft(), content.getPaddingTop(), content.getPaddingRight(), content.getPaddingBottom());
        //mBehindViewContent = act.getLayoutInflater().inflate(behindLayout, mBehindView);
        return content;
    }
    
    /**
     * Change the side scroll interpolator
     * @param ip Interpolator object
     */
    public void setScrollInterpolator(Interpolator ip) {
        mScroller = new Scroller(getContext(), ip);
    }
    
    /**
     * Change the duration time of scrolling
     * @param msec The duration time should be milli-second
     */
    public void setAnimationDuration(int msec) {
        mDuration = msec;
    }
    
    /**
     * Close the behind view by swiping left the front view.
     */
    public void close() {
        int curX = mAboveView.getScrollX();
        mScroller.startScroll(curX, 0, -curX, 0, mDuration);
        invalidate();
    }
    
    /**
     * Open the behind view by swiping right the front view
     */
    public void open() {
        int curX = mAboveView.getScrollX();
        mScroller.startScroll(curX, 0, -mBehindViewWidth, 0, mDuration);
        invalidate();
    }
    
    /**
     * If the behind view is opened, close it. If the behind view is closed, open it.
     */
    public void toggleDrawer() {
        if (isClosed()) {
            open();
        } else {
            close();
        }
    }

    /**
     * Check the current status of the behind view
     * @return
     */
    public boolean isClosed() {
        return mAboveView != null && mAboveView.getScrollX() == 0;
    }
    
    /**
     * Need to adjust the behind view height
     * {@hide}
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBehindViewWidth = mBehindView.getChildAt(0).getMeasuredWidth();

        //adjust the behind display area
        ViewGroup decor = (ViewGroup) mWindow.getDecorView();
        Rect rect = new Rect();
        decor.getWindowVisibleDisplayFrame(rect);
        mBehindView.fitDisplay(rect);
    }
    
    /**
     * Side scroll animation
     * {@hide}
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mAboveView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            if (mAboveView.getScrollX() == 0) {
                mOverlay.setVisibility(View.GONE);
            } else {
                mOverlay.setVisibility(View.VISIBLE);
            }
        }
    }
    
    /**
     * {@hide}
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
        case MotionEvent.ACTION_DOWN:
        {
            float x = ev.getX();
            mLastMotionX = x;
            mDraggable = -(mAboveView.getScrollX()) < x;
            break;
        }
        case MotionEvent.ACTION_UP:
        {
            if (mDraggable) {
                int currentX = mAboveView.getScrollX();
                int diffX = 0;
                if (mOpening) {
                    diffX = -mBehindViewWidth - currentX;
                } else {
                    diffX = -currentX;
                }
                mScroller.startScroll(currentX, 0, diffX, 0, mDuration);
                invalidate();
            }
            break;
        }
        case MotionEvent.ACTION_MOVE:
            if (!mDraggable) return false;
            
            float newX = ev.getX();
            float diffX = -(newX - mLastMotionX);
            int x = mAboveView.getScrollX();
            {
                mOpening = mLastMotionX < newX;
                if (Math.abs(diffX) < 3) {
                    mOpening = mBehindViewWidth / 2 < -x;
                }
            }
            mLastMotionX = newX;
            float nextX = x + diffX;
            if (0 < nextX) {
                mAboveView.scrollTo(0, 0);
            } else {
                if (nextX < -mBehindViewWidth) {
                    mAboveView.scrollTo(-mBehindViewWidth, 0);
                } else {
                    mAboveView.scrollBy((int) diffX, 0);
                }
            }
            break;
        }
        return true;
    }
    
    private class BehindFrameLayout extends FrameLayout {

        public BehindFrameLayout(Context context) {
            super(context);
        }
        
        /**
         * Adjust the behind view
         * @param rect The display area
         */
        public void fitDisplay(Rect rect) {
            //fitSystemWindows(rect); /* do not use this function because this is not available on Android2.x */
            View v = mBehindView.getChildAt(0);
            v.setPadding(mPaddingRect.left, mPaddingRect.top + rect.top, mPaddingRect.right, mPaddingRect.bottom);
            requestLayout();
        }
    }
    
    /**
     * Overlay view only when the behind menu is appeared.
     * This view control scrolling the above view  
     * @author Masahiko Adachi
     */
    private class OverlayView extends View {
        private static final float CLICK_RANGE = 3;
        private float mDownX;
        private float mDownY;
        private OnClickListener mClickListener;
        public OverlayView(Context context) {
            super(context);
        }
        
        public void setOnClickListener(OnClickListener listener) {
            mClickListener = listener;
            super.setOnClickListener(listener);
        }
        
        public boolean onTouchEvent(MotionEvent ev) {
            ev.setLocation(ev.getX() - mAboveView.getScrollX(), 0);
            SimpleSideDrawer.this.onTouchEvent(ev);
            int action = ev.getAction() & MotionEvent.ACTION_MASK;
                float x = ev.getX();
                float y = ev.getY();
            if (action == MotionEvent.ACTION_DOWN) {
                mDownX = x;
                mDownY = y;
            } else if (action == MotionEvent.ACTION_UP) {
                if (mClickListener != null) {
                    if (Math.abs(mDownX - x) < CLICK_RANGE && Math.abs(mDownY - y) < CLICK_RANGE) {
                        mClickListener.onClick(this);
                    }
                }
            }
            return true;
        }
    }
}