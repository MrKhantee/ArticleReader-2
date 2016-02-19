package zsl.zhaoqing.com.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import zsl.zhaoqing.com.articlereader.R;

/**
 * 一个列表项中所用到的控件缓存
 */
public class ItemCoach {

    //缓存整个列表项的视图
    private View itemViewCoach;
    //缓存列表项的图片控件
    private ImageView imageItem;
    //缓存链表项的标题控件
    private TextView labelItem;


    public ItemCoach(View itemViewCoach) {
        this.itemViewCoach = itemViewCoach;
    }

    public ImageView getImageItem(){
        if (imageItem == null){
            imageItem = (ImageView) itemViewCoach.findViewById(R.id.image_item);
        }
        return imageItem;
    }

    public TextView getLabelItem(){
        if (labelItem == null){
            labelItem = (TextView) itemViewCoach.findViewById(R.id.label_item);
        }
        return labelItem;
    }
}
