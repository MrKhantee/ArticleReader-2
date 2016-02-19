package zsl.zhaoqing.com.articlereader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import zsl.zhaoqing.com.adapter.ArticleListAdapter;
import zsl.zhaoqing.com.model.ImageAndText;
import zsl.zhaoqing.com.utility.DataLoader;
import zsl.zhaoqing.com.utility.NetworkUtils;

/**
 * Created by Administrator on 2015/11/10.
 */
public class ArticleListActivity extends Activity {

    //数据下载成功SUCCESS，数据下载失败 FAIL
    public final static int FAIL = 0;
    public final static int SUCCESS = 1;
    private final static String HTTPURL = "http://apis.baidu.com/txapi/weixin/wxhot?num=50";
    //进度条的颜色
    int[] colors = {R.color.blue,R.color.green,R.color.yellow,R.color.gray};

    //UI组件
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private TextView title;
    private ImageView searchButton;

    private ArticleListAdapter adapter;
    private List<ImageAndText> imageAndTextList;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshLayout.setRefreshing(false);
            switch (msg.what){
                case SUCCESS:
                    List<ImageAndText> list = (List<ImageAndText>) msg.obj;
                    if (list != null && list.size() > 0){
                        if (imageAndTextList != null){
                            imageAndTextList.clear();
                            imageAndTextList.addAll(list);
                            adapter.notifyDataSetChanged();
                        }else {
                            imageAndTextList = list;
                            adapter = new ArticleListAdapter(ArticleListActivity.this, listView, imageAndTextList);
                            listView.setAdapter(adapter);
                        }
                    }else {
                        Toast.makeText(ArticleListActivity.this,"搜索到0条相关内容",Toast.LENGTH_SHORT).show();;
                    }
                    break;
                case FAIL:
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(ArticleListActivity.this,"加载失败，请检查网络！",Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list_view);
        initActionBar();
        initWidget();
        showList(HTTPURL);
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        //设置actionBar使用自定义View
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_list);

        title = (TextView) actionBar.getCustomView().findViewById(R.id.title);
        searchButton = (ImageView) actionBar.getCustomView().findViewById(R.id.search);
        title.setText(getResources().getString(R.string.list_title));
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticleListActivity.this, SearchInputActivity.class);
                startActivityForResult(intent, 0x11);
            }
        });
    }

    private void initWidget() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        listView = (ListView) findViewById(R.id.article_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageAndText imageAndText = imageAndTextList.get(position);
                String articleUrl = imageAndText.getArticleUrl();
                String title = imageAndText.getLabel();
                String imageUrl = imageAndText.getImageUrl();
                Intent intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
                intent.putExtra("articleUrl", articleUrl);
                intent.putExtra("title", title);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
            }
        });
        refreshLayout.setColorSchemeResources(colors);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showList(HTTPURL);
            }
        });
    }

    private void showList(String url){
        if(!NetworkUtils.isNetworkConnection(this)){
            refreshLayout.setRefreshing(false);
            Toast.makeText(ArticleListActivity.this,"加载失败，请检查网络！",Toast.LENGTH_SHORT).show();
            return;
        }
        DataLoader.sendRequestForData(ArticleListActivity.this, url, new DataLoader.OnFinishListener() {
            @Override
            public void onFinish(String response) {
                List<ImageAndText> imageAndTexts = DataLoader.readJsonToData(response);
                Message msg = handler.obtainMessage(SUCCESS, imageAndTexts);
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(FAIL);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x11 && resultCode == 0x12){
            String result = data.getStringExtra("result");
            String url = HTTPURL + "&word=" + result;
            showList(url);
        }
    }




}
