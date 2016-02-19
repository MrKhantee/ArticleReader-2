package zsl.zhaoqing.com.articlereader;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import zsl.zhaoqing.com.model.AppInfo;
import zsl.zhaoqing.com.utility.AsynImageLoader;
import zsl.zhaoqing.com.utility.NetworkUtils;
import zsl.zhaoqing.com.utility.ShareUtils;
import zsl.zhaoqing.com.utility.Util;

/**
 * Created by Administrator on 2015/11/10.
 */
@SuppressLint("SetJavaScriptEnabled")
public class ArticleDetailActivity extends Activity {

    private static final String APP_ID = "wx33c80bf9e03e492a";
    private IWXAPI api;
    private String articleUrl;
    private String titleLabel;
    private String imageUrl;
    private Bitmap bitmap = null;

    private WebView articleDetailView;
    private ActionBar actionBar;
    private ImageButton backButton;
    private ImageButton shareButton;
    private TextView title;
    private ProgressBar progressBar;
    private PopupWindow popupWindow = null;
    private FrameLayout video;
    private View xCustomView;
    private MyChromeClient myChromeClient;
    private WebChromeClient.CustomViewCallback customViewCallback;
    //AppID：wx33c80bf9e03e492a
    //AppSecret：d4624c36b6795d1d99dcf0547af5443d

    /**
     * 更新progressBar
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x10) {
                progressBar.setProgress((Integer) msg.obj);
                if (msg.obj == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    };

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.article_detail);
        if (!NetworkUtils.isNetworkConnection(this)){
            Toast.makeText(this,"请检查网络！",Toast.LENGTH_SHORT).show();
            return;
        }
        video = (FrameLayout) findViewById(R.id.video);
        Intent intent = getIntent();
        articleUrl = intent.getStringExtra("articleUrl");
        titleLabel = intent.getStringExtra("title");
        imageUrl = intent.getStringExtra("imageUrl");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = AsynImageLoader.loadImageFromUrl(imageUrl);
                if (drawable != null){
                    Bitmap mBitmap = ((BitmapDrawable)drawable).getBitmap();
                    bitmap = Bitmap.createScaledBitmap(mBitmap,100,100,false);
                }
            }
        }).start();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        initActionBar(titleLabel);
        initWebView();
        regToWx();

    }

    private void initWebView() {
        articleDetailView = (WebView) findViewById(R.id.article_detail);
        WebSettings settings = articleDetailView.getSettings();
        //设置加载页面时是否阻塞图片的加载
        settings.setBlockNetworkImage(false);
        //设置js是否可用
        settings.setJavaScriptEnabled(true);
        //设置插件是否可用
        settings.setPluginState(WebSettings.PluginState.ON);
        //启用或禁止WebView访问文件数据
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        // 排版适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //settings.setUseWideViewPort(true);设置webview推荐使用的窗口
        settings.setLoadWithOverviewMode(true);
        //设置为true,防止加载页面视频时出现空白
        settings.setDomStorageEnabled(true);
        articleDetailView.loadUrl(articleUrl);
        articleDetailView.addJavascriptInterface(new JavascriptInterface(this), "lister");
        myChromeClient = new MyChromeClient();
        articleDetailView.setWebChromeClient(myChromeClient);
        articleDetailView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setJavaScriptEnabled(true);
                super.onPageFinished(view, url);
                addImageClickListener();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.getSettings().setJavaScriptEnabled(true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            articleDetailView.getClass().getMethod("onResume").invoke(articleDetailView, (Objects[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            articleDetailView.getClass().getMethod("onPause").invoke(articleDetailView, (Objects[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (articleDetailView != null)
            articleDetailView.clearCache(true);
            articleDetailView.destroy();
    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }

    private void shareToWx() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = articleUrl;

        WXMediaMessage message = new WXMediaMessage(webpage);
        message.title = titleLabel;
        message.description = articleUrl;
        if (bitmap != null) {
            message.thumbData = Util.bmpToByteArray(bitmap, true);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = message;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);

    }

    private void initActionBar(String titleLabel) {
        actionBar = getActionBar();
        //设置actionBar显示自定义View
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_detail);
        title = (TextView) actionBar.getCustomView().findViewById(R.id.title);
        shareButton = (ImageButton) actionBar.getCustomView().findViewById(R.id.share_button);
        backButton = (ImageButton) actionBar.getCustomView().findViewById(R.id.back_icon);
        title.setText(titleLabel);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleDetailActivity.this.finish();
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareToWx();
                initShareList(shareButton);
            }
        });
    }

    /**
     * 注入到网页中的脚本，实现图片点击
     */
    private void addImageClickListener() {
        articleDetailView.loadUrl("javascript:(function(){"
                + "var obj = document.getElementsByTagName(\"img\"); "
                + "var images = new Array(obj.length);"
                + "for(var i=0;i<obj.length;i++)  "
                + "{"
                +"     images[i] = obj[i].src;"
                + "    obj[i].onclick=function()  "
                + "    {  "
                + "        window.lister.openImage(images,this.src);  "
                + "    }  "
                + "}"
                + "})()");
    }

