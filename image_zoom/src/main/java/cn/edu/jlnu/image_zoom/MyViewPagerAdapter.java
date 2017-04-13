package cn.edu.jlnu.image_zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static cn.edu.jlnu.image_zoom.ImageActivity.bigBitmapCache;
import static cn.edu.jlnu.image_zoom.ImageActivity.local_file_path;
import static cn.edu.jlnu.image_zoom.ImageActivity.url_path;

/**
 * Created by Administrator on 2017/4/12.
 */

class MyViewPagerAdapter extends PagerAdapter {

    ArrayList<String> imageUrls;
    private Context context;
    private int pathType;
    List<View> pagerViews=new ArrayList<>();


    public MyViewPagerAdapter(Context context,ArrayList<String> imageUrls,int pathType) {
        this.context=context;
        this.imageUrls=imageUrls;
        this.pathType=pathType;
        for(String pic:imageUrls){
            View view=View.inflate(context,R.layout.image_detail_lay,null);
            ViewHolder holder=new ViewHolder();
            initView(holder,view);
            pagerViews.add(view);
        }
    }

    private void initView(ViewHolder holder, View view) {
        holder.content_iv= (ZoomImageView) view.findViewById(R.id.content_iv);
        holder.content_iv.mListener=new ZoomTouchListener(holder.content_iv); //设置监听
        holder.pb= (ProgressBar) view.findViewById(R.id.pb);
        view.setTag(holder);
    }

    @Override
    public int getCount() {
        return imageUrls==null?0:imageUrls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view=pagerViews.get(position);
        final ViewHolder holder= (ViewHolder) view.getTag();
        holder.setPos(position);
        if(pathType==url_path){
            setBitmapAsUrl(holder);
        }else{
            setBitmap(holder);
        }
        container.addView(view);
        return view;
    }

    private void setBitmap(final ViewHolder holder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap tempBitmap=null;
                if(bigBitmapCache.get(holder.pos)==null){
                    tempBitmap= BitmapFactory.decodeResource(context.getResources(),Integer.parseInt(imageUrls.get(holder.pos)));
                    final Bitmap bitmap=tempBitmap;
                    holder.content_iv.post(new Runnable() {
                        @Override
                        public void run() {
                            if(bitmap==null){

                            }else{
                                bigBitmapCache.put(holder.pos,bitmap);
                                holder.content_iv.setImageBitmap(bitmap);

                            }
                            holder.pb.setVisibility(GONE);
                        }
                    });
                }else{
                    holder.content_iv.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.content_iv.setImageBitmap(bigBitmapCache.get(holder.pos));
                            holder.pb.setVisibility(GONE);
                        }
                    });
                }
            }
        }).start();
    }

    public Bitmap getBitmap(int pos){
        return bigBitmapCache.get(pos);
    }

    private void setBitmapAsUrl(final ViewHolder vh) {
        Log.e("liuchen", "setBitmapAsUrl" + imageUrls.get(vh.pos));
        Log.e("liuchen", "需要加载网络图片");
        final String url=imageUrls.get(vh.pos);

        if(bigBitmapCache.get(vh.pos)==null){
            Glide.with( context.getApplicationContext() ) // could be an issue!
                    .load(url)
                    .asBitmap()   //强制转换Bitmap
                    .into(new SimpleTarget<Bitmap>() {

                        @Override
                        public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Log.e("liuchen","获取图片成功："+resource.toString());
                            vh.content_iv.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (resource == null) {
                                    } else {
                                        vh.content_iv.setImageBitmap(resource);
                                        bigBitmapCache.put(vh.pos,resource);
                                    }
                                    vh.pb.setVisibility(View.GONE);
                                }
                            });
                        }


                    });
        }else{
            vh.content_iv.post(new Runnable() {
                @Override
                public void run() {
                    vh.content_iv.setImageBitmap(bigBitmapCache.get(vh.pos));
                    vh.pb.setVisibility(GONE);
                }
            });
        }


    }

    private void setPreviewBitmap(ViewHolder holder, int type) {
        if(pathType==url_path){
            Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
            if(bitmap!=null){
                holder.content_iv.setImageBitmap(bitmap);
            }
        }
    }

    /***
     * 删除view
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        container.removeView(pagerViews.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Parcelable saveState() {
        return super.saveState();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }
    public View getItem(int position) {
        return pagerViews.get(position);
    }

    public class ViewHolder{
        int pos;
        public ZoomImageView content_iv;
        ProgressBar pb;
        public void setPos(int pos){
            this.pos=pos;
        }
    }
}
