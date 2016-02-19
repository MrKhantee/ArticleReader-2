package zsl.zhaoqing.com.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

import zsl.zhaoqing.com.model.AppInfo;

/**
 * Created by Administrator on 2015/12/17.
 */
public class ShareUtils {

    public static List<AppInfo> getShareList(Context context){
        List<AppInfo> appInfoList = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = getShareAppList(pm);
        if (list == null){
            return null;
        }else {
            for (ResolveInfo info : list){
                AppInfo appInfo = new AppInfo();
                appInfo.setAppPkgName(info.activityInfo.packageName);
                appInfo.setAppLauncherClassName(info.activityInfo.name);
                appInfo.setAppName(info.loadLabel(pm).toString());
                appInfo.setAppIcon(info.loadIcon(pm));
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }

    public static List<ResolveInfo> getShareAppList(PackageManager pm){
        List<ResolveInfo> infoList = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_SEND,null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        infoList = pm.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return infoList;
    }
}
