package cn.edu.jlnu.image_zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/10.
 */

public class ZoomImageViewPager extends ViewPager {

    private MyViewPagerAdapter adapter;

    public ZoomImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        this.adapter= (MyViewPagerAdapter) adapter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        MyViewPagerAdapter.ViewHolder tag=getCurrentViewHolder();
        boolean consume=tag.content_iv.mListener.onTouch(ev);
        if(!consume||ev.getAction()!=MotionEvent.ACTION_MOVE){
            super.onTouchEvent(ev);
        }else{
            resetLastMotionX(ev.getX());
        }
        return true;
    }



    private void resetLastMotionX(float x) {
        try {
            Field mLastMotionX = ViewPager.class.getDeclaredField("mLastMotionX");
            mLastMotionX.setAccessible(true);
            mLastMotionX.set(this,x);

            Field mInitialMotionX = ViewPager.class.getDeclaredField("mInitialMotionX");
            mInitialMotionX.setAccessible(true);
            mInitialMotionX.set(this,x);
        } catch (Exception e) {
        }
    }

    public void resetImageMatrix(){
        MyViewPagerAdapter.ViewHolder tag=getCurrentViewHolder();
        tag.content_iv.resetImageMatrix();
    }

    /***
     * 获取当前ViewHolder
     * @return
     */
    private MyViewPagerAdapter.ViewHolder getCurrentViewHolder() {
        View item=adapter.getItem(getCurrentItem());
        MyViewPagerAdapter.ViewHolder tag= (MyViewPagerAdapter.ViewHolder) item.getTag();
        return tag;
    }

    /**
     * Retrieve the current adapter supplying pages.
     *
     * @return The currently registered PagerAdapter
     */
    @Override
    public PagerAdapter getAdapter() {
        return adapter;
    }
}
