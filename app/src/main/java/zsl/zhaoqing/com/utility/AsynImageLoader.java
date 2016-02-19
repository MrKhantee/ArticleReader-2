package zsl.zhaoqing.com.utility;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import java.io.InputStream;
import java.net.URL;

/**
 * 图片下载工具类，用于下载及缓存图片
 */
public class AsynImageLoader {

    //缓存图片的链接和下载的图片
    private LruCache<String,Drawable> cacheDrawable;

    public AsynImageLoader() {
        long maxcache = Runtime.getRuntime().maxMemory() / 1024;
        cacheDrawable = new LruCache<String,Drawable>((int) (maxcache / 8)){
            @Override
            protected int sizeOf(String key, Drawable value) {
                return ((BitmapDrawable)value).getBitmap().getByteCount() / 1024;
            }
        };
    }

    public Drawable imageLoad(final String imageUrl , final FinishLoadingListener listener ){
        //判断imageCoach对象中的key值（图片链接）所对应的value是否已经存有图片
        Drawable drawable = getDrawableFromCache(imageUrl);
        if (drawable != null){
            return drawable;
        }
        //处理线程发送来的图片
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                listener.loadImage(imageUrl, (Drawable) msg.obj);
            }
        };

        //开启线程下载图片，图片下载后存储到imageCoach对象，并发送到处理器去处理图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = loadImageFromUrl(imageUrl);
                if (drawable == null){
                    return;
                }
                addDrawableToCache(imageUrl,drawable);
                Message msg = handler.obtainMessage(0 , drawable);
                handler.sendMessage(msg);
            }
        }).start();
        return null;
    }

    //用于下载图片的方法
    public static Drawable loadImageFromUrl(String imageUrl){
        URL m;
        InputStream in = null;
        Drawable drawable = null;
        try {
            m = new URL(imageUrl);
            in = (InputStream) m.getContent();
            drawable= Drawable.createFromStream(in , "src");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    public void addDrawableToCache(String key, Drawable drawable){
        if (getDrawableFromCache(key) == null){
            cacheDrawable.put(key,drawable);
        }
    }

    public Drawable getDrawableFromCache(String key){
        return cacheDrawable.get(key);
    }

    //图片下载完毕后回调该接口的实例
    public interface FinishLoadingListener{
        public void loadImage(String imageUrl , Drawable drawable);
    }
}
