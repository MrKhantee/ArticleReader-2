package zsl.zhaoqing.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import zsl.zhaoqing.com.articlereader.R;
import zsl.zhaoqing.com.model.ImageAndText;
import zsl.zhaoqing.com.model.ItemCoach;
import zsl.zhaoqing.com.utility.AsynImageLoader;

/**
 * Created by Administrator on 2015/11/11.
 */
public class ArticleListAdapter extends ArrayAdapter<ImageAndText> {

    private ListView listView;
    private AsynImageLoader asynImageLoader;

    public ArticleListAdapter(Context context, ListView listView, List<ImageAndText> objects) {
        super(context, 0, objects);
        this.listView = listView;
        asynImageLoader = new AsynImageLoader();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
        View view = convertView;
        ItemCoach itemCoach;
        if (view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.item_view, null);
            itemCoach = new ItemCoach(view);
            view.setTag(itemCoach);
        } else {
            itemCoach = (ItemCoach) view.getTag();
        }
        ImageAndText imageAndText = getItem(position);
        String imageUrl = imageAndText.getImageUrl();
        ImageView imageView = itemCoach.getImageItem();
        imageView.setTag(imageUrl);
        Drawable image = asynImageLoader.imageLoad(imageUrl, new AsynImageLoader.FinishLoadingListener() {
            @Override
            public void loadImage(String imageUrl, Drawable drawable) {
                ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
                if (imageViewByTag != null) {
                    imageViewByTag.setImageDrawable(drawable);
                }
            }
        });
        if (image == null) {
            imageView.setImageResource(R.drawable.loading);
        } else {
            imageView.setImageDrawable(image);
        }
        TextView label = itemCoach.getLabelItem();
        label.setText(imageAndText.getLabel());
        return view;
    }
}
