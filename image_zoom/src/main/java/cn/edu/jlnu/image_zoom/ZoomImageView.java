package cn.edu.jlnu.image_zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/4/10.
 */

public class ZoomImageView extends ImageView {

    private  Matrix matrix=new Matrix();
    public Matrix initializationMatrix=new Matrix();
    public ZoomTouchListener mListener;
    Bitmap bitmap;
    private float scale;

    public ZoomImageView(Context context) {
        this(context,null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
       this(context,attrs,0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        Log.e("liuchen","初始化");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bitmap!=null){
            canvas.drawBitmap(bitmap,matrix,null);
        }
    }

    /***
     * 初始化matric
     * @param bitmap
     */
    public void initMatrix(Bitmap bitmap){
        if(bitmap!=null){
            this.bitmap=bitmap;
            scale=getWidth()*1.0f/bitmap.getWidth(); //缩放为imgaeView的宽度/bitmap的宽度
            matrix.reset();
            matrix.setScale(scale,scale);
            float scaledHeight=bitmap.getHeight()*scale; //获取缩放的scaleHeight
            if(scaledHeight<getHeight()){ //如果缩放的scaleHeight<imageView的height
                float dy=getHeight()-scaledHeight;
                matrix.postTranslate(0,dy/2); //移动
            }
            initializationMatrix.set(matrix);
        }
    }

    /***
     * 获取初始化bitmapHeight
     * @return
     */
    public float getInitializationBitmapHeight(){
        return bitmap==null?0:bitmap.getHeight()*scale;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        initMatrix(bm);
        super.setImageBitmap(bm);
    }

    @Override
    public Matrix getImageMatrix() {
        return matrix;
    }

    /***
     * 重置imageMatrix
     */
    public void resetImageMatrix(){
        this.matrix=initializationMatrix; //还原成原始的imageMatrix
        //TODO　listener
        mListener.resetToMinStatus();
        invalidate();
    }

    /***
     * 设置imageMatrix
     * @param matrix
     */
    @Override
    public void setImageMatrix(Matrix matrix) {
        this.matrix=matrix;
        invalidate();
    }

}
