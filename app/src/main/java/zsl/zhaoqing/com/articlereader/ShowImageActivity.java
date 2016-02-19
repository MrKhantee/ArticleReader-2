package zsl.zhaoqing.com.articlereader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import zsl.zhaoqing.com.utility.AsynImageLoader;

/**
 * Created by Administrator on 2015/11/21.
 */
public class ShowImageActivity extends Activity implements GestureDetector.OnGestureListener {

    private AsynImageLoader loader;
    private ImageSwitcher imageSwitcher;
    private ImageButton saveImage;
    private Drawable image;
    private String[] src;
    private int index = 0;
    private File file = null;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image);
        imageSwitcher = (ImageSwitcher) findViewById(R.id.show_webimage_imageview);
        saveImage = (ImageButton) findViewById(R.id.save_image);
        loader = new AsynImageLoader();
        detector = new GestureDetector(this, this);
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");;
        src = intent.getStringArrayExtra("image");
        for (int i = 0; i < src.length; i++){
            if (imageUrl.equals(src[i])){
                index = i;
            }
        }
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(ShowImageActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        updateImage(loader,imageUrl);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSuccess = saveImage();
                if (isSuccess){
                    Toast.makeText(ShowImageActivity.this,"保存图片成功"+file.getParent(),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ShowImageActivity.this,"没有存储空间",Toast.LENGTH_SHORT).show();
                }
                saveImage.setVisibility(View.GONE);
            }
        });
    }

    private void updateImage(AsynImageLoader loader , String imageUrl) {
        Drawable drawable = loader.imageLoad(imageUrl, new AsynImageLoader.FinishLoadingListener() {
            @Override
            public void loadImage(String imageUrl, Drawable drawable) {
                if (drawable != null){
                    imageSwitcher.setImageDrawable(drawable);
                }else {
                    Toast.makeText(ShowImageActivity.this,"加载图片失败！",Toast.LENGTH_SHORT).show();;
                }
            }
        });
        if (drawable != null){
            imageSwitcher.setImageDrawable(drawable);
        }else {
            imageSwitcher.setImageResource(R.drawable.default_image);
        }
    }

    /**
     * 保存图片
     * @return
     */
    public boolean saveImage(){
        FileOutputStream os = null;
        boolean flag = false;
        BitmapDrawable drawable = (BitmapDrawable) image;
        Bitmap bitmap = drawable.getBitmap();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES),String.valueOf(System.currentTimeMillis())+".jpg");
        }else {
            return flag;
        }
        try {
            os = new FileOutputStream(file);
            flag = bitmap.compress(Bitmap.CompressFormat.JPEG,100, os);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (saveImage.getVisibility() == View.VISIBLE){
            saveImage.setVisibility(View.GONE);
        }else {
            finish();
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        saveImage.setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(0.1f,1.0f);
        animation.setDuration(500);
        saveImage.setAnimation(animation);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (saveImage.getVisibility() == View.VISIBLE){
            saveImage.setVisibility(View.GONE);
        }
        if (e1.getX() - e2.getX() > 50){
            index = index + 1;
            if (index >= src.length){
                return false;
            }
        }
        else if (e1.getX() - e2.getX() < 50){
            index = index - 1;
            if (index < 0){
                return false;
            }
        }
        updateImage(loader,src[index]);
        return false;
    }
}
