package cn.edu.jlnu.image_zoom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.path;

/**
 * Created by Administrator on 2017/4/10.
 */

public class ImageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    ZoomImageViewPager vp;
    ImageView[] postionGuide;
    public static int url_path=0; //网络请求
    public static int local_file_path=1; //本地请求
    int pathType; //路径类型
    private Toolbar toobar;
    private Button btnSave;
    private TextView displayText;
    private ArrayList<String> imageUrls;
    private int globalPos=0;
    public static Map<Integer,Bitmap> bigBitmapCache=new HashMap<>();

    /**
     *
     * @param c
     * @param imageUrls 路径
     * @param defaultPos //从第pos个开始显示
     * @param pathType //路径类型：网络或者本地
     * @return
     */
    public static Intent getMyStartIntent(Context c, ArrayList<String> imageUrls,int defaultPos,int pathType){
        Intent intent=new Intent(c,ImageActivity.class);
        intent.putExtra("pos",defaultPos);
        intent.putExtra("pathType",pathType);
        intent.putStringArrayListExtra("imageUrls",imageUrls);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        toobar= (Toolbar) findViewById(R.id.toolbar);
        toobar.setTitle("预览");
        toobar.setTitleTextColor(Color.WHITE);
        toobar.setBackgroundColor(Color.parseColor("#20211c"));
        StatusBarUtil.setColor(ImageActivity.this, Color.parseColor("#20211c"),30);

        btnSave= (Button) toobar.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO save image
                Log.e("liuchen",globalPos+"");
                final Bitmap bitmap= bigBitmapCache.get(globalPos);
                if(bitmap!=null){
                    Log.e("liuchen",bitmap.toString());
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", "description");
                    boolean success=saveImageToGallery(ImageActivity.this,bitmap);
                    if(success){
                        Toast.makeText(ImageActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ImageActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ImageActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                }

//                final Bitmap b = bigBitmapsCache.get(pos);
//                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", "description");
//                    Toast.makeText(ImageActivity.this,"保存成功：",Toast.LENGTH_SHORT).show();
               }
        });
        setSupportActionBar(toobar);

        displayText= (TextView) findViewById(R.id.display_text);


        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        int pos=getIntent().getIntExtra("pos",0); //默认为0
        globalPos=pos;
        pathType=getIntent().getIntExtra("pathType",url_path);
        imageUrls=getIntent().getStringArrayListExtra("imageUrls");
        displayText.setText(String.format("%1$d/%2$d", pos+1,imageUrls.size()));
        vp= (ZoomImageViewPager) findViewById(R.id.vp);
        vp.setAdapter(new MyViewPagerAdapter(ImageActivity.this,imageUrls,pathType));
        vp.setOnPageChangeListener(this);
        vp.setOffscreenPageLimit(5);
        vp.setCurrentItem(pos);
    }



    public  boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "lovejlnu");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("liuchen","FileNotFoundException");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e("liuchen","IOException");
            e.printStackTrace();
            return false;
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            Log.e("liuchen","FileNotFoundException");
            e.printStackTrace();
            return false;
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {

        globalPos=position;
        displayText.setText(String.format("%1$d/%2$d", position+1,imageUrls.size()));
        vp.resetImageMatrix();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    @Override
    protected void onDestroy() {
        for (int i : bigBitmapCache.keySet()) {
            if (bigBitmapCache.get(i) != null) {
                bigBitmapCache.get(i).recycle();
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
