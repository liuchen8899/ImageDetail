package cn.edu.jlnu.image_zoom;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/4/10.
 */

public class ZoomTouchListener {

    ZoomImageView iv;

    public ZoomTouchListener(ZoomImageView iv) {
        this.iv = iv;
    }
    private int mode=0;
    private static final int MODE_DRAG=1;
    private static final int MODE_ZOOM=2;
    float total_scale=1,current_scale;
    private float canDragToRightDistance,canDragToLeftDistance,canDragToTopDistance,canDragToBottomDistance;
    private PointF actionDownPoint=new PointF(); //点击的点
    private PointF dragPoint=new PointF(); //拖拽点
    private Matrix matrixNow=new Matrix(); //matrix之前
    private Matrix matrixBefore=new Matrix(); //matrix之后
    private float startDis;
    //两个手指的中间点
    private PointF midPoint=new PointF(0,0);

    /***
     * 多点触控
     * @param event
     * @return
     */
    public boolean onTouch(MotionEvent event){
        switch (event.getAction()&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mode=MODE_DRAG;
                matrixBefore.set(iv.getImageMatrix());
                matrixNow.set(iv.getImageMatrix());
                dragPoint.set(event.getX(),event.getY()); //点击点为这个
                actionDownPoint.set(event.getX(),event.getY()); //sctionDown点为这个
                break;
            case MotionEvent.ACTION_MOVE: //如果动作为move
                if(mode==MODE_DRAG){ //如果是拖拽模式
                    float dx=event.getX()-dragPoint.x; //当前点的x坐标减去上一次点的x坐标
                    float dy=event.getY()-dragPoint.y; //当前点的y坐标减去上一次点的y坐标
                    dragPoint.set(event.getX(),event.getY());
                    if(checkXDragValid(dx)){
                        canDragToRightDistance-=dx;
                        canDragToLeftDistance+=dx;
                        if(checkYDragValid(dy)){
                            canDragToBottomDistance-=dy;
                            canDragToTopDistance+=dy;
                        }else{
                            dy=0;
                        }
                    }else{
                        return false;
                    }
                }else if(mode==MODE_ZOOM){ //如果是缩放模式
                    float endDis=distance(event); //记录当前两点的距离
                    midPoint=mid(event); //记录中间点
                    if(endDis>10f){ //如果两点之间的距离足够大
                        current_scale=endDis/startDis; //缩放倍数
                        total_scale*=current_scale;
                        //边界检测，如果左边有空边，那么mid.x就是0，覆盖做空边
                        if(canDragToRightDistance<=0&&canDragToLeftDistance>0){
                            midPoint.x=0;
                        }
                        //如果右边有空边，那么mid.x就是imageView的宽度，覆盖右空边
                        if(canDragToLeftDistance<=0&&canDragToRightDistance>0){
                            midPoint.x=iv.getMeasuredWidth();
                        }
                        //重置拖拽距离
                        resetDragDistance(current_scale);
                        matrixNow.postScale(current_scale,current_scale,midPoint.x,midPoint.y);
                        iv.setImageMatrix(matrixNow);
                    }
                    startDis=distance(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mode==MODE_DRAG){
                    checkClick(event.getX(),event.getY(),actionDownPoint.x,actionDownPoint.y);
                }
                mode=0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                checkZoomValid(); //看是否可以缩放
                mode=0;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode=MODE_ZOOM;
                startDis=distance(event);
                if(startDis>10f){
                    matrixBefore.set(iv.getImageMatrix());
                    matrixNow.set(iv.getImageMatrix());
                }
                break;

        }
        return true;
    }

    private boolean checkZoomValid() {
        if(mode==MODE_ZOOM){
            if(total_scale>2){
                resetToMaxStatus();
                matrixNow.set(iv.initializationMatrix);
                matrixNow.postScale(2,2,midPoint.x,midPoint.y);
                return false;
            }else if(total_scale<1){
                resetToMinStatus();
                matrixNow.set(iv.initializationMatrix);
                iv.setImageMatrix(matrixNow);
                return false;
            }
        }
        return true;
    }

    public void resetToMinStatus() {
        total_scale=1;
        canDragToBottomDistance=0;
        canDragToLeftDistance=0;
        canDragToRightDistance=0;
        canDragToTopDistance=0;
        resetDragDistance(total_scale);
    }

    public void resetToMaxStatus() {
        total_scale=2;
        canDragToRightDistance=0;
        canDragToBottomDistance=0;
        canDragToTopDistance=0;
        canDragToLeftDistance=0;
        resetDragDistance(total_scale);
    }

    /***
     * 确定是否是click
     * @param x
     * @param y
     * @param x1
     * @param y1
     */
    private boolean checkClick(float x, float y, float x1, float y1) {
        float x_d=Math.abs(x-x1);
        float y_d=Math.abs(y-y1);
        if(x_d<10&&y_d<10){
//            Activity activity= (Activity) iv.getContext();
//            activity.finish();
            return true;
        }
        return false;
    }

    private void resetDragDistance(float scale) {
        canDragToRightDistance=(midPoint.x+canDragToRightDistance)*scale-midPoint.x;
        canDragToLeftDistance=(iv.getMeasuredWidth()-midPoint.x+canDragToLeftDistance)*scale-(iv.getMeasuredWidth()-midPoint.x);
        if(total_scale*iv.getInitializationBitmapHeight()>iv.getMeasuredHeight()){
            canDragToBottomDistance=(midPoint.y+canDragToBottomDistance)*scale-midPoint.y;
            canDragToTopDistance=(iv
            .getMeasuredHeight()-midPoint.y+canDragToTopDistance)*scale-(iv.getMeasuredHeight()-midPoint.y);
        }else{
            canDragToBottomDistance=canDragToTopDistance=0;
        }
    }

    /***
     * 算出两点之间的距离
     * @param event
     * @return
     */
    private float distance(MotionEvent event) {
        float dx=event.getX(1)-event.getX(0);
        float dy=event.getY(1)-event.getY(0);
        return (float) Math.sqrt(dx*dx+dy*dy);
    }

    /***
     * 算出两点之间的中心点
     * @param event
     * @return
     */
    private PointF mid(MotionEvent event){
        float midX=(event.getX(1)+event.getX(0))/2;
        float midY=(event.getY(1)+event.getY(0))/2;
        return new PointF(midX,midY);
    }

    /***
     * 判断y是否可以拖动
     * @param dy
     * @return
     */
    private boolean checkYDragValid(float dy) {
        if(mode==MODE_DRAG){
            if(dy>0){ //向下
                return canDragToBottom(dy);
            }else{ //向上
                return canDragToTop(dy);
            }
        }
        return false;
    }

    /***
     * 判断y是否可以拖拽带顶部
     * @param dy
     * @return
     */
    private boolean canDragToTop(float dy) {
        if(canDragToTopDistance>Math.abs(dy)){
            return true;
        }
        return false;
    }

    /***
     * 判断y是否可以拖拽到底部
     * @param dy
     * @return
     */
    private boolean canDragToBottom(float dy) {
        if(canDragToBottomDistance>dy){
            return true;
        }
        return false;
    }

    /***
     * 判断x是否可以拖动
     * @param dx
     * @return
     */
    private boolean checkXDragValid(float dx) {
        if(mode==MODE_DRAG){
            if(dx>0){ //向右
                return canDragToRight(dx);
            }else{ //向左
                return canDragToLeft(dx);
            }
        }
        return false;
    }

    /***
     * 拖拽到左面
     * @param dx
     * @return
     */
    private boolean canDragToLeft(float dx) {
        if(canDragToLeftDistance>Math.abs(dx)){
            return true;
        }
        return false;
    }

    /***
     * 可以拖拽到右面
     * @param dx
     * @return
     */
    private boolean canDragToRight(float dx) {
        if(canDragToRightDistance>dx){
            return true;
        }
        return false;
    }

}
