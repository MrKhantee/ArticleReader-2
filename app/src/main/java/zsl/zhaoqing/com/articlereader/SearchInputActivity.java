package zsl.zhaoqing.com.articlereader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/11/14.
 */
public class SearchInputActivity extends Activity {

    private EditText input;
    private ImageView search;
    private ImageButton backButton;
    private String result;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        intent = getIntent();
        initActionBar();
        input = (EditText) findViewById(R.id.input);
        search = (ImageView) findViewById(R.id.search);
        backButton = (ImageButton) findViewById(R.id.back_icon);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = input.getText().toString();
                if (result != null && !TextUtils.isEmpty(result)){
                    intent.putExtra("result",result);
                    setResult(0x12, intent);
                    finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar =getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search_input);
    }
}
