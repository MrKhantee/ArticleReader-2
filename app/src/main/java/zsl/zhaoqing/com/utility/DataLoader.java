package zsl.zhaoqing.com.utility;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import zsl.zhaoqing.com.model.ImageAndText;

/**
 * Created by Administrator on 2015/11/11.
 */
public class DataLoader {

    public final static String APIKEY = "a04c18ba00cbe10a85869af29cb0fabd";

    public static List<ImageAndText> readJsonToData(String response ){
        if (!TextUtils.isEmpty(response)){
            List<ImageAndText> dataSources = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.length() > 0 ){
                    for (int i = 0 ; i < jsonObject.length() - 2; i++){
                        JSONObject object = (JSONObject) jsonObject.get(i+"");
                        String imageUrl = object.getString("picUrl");
                        String label = object.getString("title");
                        String articleUrl = object.getString("url");
                        ImageAndText imageAndText = new ImageAndText(imageUrl,label,articleUrl);
                        dataSources.add(imageAndText);
                    }
                }
                return dataSources;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void sendRequestForData(final Context context,final String dataUrl,
                                          final OnFinishListener listener){
        if (!NetworkUtils.isNetworkConnection(context)){
            Toast.makeText(context,"网络不可用",Toast.LENGTH_SHORT).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(dataUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("apikey",APIKEY);
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null){
                            response.append(line);
                        }
                        if (listener != null) {
                            listener.onFinish(response.toString());
                        }
                    } catch (Exception e) {
                        if (listener != null){
                            listener.onError(e);
                        }
                    }finally {
                        if (connection != null){
                            connection.disconnect();
                        }
                    }
                }
            }).start();
        }

    }



    public interface OnFinishListener{
        void onFinish(String response);
        void onError(Exception e);
    }
}
