package cn.edu.jlnu.imagedetail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import cn.edu.jlnu.image_zoom.ImageActivity;
import cn.edu.jlnu.image_zoom.ZoomImageView;

public class MainActivity extends AppCompatActivity {

    private ZoomImageView mImageView;
    private Toolbar toobar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<String> strings = new ArrayList<>();
//        strings.add(R.mipmap.a+"");
//        strings.add(R.mipmap.b+"");
//        strings.add(R.mipmap.c+"");
//        startActivity(ImageActivity.getMyStartIntent(this,strings,0, ImageActivity.local_file_path));
        toobar= (Toolbar) findViewById(cn.edu.jlnu.image_zoom.R.id.toolbar);
        toobar.setTitle("预览");
        toobar.setTitleTextColor(Color.WHITE);
        toobar.setBackgroundColor(Color.parseColor("#20211c"));
        setSupportActionBar(toobar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        strings.add("http://www.jlnu.edu.cn/upload/DSC_3290-041010.jpg");
        strings.add("http://www.jlnu.edu.cn/upload/DSC_3359%2004100001.jpg");
        strings.add("http://www.jlnu.edu.cn/upload/040917-3.jpg");
        strings.add("http://www.jlnu.edu.cn/upload/040917-4.jpg");
        startActivity(ImageActivity.getMyStartIntent(this,strings,2, ImageActivity.url_path));
    }
}
