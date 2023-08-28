package com.oilpalm3f.mainapp.uihelper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;


//Action to happen when screen is scrolled
public class InteractiveScrollView extends ScrollView {

    private static final String TAG = InteractiveScrollView.class.getName();
    public OnBottomReachedListener mListener;
    public OnTopReachedListener mListener2;
    public OnScrollingListener mListener3;

    public InteractiveScrollView(Context context) {
        super(context);
    }

    public InteractiveScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InteractiveScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InteractiveScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount()-1);
        int diff = (view.getBottom()-(getHeight()+getScrollY()));
        //Log.d(TAG, "onScrollChanged: difference "+diff+"..."+getScrollY());
        if (diff == 0 || diff <= 16 && mListener != null) {
            mListener.onBottomReached();
        } else if (getScrollY() == 0 && mListener2 != null) {
            mListener2.onTopReached();
        } else if (diff >= 200){
            mListener3.onScrolling();
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }


    public OnBottomReachedListener getOnBottomReachedListener() {
        return mListener;
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        mListener = onBottomReachedListener;
    }

    public OnTopReachedListener getOnTopReachedListener() {
        return mListener2;
    }

    public void setOnTopReachedListener(
            OnTopReachedListener onTopReachedListener) {
        mListener2 = onTopReachedListener;
    }

    public void setOnScrollingListener(
            OnScrollingListener onScrollingListener) {
        mListener3 = onScrollingListener;
    }

    /**
     * Event listener.
     */
    public interface OnBottomReachedListener{
        public void onBottomReached();
    }

    public interface OnTopReachedListener{
        public void onTopReached();
    }

    public interface OnScrollingListener {
        public void onScrolling();
    }


}
