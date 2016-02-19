package zsl.zhaoqing.com.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2015/11/10.
 */
public class NetworkUtils {

    //检查设备的网络是否可以使用，网络可用返回true
    public static boolean isNetworkConnection(Context context){
        //获取系统的连接服务管理实例
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取当前可用网络的信息
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null){
            if (info.getState() == NetworkInfo.State.CONNECTED)
                return true;
        }
        return false;
    }

}
