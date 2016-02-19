package zsl.zhaoqing.com.articlereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * app启动时显示动画效果
 * Created by Administrator on 2015/11/10.
 */
public class LoadingActivity extends Activity {

    private ImageView loadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);
        loadView = (ImageView) findViewById(R.id.loading_view);
        // 设置动画的属性
        AlphaAnimation animation = new AlphaAnimation(0.8f,1.0f);
        animation.setDuration(1000);
        loadView.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(LoadingActivity.this,ArticleListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