    /**
     * 暴露给Webview的对象，实现图片点击时跳转到showImageActivity
     */
    public class JavascriptInterface {
        private Context context;
        private String[] imageUrls;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String[] src, String imageUrl) {

            Intent intent = new Intent();
            intent.putExtra("image", src);
            intent.putExtra("imageUrl",imageUrl);
            intent.setClass(context, ShowImageActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * 初始化分享列表
     * @param parent 弹出窗口所依赖的父视图
     */
    public void initShareList(View parent) {
        View view = null;
        ListView shareList = null;
        if (popupWindow == null) {
            view = LayoutInflater.from(ArticleDetailActivity.this).inflate(R.layout.popup_list, null);
            shareList = (ListView) view.findViewById(R.id.popup_list);
            List<AppInfo> list = ShareUtils.getShareList(ArticleDetailActivity.this);
            final List<AppInfo> apps = handShareListData(list);
            ShareAdapter adapter = new ShareAdapter(ArticleDetailActivity.this, apps);
            shareList.setAdapter(adapter);
            popupWindow = new PopupWindow(view, 300, LinearLayout.LayoutParams.WRAP_CONTENT);
            shareList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (apps.get(position).getAppName().equals("发送到朋友圈")) {
                        shareToWx();
                        popupWindow.dismiss();
                    }
                }
            });
        }
        //使其聚焦
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //xoff,yoff基于anchor的左下角进行偏移。正数表示下方右边，负数表示（上方左边）
        //showAsDropDown(parent, xPos, yPos);
        popupWindow.showAsDropDown(parent, -200, 0);
    }

    /**
     * 从收集到的具有分享功能的app信息中赛选出本app需要的分享app信息
     * @param lists 收集到的具有分享功能的app列表
     * @return 本app需要的app列表
     */
    public List<AppInfo> handShareListData(List<AppInfo> lists) {
        List<AppInfo> infos = new ArrayList();
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getAppName().equals("发送给朋友")) {
                lists.get(i).setAppName("发送到朋友圈");
                infos.add(lists.get(i));
                lists.clear();
                break;
            }
        }
        return infos;
    }

    /**
     * 实现分享列表的adapter
     */
    class ShareAdapter extends ArrayAdapter<AppInfo> {

        List<AppInfo> list = null;

        public ShareAdapter(Context context, List<AppInfo> objects) {
            super(context, 0, objects);
            list = objects;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(ArticleDetailActivity.this).inflate(R.layout.popup_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.icon_app);
            TextView textView = (TextView) view.findViewById(R.id.text_app);
            imageView.setImageDrawable(list.get(position).getAppIcon());
            textView.setText(list.get(position).getAppName());
            return view;
        }

        @Override
        public AppInfo getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    class MyChromeClient extends WebChromeClient{
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Message msg = handler.obtainMessage(0x10, newProgress);
                handler.sendMessage(msg);
            }
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                getActionBar().hide();
                articleDetailView.setVisibility(View.GONE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                if (xCustomView != null){
                    callback.onCustomViewHidden();
                    return;
                }
                video.addView(view);
                customViewCallback = callback;
                xCustomView = view;
                video.setVisibility(View.VISIBLE);
            }

            @Override
            public void onHideCustomView() {
                getActionBar().show();
                if (xCustomView == null){
                    return;
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                final WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().setAttributes(attrs);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                xCustomView.setVisibility(View.GONE);
                video.removeView(xCustomView);
                xCustomView = null;
                customViewCallback.onCustomViewHidden();
                articleDetailView.setVisibility(View.VISIBLE);
            }
    }

    /**
     * 判断是否处于全屏状态
     * @return
     */
    public boolean inCustomView() {
        return (xCustomView != null);
    }

    /**
     * 隐藏全屏
     */
    public void hideCustomView() {
        myChromeClient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                hideCustomView();
                return true;
            } else {
                //articleDetailView.loadUrl("about:blank");
                this.finish();
            }
        }
        return false;
    }

}
